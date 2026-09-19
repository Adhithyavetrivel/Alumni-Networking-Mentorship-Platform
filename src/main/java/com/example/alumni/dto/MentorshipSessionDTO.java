package com.example.alumni.dto;

import com.example.alumni.entity.SessionStatus;
import java.time.LocalDateTime;

public class MentorshipSessionDTO {
    private Long id;
    private Long mentorshipRequestId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long alumniId;
    private String alumniName;
    private String alumniEmail;
    private String alumniCompany;
    private String alumniJobRole;
    private String topic;
    private LocalDateTime sessionDate;
    private String description;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private boolean feedbackSubmitted;
    private Integer rating;
    private String feedbackComment;

    public MentorshipSessionDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMentorshipRequestId() {
        return mentorshipRequestId;
    }

    public void setMentorshipRequestId(Long mentorshipRequestId) {
        this.mentorshipRequestId = mentorshipRequestId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Long getAlumniId() {
        return alumniId;
    }

    public void setAlumniId(Long alumniId) {
        this.alumniId = alumniId;
    }

    public String getAlumniName() {
        return alumniName;
    }

    public void setAlumniName(String alumniName) {
        this.alumniName = alumniName;
    }

    public String getAlumniEmail() {
        return alumniEmail;
    }

    public void setAlumniEmail(String alumniEmail) {
        this.alumniEmail = alumniEmail;
    }

    public String getAlumniCompany() {
        return alumniCompany;
    }

    public void setAlumniCompany(String alumniCompany) {
        this.alumniCompany = alumniCompany;
    }

    public String getAlumniJobRole() {
        return alumniJobRole;
    }

    public void setAlumniJobRole(String alumniJobRole) {
        this.alumniJobRole = alumniJobRole;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFeedbackSubmitted() {
        return feedbackSubmitted;
    }

    public void setFeedbackSubmitted(boolean feedbackSubmitted) {
        this.feedbackSubmitted = feedbackSubmitted;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }
}
