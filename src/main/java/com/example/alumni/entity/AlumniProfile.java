package com.example.alumni.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alumni_profiles")
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    private String department;

    private String company;

    @Column(name = "job_role")
    private String jobRole;

    private Integer experience; // years of experience

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String expertise;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "available_for_mentoring")
    private boolean availableForMentoring = true;

    @Column(nullable = false)
    private boolean verified = false;

    public AlumniProfile() {}

    public AlumniProfile(User user, Integer graduationYear, String department, String company, String jobRole,
                         Integer experience, String bio, String skills, String expertise, String linkedinUrl,
                         boolean availableForMentoring, boolean verified) {
        this.user = user;
        this.graduationYear = graduationYear;
        this.department = department;
        this.company = company;
        this.jobRole = jobRole;
        this.experience = experience;
        this.bio = bio;
        this.skills = skills;
        this.expertise = expertise;
        this.linkedinUrl = linkedinUrl;
        this.availableForMentoring = availableForMentoring;
        this.verified = verified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public boolean isAvailableForMentoring() {
        return availableForMentoring;
    }

    public void setAvailableForMentoring(boolean availableForMentoring) {
        this.availableForMentoring = availableForMentoring;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
