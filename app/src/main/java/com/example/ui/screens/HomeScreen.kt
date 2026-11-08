package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompetitionItem
import com.example.data.model.JobItem
import com.example.ui.components.Interactive3DCareerCanvas
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    jobs: List<JobItem>,
    competitions: List<CompetitionItem>,
    onSelectJob: (JobItem) -> Unit,
    onSaveJob: (JobItem) -> Unit,
    savedJobIds: Set<String>,
    onExploreAllJobs: () -> Unit,
    onExploreCompetitions: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Engineering", "Internships", "AI/ML", "Fintech", "Remote")

    val filteredJobs = remember(jobs, searchKeyword, selectedCategory) {
        jobs.filter { job ->
            val matchesKeyword = searchKeyword.isBlank() ||
                    job.title.contains(searchKeyword, ignoreCase = true) ||
                    job.companyName.contains(searchKeyword, ignoreCase = true) ||
                    job.skills.any { it.contains(searchKeyword, ignoreCase = true) }
            val matchesCategory = selectedCategory == "All" ||
                    (selectedCategory == "Internships" && job.jobType.equals("Internship", ignoreCase = true)) ||
                    (selectedCategory == "Remote" && (job.jobType.contains("Remote", ignoreCase = true) || job.location.contains("Remote", ignoreCase = true))) ||
                    job.category.contains(selectedCategory, ignoreCase = true) ||
                    job.skills.any { it.contains(selectedCategory, ignoreCase = true) }
            matchesKeyword && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero 3D Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                JobixNavyDark,
                                JobixNavyCard,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Tagline badge
                    Surface(
                        color = JobixBlue.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, JobixCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(JobixCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Next-Gen 3D Career Ecosystem",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Discover Opportunities.\nBuild Connections.\nShape Your Career.",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            lineHeight = 34.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Join 45,000+ graduates and professionals landing high-growth careers, hackathons, and company connections.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Orbital Canvas
                    Interactive3DCareerCanvas(
                        isDark = true,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Quick Search Bar
                    OutlinedTextField(
                        value = searchKeyword,
                        onValueChange = { searchKeyword = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        placeholder = { Text("Search by role, company, or skill (e.g. Kotlin, Google)...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = JobixBlue)
                        },
                        trailingIcon = {
                            if (searchKeyword.isNotEmpty()) {
                                IconButton(onClick = { searchKeyword = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = JobixBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Live Platform Impact Numbers
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Active Roles",
                    value = "2,840+",
                    accentColor = JobixBlue,
                    icon = Icons.Default.Work,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Top Tech Firms",
                    value = "180+",
                    accentColor = JobixViolet,
                    icon = Icons.Default.Domain,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Prize Grants",
                    value = "₹1.1 Cr+",
                    accentColor = JobixEmerald,
                    icon = Icons.Default.EmojiEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Categories Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JobixBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }
        }

        // Featured Jobs Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Featured Opportunities",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Curated high-growth tech positions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(
                    onClick = onExploreAllJobs,
                    modifier = Modifier.testTag("explore_all_jobs_btn")
                ) {
                    Text("View All", fontWeight = FontWeight.Bold, color = JobixBlue)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Job Cards
        items(filteredJobs.take(5)) { job ->
            val isSaved = savedJobIds.contains(job.id)
            JobCardItem(
                job = job,
                isSaved = isSaved,
                onSelect = { onSelectJob(job) },
                onSave = { onSaveJob(job) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Competitions & Hackathons Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onExploreCompetitions() }
                    .testTag("hackathons_banner_card"),
                colors = CardDefaults.cardColors(
                    containerColor = JobixNavyCard
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = JobixViolet.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "STUDENT & PRO ARENA",
                                color = JobixCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Smart India Hackathon",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Build Projects, Win Grants & Skip Standard Hiring Queues",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Participate in national coding contests and team hackathons with direct sponsor interview tracks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prize Pool: ₹1,00,00,000",
                            color = JobixEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        FilledTonalButton(
                            onClick = onExploreCompetitions,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = JobixBlue
                            )
                        ) {
                            Text("Explore Arenas", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun JobCardItem(
    job: JobItem,
    isSaved: Boolean,
    onSelect: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("job_card_${job.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Company Logo Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(JobixNavyDark, JobixBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = job.companyName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = job.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = job.companyName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = JobixBlue
                        )
                    }
                }

                IconButton(
                    onClick = onSave,
                    modifier = Modifier.testTag("save_job_btn_${job.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save Job",
                        tint = if (isSaved) JobixViolet else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Location, Job Type, Salary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                JobMetaBadge(icon = Icons.Outlined.LocationOn, text = job.location.substringBefore(","))
                JobMetaBadge(icon = Icons.Outlined.Schedule, text = job.jobType)
                JobMetaBadge(icon = Icons.Outlined.Payments, text = job.salaryFormatted, isAccent = true)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Skills chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                job.skills.take(3).forEach { skill ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = skill,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (job.skills.size > 3) {
                    Text(
                        text = "+${job.skills.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun JobMetaBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isAccent: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isAccent) JobixBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = if (isAccent) JobixBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isAccent) FontWeight.Bold else FontWeight.Normal),
                color = if (isAccent) JobixBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
