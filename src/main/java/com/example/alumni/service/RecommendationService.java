package com.example.alumni.service;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.MentorMatchDTO;
import com.example.alumni.entity.AlumniProfile;
import com.example.alumni.entity.StudentProfile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    /**
     * Calculates match scores for a student against a list of alumni profiles.
     * Formula:
     * - Skill Match: 50%
     * - Interest Match: 30%
     * - Career Goal Match: 20%
     */
    public List<MentorMatchDTO> recommendMentors(StudentProfile student, List<AlumniProfileDTO> alumniList) {
        if (student == null || alumniList == null) {
            return Collections.emptyList();
        }

        Set<String> studentSkills = parseTokens(student.getSkills());
        Set<String> studentInterests = parseTokens(student.getInterests());
        String studentGoal = student.getCareerGoal() != null ? student.getCareerGoal().trim().toLowerCase() : "";

        List<MentorMatchDTO> recommendations = new ArrayList<>();

        for (AlumniProfileDTO alumni : alumniList) {
            // 1. Skill Match (50%)
            Set<String> alumniSkills = parseTokens(alumni.getSkills());
            double skillMatchFraction = calculateSetOverlap(studentSkills, alumniSkills);
            int skillScore = (int) Math.round(skillMatchFraction * 100);

            // 2. Interest Match (30%) - compares student interests with alumni expertise and bio
            Set<String> alumniExpertise = parseTokens(alumni.getExpertise());
            if (alumni.getBio() != null) {
                alumniExpertise.addAll(parseTokens(alumni.getBio()));
            }
            double interestMatchFraction = calculateFuzzyOrSetOverlap(studentInterests, alumniExpertise);
            int interestScore = (int) Math.round(interestMatchFraction * 100);

            // 3. Career Goal Match (20%) - compares student career goal with alumni job role
            String alumniRole = alumni.getJobRole() != null ? alumni.getJobRole().trim().toLowerCase() : "";
            double goalMatchFraction = calculateStringSimilarity(studentGoal, alumniRole);
            int goalScore = (int) Math.round(goalMatchFraction * 100);

            // Weighted Total (0 - 100)
            int totalMatchPercentage = (int) Math.round(
                    (skillScore * 0.50) + (interestScore * 0.30) + (goalScore * 0.20)
            );

            // Enforce bounds
            totalMatchPercentage = Math.min(100, Math.max(0, totalMatchPercentage));

            recommendations.add(new MentorMatchDTO(
                    alumni,
                    totalMatchPercentage,
                    skillScore,
                    interestScore,
                    goalScore
            ));
        }

        // Sort descending by match percentage
        recommendations.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));

        return recommendations;
    }

    private Set<String> parseTokens(String text) {
        if (text == null || text.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(text.split("[,;\\n/|]+"))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private double calculateSetOverlap(Set<String> studentTokens, Set<String> alumniTokens) {
        if (studentTokens.isEmpty() || alumniTokens.isEmpty()) {
            return 0.0;
        }

        int matches = 0;
        for (String s : studentTokens) {
            for (String a : alumniTokens) {
                if (s.equals(a) || s.contains(a) || a.contains(s)) {
                    matches++;
                    break;
                }
            }
        }
        return (double) matches / (double) studentTokens.size();
    }

    private double calculateFuzzyOrSetOverlap(Set<String> studentTokens, Set<String> alumniTokens) {
        if (studentTokens.isEmpty() || alumniTokens.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        for (String s : studentTokens) {
            double bestMatch = 0.0;
            for (String a : alumniTokens) {
                if (s.equals(a)) {
                    bestMatch = 1.0;
                    break;
                } else if (s.contains(a) || a.contains(s)) {
                    bestMatch = Math.max(bestMatch, 0.85);
                } else {
                    double sim = tokenWordOverlap(s, a);
                    bestMatch = Math.max(bestMatch, sim);
                }
            }
            totalScore += bestMatch;
        }
        return Math.min(1.0, totalScore / (double) studentTokens.size());
    }

    private double calculateStringSimilarity(String str1, String str2) {
        if (str1.isEmpty() || str2.isEmpty()) {
            return 0.0;
        }
        if (str1.equals(str2)) {
            return 1.0;
        }
        if (str1.contains(str2) || str2.contains(str1)) {
            return 0.9;
        }

        return tokenWordOverlap(str1, str2);
    }

    private double tokenWordOverlap(String s1, String s2) {
        Set<String> words1 = Arrays.stream(s1.split("\\s+"))
                .map(String::trim)
                .filter(w -> w.length() > 2)
                .collect(Collectors.toSet());
        Set<String> words2 = Arrays.stream(s2.split("\\s+"))
                .map(String::trim)
                .filter(w -> w.length() > 2)
                .collect(Collectors.toSet());

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        int common = 0;
        for (String w1 : words1) {
            if (words2.contains(w1)) {
                common++;
            }
        }

        return (double) common / (double) Math.max(words1.size(), words2.size());
    }
}
