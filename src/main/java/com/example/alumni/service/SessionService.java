package com.example.alumni.service;

import com.example.alumni.dto.MentorshipSessionDTO;
import com.example.alumni.entity.*;
import com.example.alumni.exception.BadRequestException;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.exception.UnauthorizedException;
import com.example.alumni.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final FeedbackRepository feedbackRepository;

    public SessionService(MentorshipSessionRepository mentorshipSessionRepository,
                          MentorshipRequestRepository mentorshipRequestRepository,
                          AlumniProfileRepository alumniProfileRepository,
                          FeedbackRepository feedbackRepository) {
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Transactional
    public MentorshipSessionDTO scheduleSession(Long currentUserId, Long mentorshipRequestId, String topic, LocalDateTime sessionDate, String description) {
        MentorshipRequest request = mentorshipRequestRepository.findById(mentorshipRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship request not found with ID: " + mentorshipRequestId));

        if (request.getStatus() != RequestStatus.ACCEPTED) {
            throw new BadRequestException("Sessions can only be scheduled for accepted mentorship requests");
        }

        // Verify the user is either the student or the alumni of this mentorship request
        boolean isStudent = request.getStudent().getId().equals(currentUserId);
        boolean isAlumni = request.getAlumni().getId().equals(currentUserId);

        if (!isStudent && !isAlumni) {
            throw new UnauthorizedException("You are not part of this mentorship relationship");
        }

        if (sessionDate == null) {
            throw new BadRequestException("Session date is required");
        }

        MentorshipSession session = new MentorshipSession(request, topic, sessionDate, description);
        // If created by alumni, can be confirmed directly; if created by student, set to REQUESTED
        if (isAlumni) {
            session.setStatus(SessionStatus.CONFIRMED);
        } else {
            session.setStatus(SessionStatus.REQUESTED);
        }

        session = mentorshipSessionRepository.save(session);
        return toDTO(session);
    }

    public List<MentorshipSessionDTO> getSessionsForUser(User user) {
        if (user.getRole() == Role.STUDENT) {
            return mentorshipSessionRepository.findByMentorshipRequest_Student_IdOrderBySessionDateDesc(user.getId())
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.ALUMNI) {
            return mentorshipSessionRepository.findByMentorshipRequest_Alumni_IdOrderBySessionDateDesc(user.getId())
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } else {
            // Admin can see all sessions
            return mentorshipSessionRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
    }

    public MentorshipSessionDTO getSessionById(Long sessionId, Long currentUserId, Role userRole) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        if (userRole != Role.ADMIN) {
            boolean isStudent = session.getMentorshipRequest().getStudent().getId().equals(currentUserId);
            boolean isAlumni = session.getMentorshipRequest().getAlumni().getId().equals(currentUserId);
            if (!isStudent && !isAlumni) {
                throw new UnauthorizedException("You are not authorized to view this session");
            }
        }

        return toDTO(session);
    }

    @Transactional
    public MentorshipSessionDTO confirmSession(Long currentUserId, Long sessionId) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        if (!session.getMentorshipRequest().getAlumni().getId().equals(currentUserId)) {
            throw new UnauthorizedException("Only the mentor alumni can confirm this session");
        }

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new BadRequestException("Only requested sessions can be confirmed");
        }

        session.setStatus(SessionStatus.CONFIRMED);
        session = mentorshipSessionRepository.save(session);
        return toDTO(session);
    }

    @Transactional
    public MentorshipSessionDTO completeSession(Long currentUserId, Long sessionId) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        boolean isStudent = session.getMentorshipRequest().getStudent().getId().equals(currentUserId);
        boolean isAlumni = session.getMentorshipRequest().getAlumni().getId().equals(currentUserId);

        if (!isStudent && !isAlumni) {
            throw new UnauthorizedException("You are not authorized to complete this session");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BadRequestException("This session is already marked as completed");
        }
        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw new BadRequestException("Cannot complete a cancelled session");
        }

        session.setStatus(SessionStatus.COMPLETED);
        session = mentorshipSessionRepository.save(session);
        return toDTO(session);
    }

    @Transactional
    public MentorshipSessionDTO cancelSession(Long currentUserId, Long sessionId) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        boolean isStudent = session.getMentorshipRequest().getStudent().getId().equals(currentUserId);
        boolean isAlumni = session.getMentorshipRequest().getAlumni().getId().equals(currentUserId);

        if (!isStudent && !isAlumni) {
            throw new UnauthorizedException("You are not authorized to cancel this session");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an already completed session");
        }

        session.setStatus(SessionStatus.CANCELLED);
        session = mentorshipSessionRepository.save(session);
        return toDTO(session);
    }

    public MentorshipSessionDTO toDTO(MentorshipSession session) {
        MentorshipSessionDTO dto = new MentorshipSessionDTO();
        dto.setId(session.getId());
        dto.setMentorshipRequestId(session.getMentorshipRequest().getId());

        User student = session.getMentorshipRequest().getStudent();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getName());
        dto.setStudentEmail(student.getEmail());

        User alumni = session.getMentorshipRequest().getAlumni();
        dto.setAlumniId(alumni.getId());
        dto.setAlumniName(alumni.getName());
        dto.setAlumniEmail(alumni.getEmail());

        alumniProfileRepository.findByUserId(alumni.getId()).ifPresent(p -> {
            dto.setAlumniCompany(p.getCompany());
            dto.setAlumniJobRole(p.getJobRole());
        });

        dto.setTopic(session.getTopic());
        dto.setSessionDate(session.getSessionDate());
        dto.setDescription(session.getDescription());
        dto.setStatus(session.getStatus());
        dto.setCreatedAt(session.getCreatedAt());

        Optional<Feedback> feedback = feedbackRepository.findBySessionId(session.getId());
        if (feedback.isPresent()) {
            dto.setFeedbackSubmitted(true);
            dto.setRating(feedback.get().getRating());
            dto.setFeedbackComment(feedback.get().getComment());
        } else {
            dto.setFeedbackSubmitted(false);
        }

        return dto;
    }
}
