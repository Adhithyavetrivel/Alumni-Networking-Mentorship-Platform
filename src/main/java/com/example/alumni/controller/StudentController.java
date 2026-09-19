package com.example.alumni.controller;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.MentorMatchDTO;
import com.example.alumni.dto.StudentProfileDTO;
import com.example.alumni.entity.User;
import com.example.alumni.service.AuthService;
import com.example.alumni.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final AuthService authService;

    public StudentController(StudentService studentService, AuthService authService) {
        this.studentService = studentService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDTO> getProfile() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(studentService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentProfileDTO> updateProfile(@RequestBody StudentProfileDTO dto) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(studentService.updateProfile(user.getId(), dto));
    }

    @GetMapping("/alumni")
    public ResponseEntity<List<AlumniProfileDTO>> getAllAlumni() {
        return ResponseEntity.ok(studentService.getAllVerifiedAlumni());
    }

    @GetMapping("/alumni/{id}")
    public ResponseEntity<AlumniProfileDTO> getAlumniById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getAlumniProfileById(id));
    }

    @GetMapping("/alumni/search")
    public ResponseEntity<List<AlumniProfileDTO>> searchAlumni(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String jobRole,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer graduationYear) {
        return ResponseEntity.ok(studentService.searchAlumni(skill, company, jobRole, department, graduationYear));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<MentorMatchDTO>> getRecommendations() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(studentService.getRecommendedMentors(user.getId()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(studentService.getDashboardStats(user.getId()));
    }
}
