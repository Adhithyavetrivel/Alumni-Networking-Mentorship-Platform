package com.example.alumni.service;

import com.example.alumni.dto.AuthResponse;
import com.example.alumni.dto.LoginRequest;
import com.example.alumni.dto.RegisterRequest;
import com.example.alumni.entity.*;
import com.example.alumni.exception.BadRequestException;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.exception.UnauthorizedException;
import com.example.alumni.repository.AlumniProfileRepository;
import com.example.alumni.repository.StudentProfileRepository;
import com.example.alumni.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       AlumniProfileRepository alumniProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse registerStudent(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.STUDENT
        );
        user = userRepository.save(user);

        StudentProfile profile = new StudentProfile(
                user,
                request.getRegisterNumber(),
                request.getDepartment(),
                request.getGraduationYear(),
                request.getBio(),
                request.getSkills(),
                request.getInterests(),
                request.getCareerGoal()
        );
        studentProfileRepository.save(profile);

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), UUID.randomUUID().toString(), "Student registered successfully");
    }

    @Transactional
    public AuthResponse registerAlumni(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.ALUMNI
        );
        user = userRepository.save(user);

        boolean available = request.getAvailableForMentoring() != null ? request.getAvailableForMentoring() : true;
        AlumniProfile profile = new AlumniProfile(
                user,
                request.getGraduationYear(),
                request.getDepartment(),
                request.getCompany(),
                request.getJobRole(),
                request.getExperience() != null ? request.getExperience() : 0,
                request.getBio(),
                request.getSkills(),
                request.getExpertise(),
                request.getLinkedinUrl(),
                available,
                false // Unverified by default until approved by Admin
        );
        alumniProfileRepository.save(profile);

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), UUID.randomUUID().toString(), "Alumni registered successfully. Your profile is pending Admin verification.");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Your account has been deactivated. Please contact support.");
        }

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), UUID.randomUUID().toString(), "Login successful");
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
