package com.example.alumni.repository;

import com.example.alumni.entity.StudentProfile;
import com.example.alumni.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByUser(User user);
    void deleteByUserId(Long userId);
}
