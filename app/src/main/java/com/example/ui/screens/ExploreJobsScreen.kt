package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobItem
import com.example.ui.theme.JobixBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreJobsScreen(
    jobs: List<JobItem>,
    savedJobIds: Set<String>,
    onSelectJob: (JobItem) -> Unit,
    onSaveJob: (JobItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("All") }
    var onlyRemote by remember { mutableStateOf(false) }
    var selectedExp by remember { mutableStateOf("All") }

    val types = listOf("All", "Full-time", "Internship", "Contract")
    val expLevels = listOf("All", "Freshers", "1-3 Yrs", "3+ Yrs")

    val filteredJobs = remember(jobs, searchQuery, selectedType, onlyRemote, selectedExp) {
        jobs.filter { job ->
            val matchesQuery = searchQuery.isBlank() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.companyName.contains(searchQuery, ignoreCase = true) ||
                    job.skills.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesType = selectedType == "All" || job.jobType.equals(selectedType, ignoreCase = true)

            val matchesRemote = !onlyRemote || job.location.contains("Remote", ignoreCase = true) || job.jobType.contains("Remote", ignoreCase = true)

            val matchesExp = selectedExp == "All" || when (selectedExp) {
                "Freshers" -> job.experienceLevel.contains("Entry", ignoreCase = true) || job.experienceLevel.contains("Fresher", ignoreCase = true) || job.jobType.equals("Internship", ignoreCase = true)
                "1-3 Yrs" -> job.experienceLevel.contains("1", ignoreCase = true) || job.experienceLevel.contains("2", ignoreCase = true)
                else -> job.experienceLevel.contains("3", ignoreCase = true) || job.experienceLevel.contains("5", ignoreCase = true)
            }

            matchesQuery && matchesType && matchesRemote && matchesExp
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Explore Careers",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Find full-time roles, internships, and remote tech openings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_bar"),
                    placeholder = { Text("Search by title, skill, or firm...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = JobixBlue)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter row 1: Type & Remote Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(types) { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JobixBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Remote", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = onlyRemote,
                            onCheckedChange = { onlyRemote = it },
                            modifier = Modifier.testTag("remote_only_switch")
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredJobs.size} Opportunities Found",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredJobs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matching opportunities",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Try adjusting your search keywords or clearing filters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredJobs) { job ->
                    val isSaved = savedJobIds.contains(job.id)
                    JobCardItem(
                        job = job,
                        isSaved = isSaved,
                        onSelect = { onSelectJob(job) },
                        onSave = { onSaveJob(job) }
                    )
                }
            }
        }
    }
}
