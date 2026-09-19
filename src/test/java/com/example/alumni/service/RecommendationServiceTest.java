package com.example.alumni.service;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.MentorMatchDTO;
import com.example.alumni.entity.Role;
import com.example.alumni.entity.StudentProfile;
import com.example.alumni.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationServiceTest {

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService();
    }

    @Test
    void testRecommendationAlgorithmWithPerfectAndPartialMatches() {
        User studentUser = new User("Priya Nair", "priya@student.com", "password", Role.STUDENT);
        StudentProfile student = new StudentProfile(
                studentUser,
                "21CS042",
                "Computer Science",
                2025,
                "Backend engineering enthusiast",
                "Java, Spring Boot, MySQL",
                "Backend Development",
                "Software Engineer"
        );

        AlumniProfileDTO alumni1 = new AlumniProfileDTO();
        alumni1.setId(1L);
        alumni1.setUserId(10L);
        alumni1.setName("Rahul Sharma");
        alumni1.setCompany("Google");
        alumni1.setJobRole("Software Engineer");
        alumni1.setSkills("Java, Spring Boot, AWS");
        alumni1.setExpertise("Backend Development");

        AlumniProfileDTO alumni2 = new AlumniProfileDTO();
        alumni2.setId(2L);
        alumni2.setUserId(20L);
        alumni2.setName("Neha Kulkarni");
        alumni2.setCompany("Meta");
        alumni2.setJobRole("Frontend Lead");
        alumni2.setSkills("React, JavaScript, CSS");
        alumni2.setExpertise("Frontend UI/UX");

        List<MentorMatchDTO> recommendations = recommendationService.recommendMentors(student, List.of(alumni1, alumni2));

        assertNotNull(recommendations);
        assertEquals(2, recommendations.size());

        // Rahul Sharma should have higher match percentage than Neha Kulkarni
        MentorMatchDTO topMatch = recommendations.get(0);
        assertEquals("Rahul Sharma", topMatch.getAlumni().getName());
        assertTrue(topMatch.getMatchPercentage() > 60, "Rahul should have a high match percentage");
        assertEquals(100, topMatch.getInterestMatchScore(), "Interest should match 100%");
        assertEquals(100, topMatch.getCareerGoalMatchScore(), "Career Goal should match 100%");

        MentorMatchDTO secondMatch = recommendations.get(1);
        assertEquals("Neha Kulkarni", secondMatch.getAlumni().getName());
        assertTrue(secondMatch.getMatchPercentage() < topMatch.getMatchPercentage());
    }
}
