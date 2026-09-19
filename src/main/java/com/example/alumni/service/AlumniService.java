package com.example.alumni.service;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.StudentProfileDTO;
import com.example.alumni.entity.*;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlumniService {

    private final AlumniProfileRepository alumniProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final FeedbackRepository feedbackRepository;
    private final StudentService studentService;

    public AlumniService(AlumniProfileRepository alumniProfileRepository,
                         StudentProfileRepository studentProfileRepository,
                         MentorshipRequestRepository mentorshipRequestRepository,
                         MentorshipSessionRepository mentorshipSessionRepository,
                         FeedbackRepository feedbackRepository,
                         StudentService studentService) {
        this.alumniProfileRepository = alumniProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.feedbackRepository = feedbackRepository;
        this.studentService = studentService;
    }

    public AlumniProfileDTO getProfile(Long userId) {
        AlumniProfile profile = alumniProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found for user ID: " + userId));
        return studentService.toAlumniDTO(profile);
    }

    @Transactional
    public AlumniProfileDTO updateProfile(Long userId, AlumniProfileDTO dto) {
        AlumniProfile profile = alumniProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found for user ID: " + userId));

        if (dto.getGraduationYear() != null) profile.setGraduationYear(dto.getGraduationYear());
        if (dto.getDepartment() != null) profile.setDepartment(dto.getDepartment());
        if (dto.getCompany() != null) profile.setCompany(dto.getCompany());
        if (dto.getJobRole() != null) profile.setJobRole(dto.getJobRole());
        if (dto.getExperience() != null) profile.setExperience(dto.getExperience());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getSkills() != null) profile.setSkills(dto.getSkills());
        if (dto.getExpertise() != null) profile.setExpertise(dto.getExpertise());
        if (dto.getLinkedinUrl() != null) profile.setLinkedinUrl(dto.getLinkedinUrl());
        profile.setAvailableForMentoring(dto.isAvailableForMentoring());

        profile = alumniProfileRepository.save(profile);
        return studentService.toAlumniDTO(profile);
    }

    @Transactional
    public AlumniProfileDTO toggleAvailability(Long userId) {
        AlumniProfile profile = alumniProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found for user ID: " + userId));
        profile.setAvailableForMentoring(!profile.isAvailableForMentoring());
        profile = alumniProfileRepository.save(profile);
        return studentService.toAlumniDTO(profile);
    }

    public List<StudentProfileDTO> getActiveMentees(Long alumniUserId) {
        List<MentorshipRequest> acceptedRequests = mentorshipRequestRepository.findByAlumniIdAndStatus(alumniUserId, RequestStatus.ACCEPTED);
        List<StudentProfileDTO> mentees = new ArrayList<>();
        for (MentorshipRequest req : acceptedRequests) {
            studentProfileRepository.findByUserId(req.getStudent().getId())
                    .ifPresent(p -> mentees.add(studentService.toDTO(p)));
        }
        return mentees;
    }

    public Map<String, Object> getDashboardStats(Long alumniUserId) {
        Map<String, Object> stats = new HashMap<>();
        AlumniProfile profile = alumniProfileRepository.findByUserId(alumniUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found for user ID: " + alumniUserId));

        long incomingRequests = mentorshipRequestRepository.countByAlumniIdAndStatus(alumniUserId, RequestStatus.PENDING);
        long activeMentees = mentorshipRequestRepository.countByAlumniIdAndStatus(alumniUserId, RequestStatus.ACCEPTED);
        long upcomingSessions = mentorshipSessionRepository.countByAlumniIdAndStatus(alumniUserId, SessionStatus.CONFIRMED);
        Double avgRating = feedbackRepository.getAverageRatingForAlumni(alumniUserId);
        long totalReviews = feedbackRepository.countByAlumniId(alumniUserId);

        stats.put("isVerified", profile.isVerified());
        stats.put("availableForMentoring", profile.isAvailableForMentoring());
        stats.put("incomingRequests", incomingRequests);
        stats.put("activeMentees", activeMentees);
        stats.put("upcomingSessions", upcomingSessions);
        stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        stats.put("totalReviews", totalReviews);

        return stats;
    }
}
