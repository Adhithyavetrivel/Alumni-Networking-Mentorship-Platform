package com.example.alumni.controller;

import com.example.alumni.dto.MentorshipRequestDTO;
import com.example.alumni.entity.User;
import com.example.alumni.service.AuthService;
import com.example.alumni.service.MentorshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;
    private final AuthService authService;

    public MentorshipController(MentorshipService mentorshipService, AuthService authService) {
        this.mentorshipService = mentorshipService;
        this.authService = authService;
    }

    @PostMapping("/request")
    public ResponseEntity<MentorshipRequestDTO> createRequest(@RequestBody Map<String, Object> payload) {
        User user = authService.getCurrentUser();
        Long alumniId = Long.valueOf(payload.get("alumniId").toString());
        String message = payload.get("message") != null ? payload.get("message").toString() : "";
        return new ResponseEntity<>(mentorshipService.createRequest(user.getId(), alumniId, message), HttpStatus.CREATED);
    }

    @GetMapping("/student")
    public ResponseEntity<List<MentorshipRequestDTO>> getStudentRequests() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mentorshipService.getStudentRequests(user.getId()));
    }

    @GetMapping("/alumni")
    public ResponseEntity<List<MentorshipRequestDTO>> getAlumniRequests() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mentorshipService.getAlumniRequests(user.getId()));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<MentorshipRequestDTO> acceptRequest(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mentorshipService.acceptRequest(user.getId(), id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<MentorshipRequestDTO> rejectRequest(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mentorshipService.rejectRequest(user.getId(), id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MentorshipRequestDTO> cancelRequest(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(mentorshipService.cancelRequest(user.getId(), id));
    }
}
