package com.example.alumni.controller;

import com.example.alumni.dto.FeedbackDTO;
import com.example.alumni.entity.User;
import com.example.alumni.service.AuthService;
import com.example.alumni.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    public FeedbackController(FeedbackService feedbackService, AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> submitFeedback(@Valid @RequestBody FeedbackDTO dto) {
        User user = authService.getCurrentUser();
        return new ResponseEntity<>(feedbackService.submitFeedback(user.getId(), dto), HttpStatus.CREATED);
    }

    @GetMapping("/alumni/{id}")
    public ResponseEntity<List<FeedbackDTO>> getAlumniFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackForAlumni(id));
    }
}
