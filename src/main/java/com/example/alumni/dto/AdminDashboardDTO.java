package com.example.alumni.dto;

public class AdminDashboardDTO {
    private long totalStudents;
    private long totalAlumni;
    private long verifiedAlumni;
    private long pendingVerification;
    private long activeMentorships;
    private long completedSessions;
    private long pendingReports;

    public AdminDashboardDTO() {}

    public AdminDashboardDTO(long totalStudents, long totalAlumni, long verifiedAlumni, long pendingVerification,
                             long activeMentorships, long completedSessions, long pendingReports) {
        this.totalStudents = totalStudents;
        this.totalAlumni = totalAlumni;
        this.verifiedAlumni = verifiedAlumni;
        this.pendingVerification = pendingVerification;
        this.activeMentorships = activeMentorships;
        this.completedSessions = completedSessions;
        this.pendingReports = pendingReports;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalAlumni() {
        return totalAlumni;
    }

    public void setTotalAlumni(long totalAlumni) {
        this.totalAlumni = totalAlumni;
    }

    public long getVerifiedAlumni() {
        return verifiedAlumni;
    }

    public void setVerifiedAlumni(long verifiedAlumni) {
        this.verifiedAlumni = verifiedAlumni;
    }

    public long getPendingVerification() {
        return pendingVerification;
    }

    public void setPendingVerification(long pendingVerification) {
        this.pendingVerification = pendingVerification;
    }

    public long getActiveMentorships() {
        return activeMentorships;
    }

    public void setActiveMentorships(long activeMentorships) {
        this.activeMentorships = activeMentorships;
    }

    public long getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(long completedSessions) {
        this.completedSessions = completedSessions;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }
}
