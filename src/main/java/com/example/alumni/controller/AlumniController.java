package com.example.alumni.controller;

import com.example.alumni.dto.AlumniProfileDTO;
import com.example.alumni.dto.StudentProfileDTO;
import com.example.alumni.entity.User;
import com.example.alumni.service.AlumniService;
import com.example.alumni.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    private final AlumniService alumniService;
    private final AuthService authService;

    public AlumniController(AlumniService alumniService, AuthService authService) {
        this.alumniService = alumniService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public ResponseEntity<AlumniProfileDTO> getProfile() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(alumniService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<AlumniProfileDTO> updateProfile(@RequestBody AlumniProfileDTO dto) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(alumniService.updateProfile(user.getId(), dto));
    }

    @PutMapping("/availability")
    public ResponseEntity<AlumniProfileDTO> toggleAvailability() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(alumniService.toggleAvailability(user.getId()));
    }

    @GetMapping("/mentees")
    public ResponseEntity<List<StudentProfileDTO>> getMentees() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(alumniService.getActiveMentees(user.getId()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(alumniService.getDashboardStats(user.getId()));
    }
}
