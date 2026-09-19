package com.example.alumni.service;

import com.example.alumni.dto.*;
import com.example.alumni.entity.*;
import com.example.alumni.exception.BadRequestException;
import com.example.alumni.exception.ResourceNotFoundException;
import com.example.alumni.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final ReportRepository reportRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentService studentService;

    public AdminService(UserRepository userRepository,
                        StudentProfileRepository studentProfileRepository,
                        AlumniProfileRepository alumniProfileRepository,
                        MentorshipRequestRepository mentorshipRequestRepository,
                        MentorshipSessionRepository mentorshipSessionRepository,
                        ReportRepository reportRepository,
                        DepartmentRepository departmentRepository,
                        StudentService studentService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.reportRepository = reportRepository;
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
    }

    public AdminDashboardDTO getDashboardStats() {
        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalAlumni = userRepository.countByRole(Role.ALUMNI);
        long verifiedAlumni = alumniProfileRepository.countByVerifiedTrue();
        long pendingVerification = alumniProfileRepository.countByVerifiedFalse();
        long activeMentorships = mentorshipRequestRepository.countByStatus(RequestStatus.ACCEPTED);
        long completedSessions = mentorshipSessionRepository.countByStatus(SessionStatus.COMPLETED);
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);

        return new AdminDashboardDTO(
                totalStudents,
                totalAlumni,
                verifiedAlumni,
                pendingVerification,
                activeMentorships,
                completedSessions,
                pendingReports
        );
    }

    public List<StudentProfileDTO> getAllStudents() {
        return studentProfileRepository.findAll().stream()
                .map(studentService::toDTO)
                .collect(Collectors.toList());
    }

    public List<AlumniProfileDTO> getAllAlumni() {
        return alumniProfileRepository.findAll().stream()
                .map(studentService::toAlumniDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlumniProfileDTO verifyAlumni(Long alumniId, boolean verify) {
        AlumniProfile profile = alumniProfileRepository.findById(alumniId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni profile not found with ID: " + alumniId));

        profile.setVerified(verify);
        profile = alumniProfileRepository.save(profile);
        return studentService.toAlumniDTO(profile);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin accounts cannot be deleted");
        }

        if (user.getRole() == Role.STUDENT) {
            studentProfileRepository.findByUserId(userId)
                    .ifPresent(studentProfileRepository::delete);
        } else if (user.getRole() == Role.ALUMNI) {
            alumniProfileRepository.findByUserId(userId)
                    .ifPresent(alumniProfileRepository::delete);
        }

        userRepository.delete(user);
    }

    public List<ReportDTO> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportDTO updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));

        report.setStatus(status);
        report = reportRepository.save(report);
        return toReportDTO(report);
    }

    @Transactional
    public ReportDTO createReport(Long reporterId, ReportDTO dto) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));
        User reported = userRepository.findById(dto.getReportedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Reported user not found"));

        Report report = new Report(reporter, reported, dto.getReason(), dto.getDescription());
        report = reportRepository.save(report);
        return toReportDTO(report);
    }

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentDTO(d.getId(), d.getName(), d.getCode()))
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        if (departmentRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Department code already exists: " + dto.getCode());
        }
        if (departmentRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Department name already exists: " + dto.getName());
        }
        Department dept = new Department(dto.getName(), dto.getCode());
        dept = departmentRepository.save(dept);
        return new DepartmentDTO(dept.getId(), dept.getName(), dept.getCode());
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        departmentRepository.delete(dept);
    }

    private ReportDTO toReportDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setReporterId(report.getReporter().getId());
        dto.setReporterName(report.getReporter().getName());
        dto.setReportedUserId(report.getReportedUser().getId());
        dto.setReportedUserName(report.getReportedUser().getName());
        dto.setReason(report.getReason());
        dto.setDescription(report.getDescription());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());
        return dto;
    }
}
