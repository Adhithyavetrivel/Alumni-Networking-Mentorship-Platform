package com.example.alumni.dto;

public class MentorMatchDTO {
    private AlumniProfileDTO alumni;
    private int matchPercentage;
    private int skillMatchScore;
    private int interestMatchScore;
    private int careerGoalMatchScore;

    public MentorMatchDTO() {}

    public MentorMatchDTO(AlumniProfileDTO alumni, int matchPercentage, int skillMatchScore, int interestMatchScore, int careerGoalMatchScore) {
        this.alumni = alumni;
        this.matchPercentage = matchPercentage;
        this.skillMatchScore = skillMatchScore;
        this.interestMatchScore = interestMatchScore;
        this.careerGoalMatchScore = careerGoalMatchScore;
    }

    public AlumniProfileDTO getAlumni() {
        return alumni;
    }

    public void setAlumni(AlumniProfileDTO alumni) {
        this.alumni = alumni;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getSkillMatchScore() {
        return skillMatchScore;
    }

    public void setSkillMatchScore(int skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public int getInterestMatchScore() {
        return interestMatchScore;
    }

    public void setInterestMatchScore(int interestMatchScore) {
        this.interestMatchScore = interestMatchScore;
    }

    public int getCareerGoalMatchScore() {
        return careerGoalMatchScore;
    }

    public void setCareerGoalMatchScore(int careerGoalMatchScore) {
        this.careerGoalMatchScore = careerGoalMatchScore;
    }
}
