package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class NavigationTab(val title: String) {
    HOME("Home"),
    JOBS("Jobs"),
    NETWORK("Network"),
    COMPETITIONS("Compete"),
    DASHBOARD("Workspace")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobixTopBar(
    activeRole: String,
    onRoleChange: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenMessages: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenProfile() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(listOf(JobixBlue, JobixViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "J",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Jobix",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = ".in",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black
                            ),
                            color = JobixBlue
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(JobixCyan)
                        )
                    }
                    Text(
                        text = "Career & Talent Universe",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right Actions: Role Badge & Quick Switch, Messages, Sign Out
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Role Switcher Pill
                AssistChip(
                    onClick = {
                        val nextRole = when (activeRole) {
                            "seeker" -> "recruiter"
                            "recruiter" -> "admin"
                            else -> "seeker"
                        }
                        onRoleChange(nextRole)
                    },
                    label = {
                        Text(
                            text = when (activeRole) {
                                "recruiter" -> "Recruiter"
                                "admin" -> "Admin"
                                else -> "Seeker"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (activeRole) {
                                "recruiter" -> Icons.Default.BusinessCenter
                                "admin" -> Icons.Default.AdminPanelSettings
                                else -> Icons.Default.School
                            },
                            contentDescription = "Role Mode",
                            modifier = Modifier.size(16.dp),
                            tint = when (activeRole) {
                                "recruiter" -> JobixViolet
                                "admin" -> JobixRose
                                else -> JobixBlue
                            }
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("role_switcher_chip")
                )

                IconButton(
                    onClick = onOpenMessages,
                    modifier = Modifier.testTag("messages_button")
                ) {
                    BadgedBox(
                        badge = {
                            Badge { Text("2") }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Messages"
                        )
                    }
                }

                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Sign Out"
                    )
                }
            }
        }
    }
}

@Composable
fun JobixBottomNav(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            NavigationTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                            NavigationTab.JOBS -> if (isSelected) Icons.Filled.Work else Icons.Outlined.WorkOutline
                            NavigationTab.NETWORK -> if (isSelected) Icons.Filled.People else Icons.Outlined.PeopleOutline
                            NavigationTab.COMPETITIONS -> if (isSelected) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents
                            NavigationTab.DASHBOARD -> if (isSelected) Icons.Filled.Dashboard else Icons.Outlined.Dashboard
                        },
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = JobixBlue,
                    selectedTextColor = JobixBlue,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("bottom_nav_${tab.name.lowercase()}")
            )
        }
    }
}
