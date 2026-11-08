package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class JobixRepository(
    private val context: Context,
    private val db: FirebaseFirestore
) {
    companion object {
        @Volatile
        private var instance: JobixRepository? = null

        fun getInstance(context: Context): JobixRepository {
            return instance ?: synchronized(this) {
                instance ?: JobixRepository(
                    context = context.applicationContext,
                    db = FirebaseFirestore.getInstance(
                        context.applicationContext.getString(R.string.firestore_database_id)
                    )
                ).also { instance = it }
            }
        }
    }

    private val auth = Firebase.auth

    fun currentUserId(): String? = auth.currentUser?.uid
    fun currentUserEmail(): String? = auth.currentUser?.email
    fun currentUserName(): String? = auth.currentUser?.displayName ?: "Jobix Member"

    // Seed Data for Instant Rich Platform Experience
    private val seedJobs = listOf(
        JobItem(
            id = "seed_job_1",
            title = "Senior Android Engineer",
            companyName = "Razorpay",
            companyLogo = "razorpay",
            location = "Bengaluru, India (Hybrid)",
            jobType = "Full-time",
            experienceLevel = "3-6 Years",
            salaryMin = 2200000L,
            salaryMax = 3500000L,
            salaryFormatted = "₹22 - 35 LPA",
            description = "Build payment flows used by millions of merchants. Spearhead modern architecture using Jetpack Compose, Kotlin Multiplatform, and reactive flows.",
            requirements = "3+ years production Android experience. Deep knowledge of Kotlin, Compose, Coroutines, and SDK performance optimization.",
            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Payments", "Architecture"),
            postedBy = "admin_recruiter",
            status = "open",
            category = "Mobile Engineering",
            applicantsCount = 28L
        ),
        JobItem(
            id = "seed_job_2",
            title = "Mobile Software Intern (Android)",
            companyName = "Zomato",
            companyLogo = "zomato",
            location = "Gurugram / Remote",
            jobType = "Internship",
            experienceLevel = "Freshers / 2025-2026 Batch",
            salaryMin = 45000L,
            salaryMax = 60000L,
            salaryFormatted = "₹45k - 60k / Month",
            description = "Join Zomato's Core Consumer App team. Work on instant live order tracking animations, offline resilience, and edge networking.",
            requirements = "Strong CS fundamentals, hands-on portfolio projects in Android or Kotlin, and eagerness to ship high-impact features.",
            skills = listOf("Android", "Kotlin", "Git", "REST APIs", "UI/UX"),
            postedBy = "admin_recruiter",
            status = "open",
            category = "Internship",
            applicantsCount = 142L
        ),
        JobItem(
            id = "seed_job_3",
            title = "Cloud Infrastructure & SRE",
            companyName = "Zerodha",
            companyLogo = "zerodha",
            location = "Bengaluru (Onsite)",
            jobType = "Full-time",
            experienceLevel = "2-5 Years",
            salaryMin = 2500000L,
            salaryMax = 4000000L,
            salaryFormatted = "₹25 - 40 LPA",
            description = "Scale low-latency trading infrastructure handling billions in daily volume. Work with Go, Linux internals, and distributed databases.",
            requirements = "Passionate about open-source, networking fundamentals, Kubernetes, and reliable telemetry.",
            skills = listOf("Go", "Kubernetes", "Linux", "Distributed Systems", "SRE"),
            postedBy = "admin_recruiter",
            status = "open",
            category = "DevOps & Cloud",
            applicantsCount = 64L
        ),
        JobItem(
            id = "seed_job_4",
            title = "AI / ML Research Associate",
            companyName = "Google India",
            companyLogo = "google",
            location = "Hyderabad / Bengaluru",
            jobType = "Full-time",
            experienceLevel = "1-4 Years",
            salaryMin = 3200000L,
            salaryMax = 5500000L,
            salaryFormatted = "₹32 - 55 LPA",
            description = "Collaborate with Google DeepMind and AI researchers to build on-device multimodal models and Gemini integrations for next-billion users.",
            requirements = "Master's or strong Bachelor's in CS/AI. Deep learning frameworks, PyTorch, quantization, and evaluation benchmarks.",
            skills = listOf("Python", "PyTorch", "LLMs", "Gemini", "Model Optimization"),
            postedBy = "admin_recruiter",
            status = "open",
            category = "Artificial Intelligence",
            applicantsCount = 210L
        ),
        JobItem(
            id = "seed_job_5",
            title = "Full-Stack Product Developer",
            companyName = "Cred",
            companyLogo = "cred",
            location = "Bengaluru (Hybrid)",
            jobType = "Full-time",
            experienceLevel = "2-5 Years",
            salaryMin = 2800000L,
            salaryMax = 4200000L,
            salaryFormatted = "₹28 - 42 LPA",
            description = "Build visually captivating fintech surfaces with high craft. Experience with reactive architectures and microservices.",
            requirements = "Obsession with UI fidelity, TypeScript, React / Kotlin, and resilient distributed backends.",
            skills = listOf("Kotlin", "React", "Spring Boot", "PostgreSQL", "UI Craft"),
            postedBy = "admin_recruiter",
            status = "open",
            category = "Full-Stack",
            applicantsCount = 89L
        )
    )

    private val seedCompetitions = listOf(
        CompetitionItem(
            id = "comp_1",
            title = "Smart India Hackathon 2026",
            organizer = "Ministry of Education & AICTE",
            type = "National Hackathon",
            prizePool = "₹1,00,00,000",
            deadline = "15 Days Left",
            participantsCount = 12450,
            description = "Nationwide initiative to provide students with a platform to solve pressing problems of government ministries and industry.",
            eligibility = "Undergraduate & Graduate college students across India",
            tags = listOf("AI/ML", "Smart Cities", "Fintech", "GovTech")
        ),
        CompetitionItem(
            id = "comp_2",
            title = "CodeSprint Algo Championship",
            organizer = "Jobix Collegiate League",
            type = "Competitive Coding",
            prizePool = "₹5,00,000 + Direct Interviews",
            deadline = "4 Days Left",
            participantsCount = 3820,
            description = "3-hour algorithmic sprint tested against edge cases. Top 50 participants receive direct interview fast-tracks with tech partners.",
            eligibility = "Open to all students & working professionals",
            tags = listOf("Data Structures", "Algorithms", "Graph Theory", "Dynamic Programming")
        ),
        CompetitionItem(
            id = "comp_3",
            title = "FinTech Product Innovation Challenge",
            organizer = "Razorpay & Y Combinator Alum",
            type = "Case Study & Prototype",
            prizePool = "₹10,00,000 + Grant",
            deadline = "21 Days Left",
            participantsCount = 1940,
            description = "Design and prototype next-generation financial inclusion tools for tier 2/3 Indian entrepreneurs.",
            eligibility = "Teams of 2-4 members",
            tags = listOf("Product Design", "Payments", "System Design", "UI/UX")
        )
    )

    private val seedPosts = listOf(
        PostItem(
            id = "seed_post_1",
            authorId = "system_lead",
            authorName = "Ananya Sharma",
            authorHeadline = "Principal Architect @ CloudScale | Jobix Mentor",
            content = "Excited to share that we just opened 15 summer internship roles in Mobile & Distributed Systems at our Bengaluru office! Tips for applicants: Highlight concrete projects, clean GitHub repos, and real performance profiling.",
            tags = listOf("#Hiring", "#AndroidDev", "#Internships2026", "#TechCareers"),
            likesCount = 142L,
            commentsCount = 29L
        ),
        PostItem(
            id = "seed_post_2",
            authorId = "system_recruiter",
            authorName = "Vikram Malhotra",
            authorHeadline = "Tech Talent Partner @ Razorpay",
            content = "One common resume mistake in 2026: Listing 25 programming languages without showing depth. Recruiters look for 1-2 stacks where you truly understand memory models, asynchronous concurrency, and clean testing.",
            tags = listOf("#CareerAdvice", "#JobSearch", "#EngineeringLeadership"),
            likesCount = 389L,
            commentsCount = 54L
        )
    )

    // User Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots().map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java)
                } else {
                    UserProfile(
                        userId = userId,
                        name = currentUserName() ?: "Jobix Member",
                        email = currentUserEmail() ?: "",
                        role = "seeker"
                    )
                }
            }.catch { err ->
                handleFirestoreError(err, OperationType.GET, docRef.path)
                emit(UserProfile(userId = userId, name = currentUserName() ?: "Jobix Member", email = currentUserEmail() ?: ""))
            }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("users").document(uid)
        try {
            val payload = mapOf(
                "userId" to uid,
                "name" to profile.name,
                "email" to profile.email,
                "role" to profile.role,
                "headline" to profile.headline,
                "bio" to profile.bio,
                "skills" to profile.skills,
                "education" to profile.education,
                "experience" to profile.experience,
                "portfolioUrl" to profile.portfolioUrl,
                "resumeUrl" to profile.resumeUrl,
                "preferredRoles" to profile.preferredRoles,
                "preferredLocations" to profile.preferredLocations,
                "isPublic" to profile.isPublic,
                "isSuspended" to profile.isSuspended,
                "suspensionReason" to profile.suspensionReason,
                "recruiterApproved" to profile.recruiterApproved,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload, SetOptions.merge()).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    // Jobs
    fun observeJobs(): Flow<List<JobItem>> = flow {
        val colRef = db.collection("jobs")
        emitAll(
            colRef.snapshots().map { snapshot ->
                val firestoreJobs = snapshot.toObjects(JobItem::class.java)
                val allJobs = (firestoreJobs + seedJobs).distinctBy { it.id }
                allJobs
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(seedJobs)
            }
        )
    }

    suspend fun createJob(job: JobItem) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("jobs").document()
        try {
            val payload = mapOf(
                "title" to job.title,
                "companyName" to job.companyName,
                "companyId" to job.companyId,
                "location" to job.location,
                "jobType" to job.jobType,
                "experienceLevel" to job.experienceLevel,
                "salaryMin" to job.salaryMin,
                "salaryMax" to job.salaryMax,
                "salaryFormatted" to job.salaryFormatted,
                "description" to job.description,
                "requirements" to job.requirements,
                "skills" to job.skills,
                "postedBy" to uid,
                "status" to "open",
                "category" to job.category,
                "applicantsCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    // Saved Jobs
    fun observeSavedJobs(userId: String): Flow<List<SavedJobItem>> = flow {
        val colRef = db.collection("savedJobs")
        emitAll(
            colRef.whereEqualTo("userId", userId).snapshots().map { snapshot ->
                snapshot.toObjects(SavedJobItem::class.java)
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(emptyList())
            }
        )
    }

    suspend fun toggleSaveJob(job: JobItem, isSaved: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val docId = "${uid}_${job.id}"
        val docRef = db.collection("savedJobs").document(docId)
        try {
            if (isSaved) {
                docRef.delete().await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "jobId" to job.id,
                    "jobTitle" to job.title,
                    "companyName" to job.companyName,
                    "location" to job.location,
                    "salary" to job.salaryFormatted,
                    "savedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    // Applications
    fun observeUserApplications(userId: String): Flow<List<ApplicationItem>> = flow {
        val colRef = db.collection("applications")
        emitAll(
            colRef.whereEqualTo("applicantId", userId).snapshots().map { snapshot ->
                snapshot.toObjects(ApplicationItem::class.java)
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(emptyList())
            }
        )
    }

    fun observeJobApplications(jobId: String): Flow<List<ApplicationItem>> = flow {
        val colRef = db.collection("applications")
        emitAll(
            colRef.whereEqualTo("jobId", jobId).snapshots().map { snapshot ->
                snapshot.toObjects(ApplicationItem::class.java)
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(emptyList())
            }
        )
    }

    suspend fun applyForJob(
        job: JobItem,
        applicantName: String,
        applicantEmail: String,
        coverNote: String
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Must be signed in"))
        val applicationId = "${uid}_${job.id}"
        val docRef = db.collection("applications").document(applicationId)
        return try {
            val payload = mapOf(
                "jobId" to job.id,
                "jobTitle" to job.title,
                "companyName" to job.companyName,
                "applicantId" to uid,
                "applicantName" to applicantName,
                "applicantEmail" to applicantEmail,
                "resumeUrl" to "verified_jobix_resume.pdf",
                "coverNote" to coverNote,
                "status" to "Applied",
                "recruiterNotes" to "",
                "interviewDate" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateApplicationStatus(applicationId: String, status: String, notes: String = "") {
        val docRef = db.collection("applications").document(applicationId)
        try {
            val updates = mutableMapOf<String, Any>(
                "status" to status,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (notes.isNotBlank()) updates["recruiterNotes"] = notes
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    // Community Posts
    fun observePosts(): Flow<List<PostItem>> = flow {
        val colRef = db.collection("posts")
        emitAll(
            colRef.snapshots().map { snapshot ->
                val firestorePosts = snapshot.toObjects(PostItem::class.java)
                (firestorePosts + seedPosts).distinctBy { it.id }
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(seedPosts)
            }
        )
    }

    suspend fun createPost(content: String, tags: List<String>) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("posts").document()
        try {
            val payload = mapOf(
                "authorId" to uid,
                "authorName" to (currentUserName() ?: "Jobix Member"),
                "authorHeadline" to "Tech Community Member @ Jobix",
                "content" to content,
                "tags" to tags,
                "likesCount" to 0L,
                "commentsCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    suspend fun likePost(postId: String, currentLikes: Long) {
        val docRef = db.collection("posts").document(postId)
        try {
            docRef.update("likesCount", currentLikes + 1).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
    }

    // Messaging
    fun observeMessages(conversationId: String): Flow<List<MessageItem>> = flow {
        val colRef = db.collection("conversations").document(conversationId).collection("messages")
        emitAll(
            colRef.snapshots().map { snapshot ->
                snapshot.toObjects(MessageItem::class.java)
            }.catch { err ->
                handleFirestoreError(err, OperationType.LIST, colRef.path)
                emit(emptyList())
            }
        )
    }

    suspend fun sendMessage(conversationId: String, text: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = db.collection("conversations").document(conversationId).collection("messages").document()
        try {
            val payload = mapOf(
                "conversationId" to conversationId,
                "senderId" to uid,
                "senderName" to (currentUserName() ?: "Jobix Member"),
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    // Competitions
    fun getCompetitions(): List<CompetitionItem> = seedCompetitions

    // Admin Audit Logs & Controls
    private val localAuditLogs = mutableListOf(
        AdminAuditLog("log_1", "Super Admin", "Approve Recruiter", "Razorpay Talent Team", "10:14 AM", "Verified corporate domain credentials"),
        AdminAuditLog("log_2", "Job Reviewer", "Publish Job", "Cloud Infrastructure @ Zerodha", "09:30 AM", "Compliant salary & JD benchmarks"),
        AdminAuditLog("log_3", "Security Bot", "Spam Scan Passed", "Feed Post #142", "08:15 AM", "Zero spam patterns detected")
    )

    fun getAuditLogs(): List<AdminAuditLog> = localAuditLogs.toList()

    fun logAdminAction(adminName: String, action: String, target: String, reason: String) {
        localAuditLogs.add(0, AdminAuditLog("log_${System.currentTimeMillis()}", adminName, action, target, "Just now", reason))
    }
}
