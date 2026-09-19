package com.example.alumni.controller;

import com.example.alumni.dto.*;
import com.example.alumni.entity.ReportStatus;
import com.example.alumni.entity.User;
import com.example.alumni.service.AdminService;
import com.example.alumni.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentProfileDTO>> getStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/alumni")
    public ResponseEntity<List<AlumniProfileDTO>> getAlumni() {
        return ResponseEntity.ok(adminService.getAllAlumni());
    }

    @PutMapping("/alumni/{id}/verify")
    public ResponseEntity<AlumniProfileDTO> verifyAlumni(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> payload) {
        boolean verify = true;
        if (payload != null && payload.containsKey("verified")) {
            verify = payload.get("verified");
        }
        return ResponseEntity.ok(adminService.verifyAlumni(id, verify));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportDTO>> getReports() {
        return ResponseEntity.ok(adminService.getAllReports());
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<ReportDTO> updateReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        ReportStatus status = ReportStatus.valueOf(payload.get("status"));
        return ResponseEntity.ok(adminService.updateReportStatus(id, status));
    }

    @PostMapping("/reports")
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody ReportDTO dto) {
        User user = authService.getCurrentUser();
        return new ResponseEntity<>(adminService.createReport(user.getId(), dto), HttpStatus.CREATED);
    }
}
