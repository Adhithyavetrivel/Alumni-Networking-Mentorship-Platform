package com.example.alumni.repository;

import com.example.alumni.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByAlumniIdOrderByCreatedAtDesc(Long alumniId);
    Optional<Feedback> findBySessionId(Long sessionId);
    boolean existsBySessionId(Long sessionId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.alumni.id = :alumniId")
    Double getAverageRatingForAlumni(@Param("alumniId") Long alumniId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.alumni.id = :alumniId")
    long countByAlumniId(@Param("alumniId") Long alumniId);
}
