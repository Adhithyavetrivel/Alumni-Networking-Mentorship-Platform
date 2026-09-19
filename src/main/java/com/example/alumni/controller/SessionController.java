package com.example.alumni.controller;

import com.example.alumni.dto.MentorshipSessionDTO;
import com.example.alumni.entity.User;
import com.example.alumni.service.AuthService;
import com.example.alumni.service.SessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final AuthService authService;

    public SessionController(SessionService sessionService, AuthService authService) {
        this.sessionService = sessionService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<MentorshipSessionDTO> scheduleSession(@RequestBody Map<String, Object> payload) {
        User user = authService.getCurrentUser();
        Long mentorshipRequestId = Long.valueOf(payload.get("mentorshipRequestId").toString());
        String topic = payload.get("topic").toString();
        String sessionDateStr = payload.get("sessionDate").toString();
        LocalDateTime sessionDate = LocalDateTime.parse(sessionDateStr);
        String description = payload.get("description") != null ? payload.get("description").toString() : "";

        return new ResponseEntity<>(
                sessionService.scheduleSession(user.getId(), mentorshipRequestId, topic, sessionDate, description),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<MentorshipSessionDTO>> getSessions() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(sessionService.getSessionsForUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorshipSessionDTO> getSessionById(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(sessionService.getSessionById(id, user.getId(), user.getRole()));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<MentorshipSessionDTO> confirmSession(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(sessionService.confirmSession(user.getId(), id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MentorshipSessionDTO> completeSession(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(sessionService.completeSession(user.getId(), id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MentorshipSessionDTO> cancelSession(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(sessionService.cancelSession(user.getId(), id));
    }
}
