package com.example.alumni.service;

import com.example.alumni.dto.FeedbackDTO;
import com.example.alumni.entity.*;
import com.example.alumni.exception.BadRequestException;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.exception.UnauthorizedException;
import com.example.alumni.repository.FeedbackRepository;
import com.example.alumni.repository.MentorshipSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           MentorshipSessionRepository mentorshipSessionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
    }

    @Transactional
    public FeedbackDTO submitFeedback(Long studentUserId, FeedbackDTO dto) {
        MentorshipSession session = mentorshipSessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + dto.getSessionId()));

        // Validate student is the mentee in this session
        if (!session.getMentorshipRequest().getStudent().getId().equals(studentUserId)) {
            throw new UnauthorizedException("Only the student participant can submit feedback for this session");
        }

        // Validate session is completed
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new BadRequestException("Feedback can only be submitted for completed mentoring sessions");
        }

        // Validate feedback not already submitted
        if (feedbackRepository.existsBySessionId(session.getId())) {
            throw new BadRequestException("Feedback has already been submitted for this session");
        }

        // Validate rating range
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        User student = session.getMentorshipRequest().getStudent();
        User alumni = session.getMentorshipRequest().getAlumni();

        Feedback feedback = new Feedback(
                session,
                student,
                alumni,
                dto.getRating(),
                dto.getComment()
        );

        feedback = feedbackRepository.save(feedback);
        return toDTO(feedback);
    }

    public List<FeedbackDTO> getFeedbackForAlumni(Long alumniUserId) {
        return feedbackRepository.findByAlumniIdOrderByCreatedAtDesc(alumniUserId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FeedbackDTO toDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setSessionId(feedback.getSession().getId());
        dto.setStudentId(feedback.getStudent().getId());
        dto.setStudentName(feedback.getStudent().getName());
        dto.setAlumniId(feedback.getAlumni().getId());
        dto.setAlumniName(feedback.getAlumni().getName());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
