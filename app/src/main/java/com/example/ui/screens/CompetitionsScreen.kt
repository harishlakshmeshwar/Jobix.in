package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.CompetitionItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionsScreen(
    competitions: List<CompetitionItem>,
    registeredIds: Set<String>,
    onToggleRegister: (CompetitionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCompForDetails by remember { mutableStateOf<CompetitionItem?>(null) }

    val filteredList = remember(competitions, selectedTab, registeredIds) {
        when (selectedTab) {
            1 -> competitions.filter { registeredIds.contains(it.id) }
            2 -> competitions.filter { it.type.contains("Hackathon", ignoreCase = true) }
            3 -> competitions.filter { it.type.contains("Coding", ignoreCase = true) }
            else -> competitions
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "Competitions & Hackathons",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Showcase your technical prowess, win grants, and earn sponsor interviews",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Contests") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Registered (${registeredIds.size})") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Hackathons") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Coding") }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No competitions in this category yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredList) { comp ->
                    val isRegistered = registeredIds.contains(comp.id)
                    CompetitionCard(
                        competition = comp,
                        isRegistered = isRegistered,
                        onRegister = { onToggleRegister(comp) },
                        onViewDetails = { selectedCompForDetails = comp }
                    )
                }
            }
        }
    }

    selectedCompForDetails?.let { comp ->
        AlertDialog(
            onDismissRequest = { selectedCompForDetails = null },
            title = {
                Text(comp.title, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Organized by ${comp.organizer}", fontWeight = FontWeight.SemiBold, color = JobixBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(comp.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Eligibility: ${comp.eligibility}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Prize Pool: ${comp.prizePool}", style = MaterialTheme.typography.bodySmall, color = JobixEmerald, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                val isRegistered = registeredIds.contains(comp.id)
                Button(
                    onClick = {
                        onToggleRegister(comp)
                        selectedCompForDetails = null
                    }
                ) {
                    Text(if (isRegistered) "Withdraw Registration" else "Register Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCompForDetails = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun CompetitionCard(
    competition: CompetitionItem,
    isRegistered: Boolean,
    onRegister: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comp_card_${competition.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = JobixViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = competition.type.uppercase(),
                        color = JobixViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = JobixRose
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = competition.deadline,
                        color = JobixRose,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = competition.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = "By ${competition.organizer}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Prize Pool", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(competition.prizePool, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black), color = JobixEmerald)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Registered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${competition.participantsCount}+ Techies", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Details & Rules")
                }

                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comp_register_btn_${competition.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRegistered) JobixEmerald else JobixBlue
                    )
                ) {
                    Icon(
                        imageVector = if (isRegistered) Icons.Default.Check else Icons.Default.AppRegistration,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRegistered) "Registered" else "Register")
                }
            }
        }
    }
}
