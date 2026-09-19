package com.example.alumni.dto;

import com.example.alumni.entity.RequestStatus;
import java.time.LocalDateTime;

public class MentorshipRequestDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentDepartment;
    private Long alumniId;
    private String alumniName;
    private String alumniEmail;
    private String alumniCompany;
    private String alumniJobRole;
    private String message;
    private RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    public MentorshipRequestDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
