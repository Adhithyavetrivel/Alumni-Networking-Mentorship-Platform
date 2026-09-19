package com.example.alumni.service;

import com.example.alumni.dto.MentorshipRequestDTO;
import com.example.alumni.entity.*;
import com.example.alumni.exception.BadRequestException;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.exception.UnauthorizedException;
import com.example.alumni.repository.AlumniProfileRepository;
import com.example.alumni.repository.MentorshipRequestRepository;
import com.example.alumni.repository.StudentProfileRepository;
import com.example.alumni.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final StudentProfileRepository studentProfileRepository;

    public MentorshipService(MentorshipRequestRepository mentorshipRequestRepository,
                             UserRepository userRepository,
                             AlumniProfileRepository alumniProfileRepository,
                             StudentProfileRepository studentProfileRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.userRepository = userRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public MentorshipRequestDTO createRequest(Long studentUserId, Long alumniUserId, String message) {
        if (studentUserId.equals(alumniUserId)) {
            throw new BadRequestException("You cannot send a mentorship request to yourself");
        }

        User student = userRepository.findById(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found"));
        User alumni = userRepository.findById(alumniUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni user not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can send mentorship requests");
        }
        if (alumni.getRole() != Role.ALUMNI) {
            throw new BadRequestException("Mentorship requests can only be sent to alumni");
        }

        AlumniProfile alumniProfile = alumniProfileRepository.findByUserId(alumniUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found"));

        if (!alumniProfile.isVerified()) {
            throw new BadRequestException("This alumni profile is not yet verified by the administrator");
        }
        if (!alumniProfile.isAvailableForMentoring()) {
            throw new BadRequestException("This alumni is currently not available for new mentorship requests");
        }

        // Check for existing pending request
        if (mentorshipRequestRepository.existsByStudentIdAndAlumniIdAndStatus(studentUserId, alumniUserId, RequestStatus.PENDING)) {
            throw new BadRequestException("You already have a pending mentorship request with this mentor");
        }

        MentorshipRequest request = new MentorshipRequest(student, alumni, message);
        request = mentorshipRequestRepository.save(request);

        return toDTO(request);
    }

    public List<MentorshipRequestDTO> getStudentRequests(Long studentUserId) {
        return mentorshipRequestRepository.findByStudentIdOrderByRequestedAtDesc(studentUserId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MentorshipRequestDTO> getAlumniRequests(Long alumniUserId) {
        return mentorshipRequestRepository.findByAlumniIdOrderByRequestedAtDesc(alumniUserId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MentorshipRequestDTO acceptRequest(Long alumniUserId, Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship request not found with ID: " + requestId));

        if (!request.getAlumni().getId().equals(alumniUserId)) {
            throw new UnauthorizedException("You are not authorized to accept this mentorship request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be accepted");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());
        request = mentorshipRequestRepository.save(request);

        return toDTO(request);
    }

    @Transactional
    public MentorshipRequestDTO rejectRequest(Long alumniUserId, Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship request not found with ID: " + requestId));

        if (!request.getAlumni().getId().equals(alumniUserId)) {
            throw new UnauthorizedException("You are not authorized to reject this mentorship request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be rejected");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        request = mentorshipRequestRepository.save(request);

        return toDTO(request);
    }

    @Transactional
    public MentorshipRequestDTO cancelRequest(Long studentUserId, Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship request not found with ID: " + requestId));

        if (!request.getStudent().getId().equals(studentUserId)) {
            throw new UnauthorizedException("You are not authorized to cancel this mentorship request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setRespondedAt(LocalDateTime.now());
        request = mentorshipRequestRepository.save(request);

        return toDTO(request);
    }

    public MentorshipRequestDTO toDTO(MentorshipRequest request) {
        MentorshipRequestDTO dto = new MentorshipRequestDTO();
        dto.setId(request.getId());
        dto.setStudentId(request.getStudent().getId());
        dto.setStudentName(request.getStudent().getName());
        dto.setStudentEmail(request.getStudent().getEmail());

        studentProfileRepository.findByUserId(request.getStudent().getId())
                .ifPresent(p -> dto.setStudentDepartment(p.getDepartment()));

        dto.setAlumniId(request.getAlumni().getId());
        dto.setAlumniName(request.getAlumni().getName());
        dto.setAlumniEmail(request.getAlumni().getEmail());

        alumniProfileRepository.findByUserId(request.getAlumni().getId())
                .ifPresent(p -> {
                    dto.setAlumniCompany(p.getCompany());
                    dto.setAlumniJobRole(p.getJobRole());
                });

        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setRespondedAt(request.getRespondedAt());
        return dto;
    }
}
