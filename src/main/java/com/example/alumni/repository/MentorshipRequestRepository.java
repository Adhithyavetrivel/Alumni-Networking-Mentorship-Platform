package com.example.alumni.repository;

import com.example.alumni.entity.MentorshipRequest;
import com.example.alumni.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {
    List<MentorshipRequest> findByStudentIdOrderByRequestedAtDesc(Long studentId);
    List<MentorshipRequest> findByAlumniIdOrderByRequestedAtDesc(Long alumniId);
    
    List<MentorshipRequest> findByStudentIdAndStatus(Long studentId, RequestStatus status);
    List<MentorshipRequest> findByAlumniIdAndStatus(Long alumniId, RequestStatus status);

    boolean existsByStudentIdAndAlumniIdAndStatus(Long studentId, Long alumniId, RequestStatus status);

    long countByStatus(RequestStatus status);
    long countByStudentIdAndStatus(Long studentId, RequestStatus status);
    long countByAlumniIdAndStatus(Long alumniId, RequestStatus status);
    
    void deleteByStudentIdOrAlumniId(Long studentId, Long alumniId);
}
