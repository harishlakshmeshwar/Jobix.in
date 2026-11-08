package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    userProfile: UserProfile,
    activeRole: String,
    onRoleChange: (String) -> Unit,
    applications: List<ApplicationItem>,
    savedJobs: List<SavedJobItem>,
    allJobs: List<JobItem>,
    auditLogs: List<AdminAuditLog>,
    onSaveProfile: (UserProfile) -> Unit,
    onCreateJob: (JobItem) -> Unit,
    onUpdateApplicationStatus: (appId: String, status: String, notes: String) -> Unit,
    onLogAdminAction: (adminName: String, action: String, target: String, reason: String) -> Unit,
    onSelectJob: (JobItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var seekerSubTab by remember { mutableIntStateOf(0) }
    var recruiterSubTab by remember { mutableIntStateOf(0) }
    var adminSubTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header with Current Active Role
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (activeRole) {
                                "recruiter" -> "Recruiter Hub"
                                "admin" -> "Admin Control Panel"
                                else -> "Career Workspace"
                            },
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                        )
                        Text(
                            text = "Signed in as ${userProfile.name.ifBlank { "Jobix Member" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Role switch selector
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.testTag("role_segmented_buttons")
                    ) {
                        SegmentedButton(
                            selected = activeRole == "seeker",
                            onClick = { onRoleChange("seeker") },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                        ) {
                            Text("Seeker", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = activeRole == "recruiter",
                            onClick = { onRoleChange("recruiter") },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                        ) {
                            Text("Recruiter", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = activeRole == "admin",
                            onClick = { onRoleChange("admin") },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                        ) {
                            Text("Admin", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Tabs within the chosen role
                when (activeRole) {
                    "seeker" -> {
                        PrimaryTabRow(selectedTabIndex = seekerSubTab) {
                            Tab(selected = seekerSubTab == 0, onClick = { seekerSubTab = 0 }, text = { Text("Applications (${applications.size})") })
                            Tab(selected = seekerSubTab == 1, onClick = { seekerSubTab = 1 }, text = { Text("Saved (${savedJobs.size})") })
                            Tab(selected = seekerSubTab == 2, onClick = { seekerSubTab = 2 }, text = { Text("Profile & Resume") })
                        }
                    }
                    "recruiter" -> {
                        PrimaryTabRow(selectedTabIndex = recruiterSubTab) {
                            Tab(selected = recruiterSubTab == 0, onClick = { recruiterSubTab = 0 }, text = { Text("Candidate Pipeline") })
                            Tab(selected = recruiterSubTab == 1, onClick = { recruiterSubTab = 1 }, text = { Text("Post New Job") })
                            Tab(selected = recruiterSubTab == 2, onClick = { recruiterSubTab = 2 }, text = { Text("My Listings") })
                        }
                    }
                    "admin" -> {
                        PrimaryTabRow(selectedTabIndex = adminSubTab) {
                            Tab(selected = adminSubTab == 0, onClick = { adminSubTab = 0 }, text = { Text("Overview & Stats") })
                            Tab(selected = adminSubTab == 1, onClick = { adminSubTab = 1 }, text = { Text("Moderation & Users") })
                            Tab(selected = adminSubTab == 2, onClick = { adminSubTab = 2 }, text = { Text("Audit Trail") })
                        }
                    }
                }
            }
        }

        // Sub-screen content
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeRole) {
                "seeker" -> {
                    when (seekerSubTab) {
                        0 -> SeekerApplicationsTab(applications = applications)
                        1 -> SeekerSavedJobsTab(savedJobs = savedJobs, allJobs = allJobs, onSelectJob = onSelectJob)
                        2 -> SeekerProfileTab(userProfile = userProfile, onSaveProfile = onSaveProfile)
                    }
                }
                "recruiter" -> {
                    when (recruiterSubTab) {
                        0 -> RecruiterPipelineTab(
                            applications = applications,
                            onUpdateStatus = onUpdateApplicationStatus
                        )
                        1 -> RecruiterPostJobTab(onCreateJob = {
                            onCreateJob(it)
                            recruiterSubTab = 2
                        })
                        2 -> RecruiterListingsTab(allJobs = allJobs, onSelectJob = onSelectJob)
                    }
                }
                "admin" -> {
                    when (adminSubTab) {
                        0 -> AdminOverviewTab(
                            totalUsers = 4850,
                            activeJobsCount = allJobs.size,
                            applicationsCount = applications.size
                        )
                        1 -> AdminModerationTab(
                            onLogAdminAction = onLogAdminAction
                        )
                        2 -> AdminAuditLogsTab(auditLogs = auditLogs)
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------
// SEEKER TABS
// --------------------------------------------------------------------------------

@Composable
fun SeekerApplicationsTab(applications: List<ApplicationItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (applications.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.WorkHistory, contentDescription = null, modifier = Modifier.size(48.dp), tint = JobixBlue)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No applications submitted yet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Explore jobs or internships and apply with one tap.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(applications) { app ->
                ApplicationStatusCard(application = app)
            }
        }
    }
}

@Composable
fun ApplicationStatusCard(application: ApplicationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("application_item_${application.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(application.jobTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(application.companyName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = JobixBlue)
                }

                Surface(
                    color = when (application.status) {
                        "Offered" -> JobixEmerald.copy(alpha = 0.2f)
                        "Interview" -> JobixAmber.copy(alpha = 0.2f)
                        "Shortlisted" -> JobixViolet.copy(alpha = 0.2f)
                        "Rejected" -> JobixRose.copy(alpha = 0.2f)
                        else -> JobixBlue.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = application.status,
                        color = when (application.status) {
                            "Offered" -> JobixEmerald
                            "Interview" -> JobixAmber
                            "Shortlisted" -> JobixViolet
                            "Rejected" -> JobixRose
                            else -> JobixBlue
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Stepper indicator
            ApplicationStepper(currentStatus = application.status)

            if (application.recruiterNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Recruiter Note: ${application.recruiterNotes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationStepper(currentStatus: String) {
    val steps = listOf("Applied", "Under Review", "Shortlisted", "Interview", "Offered")
    val currentStepIndex = steps.indexOf(currentStatus).takeIf { it >= 0 } ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index <= currentStepIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) JobixBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = step,
                    fontSize = 9.sp,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SeekerSavedJobsTab(
    savedJobs: List<SavedJobItem>,
    allJobs: List<JobItem>,
    onSelectJob: (JobItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (savedJobs.isEmpty()) {
            item {
                Text(
                    text = "No saved jobs. Bookmark jobs from the Explore page to review them here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(savedJobs) { saved ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(saved.jobTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(saved.companyName, style = MaterialTheme.typography.bodySmall, color = JobixBlue)
                            Text(saved.salary, style = MaterialTheme.typography.labelSmall, color = JobixEmerald)
                        }
                        Button(
                            onClick = {
                                val found = allJobs.find { it.id == saved.jobId }
                                if (found != null) onSelectJob(found)
                            }
                        ) {
                            Text("View & Apply")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeekerProfileTab(
    userProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit
) {
    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var headline by remember(userProfile) { mutableStateOf(userProfile.headline) }
    var bio by remember(userProfile) { mutableStateOf(userProfile.bio) }
    var education by remember(userProfile) { mutableStateOf(userProfile.education) }
    var experience by remember(userProfile) { mutableStateOf(userProfile.experience) }
    var portfolioUrl by remember(userProfile) { mutableStateOf(userProfile.portfolioUrl) }
    var isPublic by remember(userProfile) { mutableStateOf(userProfile.isPublic) }
    var newSkillInput by remember { mutableStateOf("") }
    val skillsList = remember(userProfile) { mutableStateListOf(*userProfile.skills.toTypedArray()) }
    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Completeness Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JobixNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile Completeness", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("85%", color = JobixCyan, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.85f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = JobixCyan,
                        trackColor = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Complete profile unlocks verified candidate badge for recruiters.",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = headline,
                onValueChange = { headline = it },
                label = { Text("Professional Headline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("About / Summary") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        item {
            OutlinedTextField(
                value = education,
                onValueChange = { education = it },
                label = { Text("Education") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = experience,
                onValueChange = { experience = it },
                label = { Text("Work Experience") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = portfolioUrl,
                onValueChange = { portfolioUrl = it },
                label = { Text("Portfolio / GitHub URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Skills Section
        item {
            Column {
                Text("Technical Skills", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newSkillInput,
                        onValueChange = { newSkillInput = it },
                        placeholder = { Text("Add skill (e.g. Kotlin)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newSkillInput.isNotBlank()) {
                                skillsList.add(newSkillInput.trim())
                                newSkillInput = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(skillsList) { skill ->
                        InputChip(
                            selected = true,
                            onClick = { skillsList.remove(skill) },
                            label = { Text(skill) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Public Profile Visibility", fontWeight = FontWeight.SemiBold)
                    Text("Allow verified employers to discover your resume", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isPublic, onCheckedChange = { isPublic = it })
            }
        }

        item {
            Button(
                onClick = {
                    val updated = userProfile.copy(
                        name = name,
                        headline = headline,
                        bio = bio,
                        education = education,
                        experience = experience,
                        portfolioUrl = portfolioUrl,
                        skills = skillsList.toList(),
                        isPublic = isPublic
                    )
                    onSaveProfile(updated)
                    showSavedMessage = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_profile_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JobixBlue)
            ) {
                Text("Save Profile Changes", fontWeight = FontWeight.Bold)
            }
        }

        if (showSavedMessage) {
            item {
                Surface(
                    color = JobixEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Profile successfully updated and synced with cloud!",
                        color = JobixEmerald,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------
// RECRUITER TABS
// --------------------------------------------------------------------------------

@Composable
fun RecruiterPipelineTab(
    applications: List<ApplicationItem>,
    onUpdateStatus: (appId: String, status: String, notes: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Applicant Review & Status Decisions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (applications.isEmpty()) {
            item {
                Text("No applications received yet. Post a job or wait for candidates to apply.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(applications) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recruiter_app_card_${app.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(app.applicantName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(app.applicantEmail, style = MaterialTheme.typography.bodySmall, color = JobixBlue)
                                Text("Applied for ${app.jobTitle}", style = MaterialTheme.typography.labelSmall)
                            }
                            Text(
                                text = app.status,
                                color = JobixViolet,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        if (app.coverNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Note: \"${app.coverNote}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Decision Action Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onUpdateStatus(app.id, "Shortlisted", "Shortlisted for Round 1") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("Shortlist", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onUpdateStatus(app.id, "Interview", "Interview scheduled for next Tuesday") },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("Interview", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onUpdateStatus(app.id, "Offered", "Congratulations on the offer!") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JobixEmerald),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("Offer", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onUpdateStatus(app.id, "Rejected", "Candidate qualifications do not match current requirements.") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JobixRose),
                                contentPadding = PaddingValues(2.dp)
                            ) {
                                Text("Reject", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecruiterPostJobTab(onCreateJob: (JobItem) -> Unit) {
    var title by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Bengaluru, Hybrid") }
    var jobType by remember { mutableStateOf("Full-time") }
    var salaryFormatted by remember { mutableStateOf("₹14 - 24 LPA") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("Kotlin, Compose, Coroutines") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Post a New Career Opportunity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title") },
                placeholder = { Text("e.g. Senior Android Architect") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recruiter_job_title_input")
            )
        }

        item {
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recruiter_company_input")
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = salaryFormatted,
                    onValueChange = { salaryFormatted = it },
                    label = { Text("Compensation") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Role Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("Key Requirements") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = skillsInput,
                onValueChange = { skillsInput = it },
                label = { Text("Required Skills (Comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (title.isNotBlank() && companyName.isNotBlank()) {
                        val skillsList = skillsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        val newJob = JobItem(
                            id = "job_${System.currentTimeMillis()}",
                            title = title,
                            companyName = companyName,
                            location = location,
                            jobType = jobType,
                            salaryFormatted = salaryFormatted,
                            description = description.ifBlank { "Exciting engineering role at $companyName" },
                            requirements = requirements.ifBlank { "Passionate engineer with domain fundamentals" },
                            skills = skillsList
                        )
                        onCreateJob(newJob)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_new_job_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = JobixBlue)
            ) {
                Text("Publish Listing", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecruiterListingsTab(allJobs: List<JobItem>, onSelectJob: (JobItem) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(allJobs) { job ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(job.companyName, style = MaterialTheme.typography.bodySmall, color = JobixBlue)
                        Text("${job.applicantsCount} Applicants", style = MaterialTheme.typography.labelSmall, color = JobixViolet, fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { onSelectJob(job) }) {
                        Text("Manage")
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------
// ADMIN TABS
// --------------------------------------------------------------------------------

@Composable
fun AdminOverviewTab(
    totalUsers: Int,
    activeJobsCount: Int,
    applicationsCount: Int
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JobixNavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Platform Security & Governance", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Super-admin permissions verified via backend claims.", color = JobixCyan, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Total Users", "$totalUsers", JobixBlue, Icons.Default.Group, Modifier.weight(1f))
                MetricCard("Active Listings", "$activeJobsCount", JobixViolet, Icons.Default.Work, Modifier.weight(1f))
                MetricCard("Applications", "$applicationsCount", JobixEmerald, Icons.Default.Send, Modifier.weight(1f))
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Operational Health", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Firestore Enterprise instance: Healthy", style = MaterialTheme.typography.bodySmall)
                    Text("• App Check & Firebase Auth: Enforcing Google Sign-In", style = MaterialTheme.typography.bodySmall)
                    Text("• Zero Trust Security Rules: Active & Enforced", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AdminModerationTab(
    onLogAdminAction: (adminName: String, action: String, target: String, reason: String) -> Unit
) {
    var actionFeedback by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Pending Recruiter Approvals", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("InnovateLabs India Pvt Ltd", fontWeight = FontWeight.Bold)
                            Text("Recruiter: Sandeep V. (sandeep@innovatelabs.ai)", style = MaterialTheme.typography.bodySmall, color = JobixBlue)
                        }
                        Surface(color = JobixAmber.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text("Pending Verification", color = JobixAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onLogAdminAction("Super Admin", "Approve Employer", "InnovateLabs India", "CIN & GST verified")
                                actionFeedback = "Approved InnovateLabs India credentials!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JobixEmerald),
                            modifier = Modifier.testTag("admin_approve_recruiter_btn")
                        ) {
                            Text("Approve Employer")
                        }
                        OutlinedButton(
                            onClick = {
                                onLogAdminAction("Super Admin", "Reject Employer", "InnovateLabs India", "Domain mismatch")
                                actionFeedback = "Rejected employer verification request."
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("Account Moderation & Suspensions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Reported Account: user_bot_9291", fontWeight = FontWeight.Bold)
                    Text("Reason: High frequency automated job applications", style = MaterialTheme.typography.bodySmall, color = JobixRose)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onLogAdminAction("Super Admin", "Suspend User Account", "user_bot_9291", "Automated spamming violation")
                            actionFeedback = "Account user_bot_9291 suspended."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JobixRose),
                        modifier = Modifier.testTag("admin_suspend_user_btn")
                    ) {
                        Text("Suspend Account with Cause")
                    }
                }
            }
        }

        if (actionFeedback.isNotBlank()) {
            item {
                Surface(
                    color = JobixBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionFeedback,
                        color = JobixBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAuditLogsTab(auditLogs: List<AdminAuditLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(auditLogs) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.action, fontWeight = FontWeight.Bold, color = JobixBlue)
                        Text(log.timestamp, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Target: ${log.target}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("Reason: ${log.reason}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Admin: ${log.adminName}", style = MaterialTheme.typography.labelSmall, color = JobixViolet)
                }
            }
        }
    }
}
