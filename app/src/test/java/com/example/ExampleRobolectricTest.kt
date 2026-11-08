package com.example

import com.example.data.model.ApplicationItem
import com.example.data.model.CompetitionItem
import com.example.data.model.JobItem
import com.example.data.model.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExampleRobolectricTest {

  @Test
  fun `verify user profile defaults and role validation`() {
    val profile = UserProfile(
      userId = "user_123",
      name = "Harish",
      email = "harish@jobix.in",
      role = "seeker"
    )
    assertEquals("seeker", profile.role)
    assertTrue(profile.skills.isNotEmpty())
    assertTrue(profile.isPublic)
  }

  @Test
  fun `verify job item structure and fields`() {
    val job = JobItem(
      id = "job_test",
      title = "Staff Android Engineer",
      companyName = "Jobix Platform",
      location = "Bengaluru",
      salaryFormatted = "₹25 - 40 LPA",
      skills = listOf("Kotlin", "Jetpack Compose", "Coroutines")
    )
    assertEquals("Staff Android Engineer", job.title)
    assertEquals("Jobix Platform", job.companyName)
    assertNotNull(job.salaryFormatted)
    assertEquals(3, job.skills.size)
  }

  @Test
  fun `verify application item status transitions`() {
    val application = ApplicationItem(
      id = "app_1",
      jobId = "job_1",
      jobTitle = "Senior Android Engineer",
      companyName = "Razorpay",
      applicantId = "user_123",
      status = "Applied"
    )
    assertEquals("Applied", application.status)
    val shortlisted = application.copy(status = "Shortlisted")
    assertEquals("Shortlisted", shortlisted.status)
  }

  @Test
  fun `verify competition prize and participants count`() {
    val competition = CompetitionItem(
      id = "comp_test",
      title = "Smart India Hackathon 2026",
      organizer = "AICTE",
      type = "Hackathon",
      prizePool = "₹1,00,00,000",
      deadline = "15 Days Left",
      participantsCount = 12450,
      description = "National hackathon",
      eligibility = "College students",
      tags = listOf("AI/ML", "Fintech")
    )
    assertEquals(12450, competition.participantsCount)
    assertEquals("₹1,00,00,000", competition.prizePool)
  }
}
