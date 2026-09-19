package com.example.alumni.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentorship_sessions")
public class MentorshipSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentorship_request_id", nullable = false)
    private MentorshipRequest mentorshipRequest;

    @Column(nullable = false)
    private String topic;

    @Column(name = "session_date", nullable = false)
    private LocalDateTime sessionDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.REQUESTED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MentorshipSession() {
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.REQUESTED;
    }

    public MentorshipSession(MentorshipRequest mentorshipRequest, String topic, LocalDateTime sessionDate, String description) {
        this.mentorshipRequest = mentorshipRequest;
        this.topic = topic;
        this.sessionDate = sessionDate;
        this.description = description;
        this.status = SessionStatus.REQUESTED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MentorshipRequest getMentorshipRequest() {
        return mentorshipRequest;
    }

    public void setMentorshipRequest(MentorshipRequest mentorshipRequest) {
        this.mentorshipRequest = mentorshipRequest;
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
}
