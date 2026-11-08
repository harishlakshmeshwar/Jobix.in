package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import com.example.data.model.*
import com.example.data.repository.JobixRepository
import com.example.ui.components.JobixBottomNav
import com.example.ui.components.JobixTopBar
import com.example.ui.components.NavigationTab
import com.example.ui.screens.*
import com.example.ui.theme.JobixTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobixTheme {
                JobixRootApp()
            }
        }
    }
}

@Composable
fun JobixRootApp() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val uid = currentUser!!.uid
        key(uid) {
            JobixMainScreen(currentUserId = uid)
        }
    }
}

@Composable
fun JobixMainScreen(currentUserId: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { JobixRepository.getInstance(context) }
    val credentialManager = remember { CredentialManager.create(context) }

    // Core Observables
    val userProfile by repository.observeUserProfile(currentUserId)
        .collectAsState(initial = UserProfile(userId = currentUserId, name = repository.currentUserName() ?: "Jobix Member", email = repository.currentUserEmail() ?: ""))

    val jobs by repository.observeJobs().collectAsState(initial = emptyList())
    val savedJobs by repository.observeSavedJobs(currentUserId).collectAsState(initial = emptyList())
    val applications by repository.observeUserApplications(currentUserId).collectAsState(initial = emptyList())
    val posts by repository.observePosts().collectAsState(initial = emptyList())
    val competitions = remember { repository.getCompetitions() }
    val registeredCompIds = remember { mutableStateListOf<String>() }

    // Navigation and Modals state
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var activeRole by remember { mutableStateOf("seeker") }
    var selectedJobForDetails by remember { mutableStateOf<JobItem?>(null) }
    var isMessagesOpen by remember { mutableStateOf(false) }

    val savedJobIds = remember(savedJobs) { savedJobs.map { it.jobId }.toSet() }
    val appliedJobIds = remember(applications) { applications.map { it.jobId }.toSet() }

    // Synchronize active role from user profile when loaded
    LaunchedEffect(userProfile) {
        userProfile?.let {
            if (it.role.isNotBlank() && activeRole == "seeker" && it.role != "seeker") {
                activeRole = it.role
            }
        }
    }

    BackHandler(enabled = isMessagesOpen || selectedJobForDetails != null || selectedTab != NavigationTab.HOME) {
        when {
            selectedJobForDetails != null -> selectedJobForDetails = null
            isMessagesOpen -> isMessagesOpen = false
            selectedTab != NavigationTab.HOME -> selectedTab = NavigationTab.HOME
        }
    }

    if (isMessagesOpen) {
        val chatMessages by repository.observeMessages("conv_1").collectAsState(initial = emptyList())
        MessagesScreen(
            messages = chatMessages,
            currentUserId = currentUserId,
            onSendMessage = { text ->
                coroutineScope.launch {
                    repository.sendMessage("conv_1", text)
                }
            },
            onBack = { isMessagesOpen = false }
        )
    } else {
        Scaffold(
            topBar = {
                JobixTopBar(
                    activeRole = activeRole,
                    onRoleChange = { newRole ->
                        activeRole = newRole
                        userProfile?.let {
                            coroutineScope.launch {
                                repository.saveUserProfile(it.copy(role = newRole))
                            }
                        }
                    },
                    onOpenProfile = { selectedTab = NavigationTab.DASHBOARD },
                    onOpenMessages = { isMessagesOpen = true },
                    onSignOut = {
                        signOut(credentialManager, coroutineScope)
                    }
                )
            },
            bottomBar = {
                JobixBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            jobs = jobs,
                            competitions = competitions,
                            onSelectJob = { selectedJobForDetails = it },
                            onSaveJob = { job ->
                                val isSaved = savedJobIds.contains(job.id)
                                coroutineScope.launch {
                                    repository.toggleSaveJob(job, isSaved)
                                }
                            },
                            savedJobIds = savedJobIds,
                            onExploreAllJobs = { selectedTab = NavigationTab.JOBS },
                            onExploreCompetitions = { selectedTab = NavigationTab.COMPETITIONS }
                        )
                    }
                    NavigationTab.JOBS -> {
                        ExploreJobsScreen(
                            jobs = jobs,
                            savedJobIds = savedJobIds,
                            onSelectJob = { selectedJobForDetails = it },
                            onSaveJob = { job ->
                                val isSaved = savedJobIds.contains(job.id)
                                coroutineScope.launch {
                                    repository.toggleSaveJob(job, isSaved)
                                }
                            }
                        )
                    }
                    NavigationTab.NETWORK -> {
                        NetworkingScreen(
                            posts = posts,
                            onLikePost = { post ->
                                coroutineScope.launch {
                                    repository.likePost(post.id, post.likesCount)
                                }
                            },
                            onCreatePost = { content, tags ->
                                coroutineScope.launch {
                                    repository.createPost(content, tags)
                                    Toast.makeText(context, "Post shared with network!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    NavigationTab.COMPETITIONS -> {
                        CompetitionsScreen(
                            competitions = competitions,
                            registeredIds = registeredCompIds.toSet(),
                            onToggleRegister = { comp ->
                                if (registeredCompIds.contains(comp.id)) {
                                    registeredCompIds.remove(comp.id)
                                    Toast.makeText(context, "Registration cancelled", Toast.LENGTH_SHORT).show()
                                } else {
                                    registeredCompIds.add(comp.id)
                                    Toast.makeText(context, "Registered for ${comp.title}!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    NavigationTab.DASHBOARD -> {
                        WorkspaceScreen(
                            userProfile = userProfile ?: UserProfile(userId = currentUserId, name = repository.currentUserName() ?: "Jobix Member", email = repository.currentUserEmail() ?: ""),
                            activeRole = activeRole,
                            onRoleChange = { newRole ->
                                activeRole = newRole
                                userProfile?.let {
                                    coroutineScope.launch {
                                        repository.saveUserProfile(it.copy(role = newRole))
                                    }
                                }
                            },
                            applications = applications,
                            savedJobs = savedJobs,
                            allJobs = jobs,
                            auditLogs = repository.getAuditLogs(),
                            onSaveProfile = { updated ->
                                coroutineScope.launch {
                                    repository.saveUserProfile(updated)
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCreateJob = { newJob ->
                                coroutineScope.launch {
                                    repository.createJob(newJob)
                                    Toast.makeText(context, "Job published successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onUpdateApplicationStatus = { appId, status, notes ->
                                coroutineScope.launch {
                                    repository.updateApplicationStatus(appId, status, notes)
                                    Toast.makeText(context, "Status updated to $status", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onLogAdminAction = { adminName, action, target, reason ->
                                repository.logAdminAction(adminName, action, target, reason)
                                Toast.makeText(context, "Admin action logged: $action", Toast.LENGTH_SHORT).show()
                            },
                            onSelectJob = { selectedJobForDetails = it }
                        )
                    }
                }
            }
        }
    }

    selectedJobForDetails?.let { job ->
        val isSaved = savedJobIds.contains(job.id)
        val isApplied = appliedJobIds.contains(job.id)
        JobDetailSheet(
            job = job,
            isSaved = isSaved,
            isApplied = isApplied,
            onSaveJob = {
                coroutineScope.launch {
                    repository.toggleSaveJob(job, isSaved)
                }
            },
            onApply = { coverNote ->
                coroutineScope.launch {
                    val result = repository.applyForJob(
                        job = job,
                        applicantName = userProfile?.name ?: repository.currentUserName() ?: "Candidate",
                        applicantEmail = userProfile?.email ?: repository.currentUserEmail() ?: "candidate@jobix.in",
                        coverNote = coverNote
                    )
                    if (result.isSuccess) {
                        Toast.makeText(context, "Application submitted to ${job.companyName}!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Submission error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { selectedJobForDetails = null }
        )
    }
}
