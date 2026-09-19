package com.example.alumni.repository;

import com.example.alumni.entity.AlumniProfile;
import com.example.alumni.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> {
    Optional<AlumniProfile> findByUserId(Long userId);
    Optional<AlumniProfile> findByUser(User user);
    void deleteByUserId(Long userId);

    List<AlumniProfile> findByVerifiedTrue();
    List<AlumniProfile> findByVerifiedTrueAndAvailableForMentoringTrue();

    long countByVerifiedTrue();
    long countByVerifiedFalse();

    @Query("SELECT a FROM AlumniProfile a WHERE a.verified = true AND a.availableForMentoring = true " +
           "AND (:department IS NULL OR LOWER(a.department) = LOWER(:department)) " +
           "AND (:company IS NULL OR LOWER(a.company) LIKE LOWER(CONCAT('%', :company, '%'))) " +
           "AND (:jobRole IS NULL OR LOWER(a.jobRole) LIKE LOWER(CONCAT('%', :jobRole, '%'))) " +
           "AND (:graduationYear IS NULL OR a.graduationYear = :graduationYear) " +
           "AND (:skill IS NULL OR LOWER(a.skills) LIKE LOWER(CONCAT('%', :skill, '%')))")
    List<AlumniProfile> searchAlumni(
            @Param("department") String department,
            @Param("company") String company,
            @Param("jobRole") String jobRole,
            @Param("graduationYear") Integer graduationYear,
            @Param("skill") String skill
    );
}
