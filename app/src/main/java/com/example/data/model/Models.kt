package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "seeker", // seeker, recruiter, admin
    val headline: String = "Aspiring Tech Professional",
    val bio: String = "Passionate about building scalable applications and continuous learning.",
    val skills: List<String> = listOf("Kotlin", "Android", "Jetpack Compose", "Firebase", "Git"),
    val education: String = "B.Tech Computer Science (2025)",
    val experience: String = "Android Developer Intern @ TechCorp",
    val portfolioUrl: String = "https://github.com",
    val resumeUrl: String = "",
    val preferredRoles: List<String> = listOf("Android Developer", "Mobile Engineer"),
    val preferredLocations: List<String> = listOf("Bengaluru", "Remote", "Pune"),
    val isPublic: Boolean = true,
    val isSuspended: Boolean = false,
    val suspensionReason: String = "",
    val recruiterApproved: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class JobItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val companyName: String = "",
    val companyId: String = "",
    val companyLogo: String = "",
    val location: String = "Bengaluru, India (Hybrid)",
    val jobType: String = "Full-time", // Full-time, Internship, Remote, Contract
    val experienceLevel: String = "Entry Level", // Freshers, Mid-Level, Senior
    val salaryMin: Long = 800000L,
    val salaryMax: Long = 1800000L,
    val salaryFormatted: String = "₹8 - 18 LPA",
    val description: String = "",
    val requirements: String = "",
    val skills: List<String> = emptyList(),
    val postedBy: String = "",
    val status: String = "open", // open, review, closed
    val category: String = "Software Development",
    val applicantsCount: Long = 12L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ApplicationItem(
    @DocumentId val id: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val companyName: String = "",
    val applicantId: String = "",
    val applicantName: String = "",
    val applicantEmail: String = "",
    val resumeUrl: String = "resume_default.pdf",
    val coverNote: String = "",
    val status: String = "Applied", // Applied, Under Review, Shortlisted, Interview, Offered, Rejected, Withdrawn
    val recruiterNotes: String = "",
    val interviewDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedJobItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val companyName: String = "",
    val location: String = "",
    val salary: String = "",
    @ServerTimestamp val savedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ConnectionItem(
    @DocumentId val id: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val requesterHeadline: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverHeadline: String = "",
    val status: String = "connected", // pending, connected, declined
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PostItem(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorHeadline: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val likedByMe: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MessageItem(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class CompetitionItem(
    val id: String,
    val title: String,
    val organizer: String,
    val type: String, // Hackathon, Coding Challenge, Case Study
    val prizePool: String,
    val deadline: String,
    val participantsCount: Int,
    val description: String,
    val eligibility: String,
    val tags: List<String>,
    val isRegistered: Boolean = false
)

data class AdminAuditLog(
    val id: String,
    val adminName: String,
    val action: String,
    val target: String,
    val timestamp: String,
    val reason: String
)
