package com.example.alumni.repository;

import com.example.alumni.entity.MentorshipSession;
import com.example.alumni.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {
    List<MentorshipSession> findByMentorshipRequest_Student_IdOrderBySessionDateDesc(Long studentId);
    List<MentorshipSession> findByMentorshipRequest_Alumni_IdOrderBySessionDateDesc(Long alumniId);

    @Query("SELECT s FROM MentorshipSession s WHERE s.mentorshipRequest.student.id = :studentId AND s.status = :status ORDER BY s.sessionDate ASC")
    List<MentorshipSession> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") SessionStatus status);

    @Query("SELECT s FROM MentorshipSession s WHERE s.mentorshipRequest.alumni.id = :alumniId AND s.status = :status ORDER BY s.sessionDate ASC")
    List<MentorshipSession> findByAlumniIdAndStatus(@Param("alumniId") Long alumniId, @Param("status") SessionStatus status);

    long countByStatus(SessionStatus status);

    @Query("SELECT COUNT(s) FROM MentorshipSession s WHERE s.mentorshipRequest.student.id = :studentId AND s.status = :status")
    long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") SessionStatus status);

    @Query("SELECT COUNT(s) FROM MentorshipSession s WHERE s.mentorshipRequest.alumni.id = :alumniId AND s.status = :status")
    long countByAlumniIdAndStatus(@Param("alumniId") Long alumniId, @Param("status") SessionStatus status);
}
