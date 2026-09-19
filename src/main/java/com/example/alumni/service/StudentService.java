package com.example.alumni.service;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.MentorMatchDTO;
import com.example.alumni.dto.StudentProfileDTO;
import com.example.alumni.entity.*;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final FeedbackRepository feedbackRepository;
    private final RecommendationService recommendationService;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          AlumniProfileRepository alumniProfileRepository,
                          MentorshipRequestRepository mentorshipRequestRepository,
                          MentorshipSessionRepository mentorshipSessionRepository,
                          FeedbackRepository feedbackRepository,
                          RecommendationService recommendationService) {
        this.studentProfileRepository = studentProfileRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.feedbackRepository = feedbackRepository;
        this.recommendationService = recommendationService;
    }

    public StudentProfileDTO getProfile(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + userId));
        return toDTO(profile);
    }

    @Transactional
    public StudentProfileDTO updateProfile(Long userId, StudentProfileDTO dto) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + userId));

        if (dto.getRegisterNumber() != null) profile.setRegisterNumber(dto.getRegisterNumber());
        if (dto.getDepartment() != null) profile.setDepartment(dto.getDepartment());
        if (dto.getGraduationYear() != null) profile.setGraduationYear(dto.getGraduationYear());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getSkills() != null) profile.setSkills(dto.getSkills());
        if (dto.getInterests() != null) profile.setInterests(dto.getInterests());
        if (dto.getCareerGoal() != null) profile.setCareerGoal(dto.getCareerGoal());

        profile = studentProfileRepository.save(profile);
        return toDTO(profile);
    }

    public List<AlumniProfileDTO> getAllVerifiedAlumni() {
        return alumniProfileRepository.findByVerifiedTrue().stream()
                .map(this::toAlumniDTO)
                .collect(Collectors.toList());
    }

    public AlumniProfileDTO getAlumniProfileById(Long id) {
        AlumniProfile profile = alumniProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found with ID: " + id));
        return toAlumniDTO(profile);
    }

    public List<AlumniProfileDTO> searchAlumni(String skill, String company, String jobRole, String department, Integer graduationYear) {
        // Clean empty strings to null for query
        String cleanedSkill = (skill != null && !skill.trim().isEmpty()) ? skill.trim() : null;
        String cleanedCompany = (company != null && !company.trim().isEmpty()) ? company.trim() : null;
        String cleanedJobRole = (jobRole != null && !jobRole.trim().isEmpty()) ? jobRole.trim() : null;
        String cleanedDepartment = (department != null && !department.trim().isEmpty()) ? department.trim() : null;

        return alumniProfileRepository.searchAlumni(cleanedDepartment, cleanedCompany, cleanedJobRole, graduationYear, cleanedSkill)
                .stream()
                .map(this::toAlumniDTO)
                .collect(Collectors.toList());
    }

    public List<MentorMatchDTO> getRecommendedMentors(Long studentUserId) {
        StudentProfile student = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + studentUserId));

        List<AlumniProfileDTO> verifiedAlumni = alumniProfileRepository.findByVerifiedTrueAndAvailableForMentoringTrue()
                .stream()
                .map(this::toAlumniDTO)
                .collect(Collectors.toList());

        return recommendationService.recommendMentors(student, verifiedAlumni);
    }

    public Map<String, Object> getDashboardStats(Long studentUserId) {
        Map<String, Object> stats = new HashMap<>();
        long availableMentors = alumniProfileRepository.findByVerifiedTrueAndAvailableForMentoringTrue().size();
        long pendingRequests = mentorshipRequestRepository.countByStudentIdAndStatus(studentUserId, RequestStatus.PENDING);
        long activeMentorships = mentorshipRequestRepository.countByStudentIdAndStatus(studentUserId, RequestStatus.ACCEPTED);
        long upcomingSessions = mentorshipSessionRepository.countByStudentIdAndStatus(studentUserId, SessionStatus.CONFIRMED);

        stats.put("availableMentors", availableMentors);
        stats.put("pendingRequests", pendingRequests);
        stats.put("activeMentorships", activeMentorships);
        stats.put("upcomingSessions", upcomingSessions);
        return stats;
    }

    public StudentProfileDTO toDTO(StudentProfile profile) {
        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setName(profile.getUser().getName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setRegisterNumber(profile.getRegisterNumber());
        dto.setDepartment(profile.getDepartment());
        dto.setGraduationYear(profile.getGraduationYear());
        dto.setBio(profile.getBio());
        dto.setSkills(profile.getSkills());
        dto.setInterests(profile.getInterests());
        dto.setCareerGoal(profile.getCareerGoal());
        return dto;
    }

    public AlumniProfileDTO toAlumniDTO(AlumniProfile profile) {
        AlumniProfileDTO dto = new AlumniProfileDTO();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setName(profile.getUser().getName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setGraduationYear(profile.getGraduationYear());
        dto.setDepartment(profile.getDepartment());
        dto.setCompany(profile.getCompany());
        dto.setJobRole(profile.getJobRole());
        dto.setExperience(profile.getExperience());
        dto.setBio(profile.getBio());
        dto.setSkills(profile.getSkills());
        dto.setExpertise(profile.getExpertise());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setAvailableForMentoring(profile.isAvailableForMentoring());
        dto.setVerified(profile.isVerified());

        Double avgRating = feedbackRepository.getAverageRatingForAlumni(profile.getUser().getId());
        dto.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        dto.setTotalReviews(feedbackRepository.countByAlumniId(profile.getUser().getId()));
        return dto;
    }
}
