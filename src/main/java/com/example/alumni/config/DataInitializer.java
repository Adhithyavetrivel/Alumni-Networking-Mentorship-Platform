package com.example.alumni.config;

import com.example.alumni.entity.*;
import com.example.alumni.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AlumniProfileRepository alumniProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           AlumniProfileRepository alumniProfileRepository,
                           DepartmentRepository departmentRepository,
                           MentorshipRequestRepository mentorshipRequestRepository,
                           MentorshipSessionRepository mentorshipSessionRepository,
                           FeedbackRepository feedbackRepository,
                           ReportRepository reportRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.alumniProfileRepository = alumniProfileRepository;
        this.departmentRepository = departmentRepository;
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.feedbackRepository = feedbackRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> Initializing sample data for Alumni Networking & Mentorship Platform...");

        // 1. Seed 5 Departments
        Department cse = departmentRepository.save(new Department("Computer Science and Engineering", "CSE"));
        Department it = departmentRepository.save(new Department("Information Technology", "IT"));
        Department ece = departmentRepository.save(new Department("Electronics and Communication Engineering", "ECE"));
        Department mech = departmentRepository.save(new Department("Mechanical Engineering", "MECH"));
        Department eee = departmentRepository.save(new Department("Electrical and Electronics Engineering", "EEE"));

        // 2. Seed 1 Admin
        User admin = new User("System Administrator", "admin@alumni.com", passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(admin);

        // 3. Seed 5 Students
        User uStudent1 = userRepository.save(new User("Priya Nair", "priya@student.com", passwordEncoder.encode("student123"), Role.STUDENT));
        StudentProfile sp1 = studentProfileRepository.save(new StudentProfile(
                uStudent1, "21CS042", "Computer Science and Engineering", 2025,
                "Passionate computer science student aiming to build robust distributed backend systems.",
                "Java, Spring Boot, MySQL, REST APIs",
                "Backend Development, Distributed Systems",
                "Software Engineer"
        ));

        User uStudent2 = userRepository.save(new User("Rohan Gupta", "rohan@student.com", passwordEncoder.encode("student123"), Role.STUDENT));
        StudentProfile sp2 = studentProfileRepository.save(new StudentProfile(
                uStudent2, "21IT019", "Information Technology", 2025,
                "Aspiring full-stack engineer passionate about crafting delightful user interfaces and modern web applications.",
                "React, JavaScript, HTML5, CSS3, Node.js",
                "Frontend Development, Web Architecture",
                "Full Stack Developer"
        ));

        User uStudent3 = userRepository.save(new User("Ananya Sharma", "ananya@student.com", passwordEncoder.encode("student123"), Role.STUDENT));
        StudentProfile sp3 = studentProfileRepository.save(new StudentProfile(
                uStudent3, "22EC088", "Electronics and Communication Engineering", 2026,
                "Pre-final year enthusiast exploring data science, machine learning models, and deep learning algorithms.",
                "Python, Machine Learning, Data Analysis, SQL",
                "AI & Data Science, Deep Learning",
                "Data Scientist"
        ));

        User uStudent4 = userRepository.save(new User("Vivek Patel", "vivek@student.com", passwordEncoder.encode("student123"), Role.STUDENT));
        StudentProfile sp4 = studentProfileRepository.save(new StudentProfile(
                uStudent4, "21CS105", "Computer Science and Engineering", 2025,
                "Cloud computing aficionado interested in Kubernetes, container orchestration, and CI/CD pipelines.",
                "Cloud Computing, Docker, Kubernetes, AWS, Linux",
                "DevOps & Cloud, Infrastructure",
                "Cloud Engineer"
        ));

        User uStudent5 = userRepository.save(new User("Sneha Reddy", "sneha@student.com", passwordEncoder.encode("student123"), Role.STUDENT));
        StudentProfile sp5 = studentProfileRepository.save(new StudentProfile(
                uStudent5, "22IT056", "Information Technology", 2026,
                "Eager learner enthusiastic about software engineering, backend databases, and API engineering.",
                "Java, Python, SQL, REST APIs",
                "Software Architecture, Backend Systems",
                "Software Engineer"
        ));

        // 4. Seed 8 Alumni (6 Verified, 2 Pending Verification)
        User uAlumni1 = userRepository.save(new User("Rahul Sharma", "rahul.sharma@google.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap1 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni1, 2018, "Computer Science and Engineering", "Google", "Software Engineer", 6,
                "Senior backend engineer at Google working on large-scale distributed systems and microservices.",
                "Java, Spring Boot, AWS, Microservices, Distributed Systems, MySQL",
                "Backend Development, System Design, Scalable Architectures",
                "https://linkedin.com/in/rahul-sharma-demo",
                true, true
        ));

        User uAlumni2 = userRepository.save(new User("Divya Krishnan", "divya.krishnan@microsoft.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap2 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni2, 2017, "Computer Science and Engineering", "Microsoft", "Cloud Solutions Architect", 8,
                "Cloud architect helping enterprises migrate and scale multi-tenant applications on Azure.",
                "Azure, Docker, Kubernetes, Cloud Computing, Microservices, CI/CD",
                "Cloud Architecture, DevOps & Cloud, Containerization",
                "https://linkedin.com/in/divya-krishnan-demo",
                true, true
        ));

        User uAlumni3 = userRepository.save(new User("Amit Verma", "amit.verma@amazon.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap3 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni3, 2019, "Information Technology", "Amazon", "Data Scientist", 5,
                "Data scientist working on recommendation engines and predictive behavioral analytics at Amazon.",
                "Python, Machine Learning, TensorFlow, SQL, PySpark, Data Analysis",
                "AI & Data Science, Machine Learning, Predictive Modeling",
                "https://linkedin.com/in/amit-verma-demo",
                true, true
        ));

        User uAlumni4 = userRepository.save(new User("Neha Kulkarni", "neha.kulkarni@meta.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap4 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni4, 2018, "Information Technology", "Meta", "Frontend Tech Lead", 7,
                "Leading frontend engineering teams building high-performance web and mobile web experiences.",
                "React, JavaScript, TypeScript, CSS3, HTML5, Web Performance",
                "Frontend Development, Web Architecture, User Experience",
                "https://linkedin.com/in/neha-kulkarni-demo",
                true, true
        ));

        User uAlumni5 = userRepository.save(new User("Karthik Raman", "karthik.raman@apple.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap5 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni5, 2020, "Electronics and Communication Engineering", "Apple", "Software Engineer", 4,
                "Software engineer passionate about full-stack web and mobile application performance.",
                "Swift, Java, REST APIs, React, Python",
                "Full Stack Development, Mobile & Web Applications",
                "https://linkedin.com/in/karthik-raman-demo",
                true, true
        ));

        User uAlumni6 = userRepository.save(new User("Pooja Mehta", "pooja.mehta@uber.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap6 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni6, 2021, "Computer Science and Engineering", "Uber", "Software Engineer II", 3,
                "Core backend engineer working on Uber's real-time matching and dispatch microservices.",
                "Java, Spring Boot, Kafka, MySQL, Go",
                "Backend Development, High-Throughput Messaging",
                "https://linkedin.com/in/pooja-mehta-demo",
                true, true
        ));

        User uAlumni7 = userRepository.save(new User("Vikram Joshi", "vikram.joshi@tcs.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap7 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni7, 2022, "Mechanical Engineering", "Tata Consultancy Services", "Systems Engineer", 2,
                "Systems engineer developing enterprise Java solutions for manufacturing and supply chain.",
                "Java, SQL, Hibernate, Spring",
                "Enterprise Applications, Supply Chain Systems",
                "https://linkedin.com/in/vikram-joshi-demo",
                true, false // Unverified - for admin demo
        ));

        User uAlumni8 = userRepository.save(new User("Shalini Saxena", "shalini.saxena@infosys.com", passwordEncoder.encode("alumni123"), Role.ALUMNI));
        AlumniProfile ap8 = alumniProfileRepository.save(new AlumniProfile(
                uAlumni8, 2023, "Electrical and Electronics Engineering", "Infosys", "Associate Consultant", 1,
                "Associate consultant working with Python and cloud backend applications.",
                "Python, Django, SQL, PostgreSQL",
                "Web Applications, Database Management",
                "https://linkedin.com/in/shalini-saxena-demo",
                true, false // Unverified - for admin demo
        ));

        // 5. Seed Mentorship Requests
        // Request 1: Priya -> Rahul Sharma (ACCEPTED)
        MentorshipRequest req1 = new MentorshipRequest(uStudent1, uAlumni1, "Hi Rahul, I am keen on mastering Spring Boot microservices and system design. Would love your mentorship!");
        req1.setStatus(RequestStatus.ACCEPTED);
        req1.setRespondedAt(LocalDateTime.now().minusDays(10));
        req1 = mentorshipRequestRepository.save(req1);

        // Request 2: Priya -> Pooja Mehta (PENDING)
        MentorshipRequest req2 = new MentorshipRequest(uStudent1, uAlumni6, "Hello Pooja, I noticed your work on high-throughput Kafka streaming at Uber. I'd love to learn about message brokers.");
        req2.setStatus(RequestStatus.PENDING);
        req2 = mentorshipRequestRepository.save(req2);

        // Request 3: Rohan -> Neha Kulkarni (ACCEPTED)
        MentorshipRequest req3 = new MentorshipRequest(uStudent2, uAlumni4, "Hi Neha, I am passionate about React architecture and design systems. Looking forward to your guidance.");
        req3.setStatus(RequestStatus.ACCEPTED);
        req3.setRespondedAt(LocalDateTime.now().minusDays(8));
        req3 = mentorshipRequestRepository.save(req3);

        // Request 4: Ananya -> Amit Verma (ACCEPTED)
        MentorshipRequest req4 = new MentorshipRequest(uStudent3, uAlumni3, "Dear Amit, I am preparing for Data Science roles. Could we connect for advice on ML portfolio projects?");
        req4.setStatus(RequestStatus.ACCEPTED);
        req4.setRespondedAt(LocalDateTime.now().minusDays(5));
        req4 = mentorshipRequestRepository.save(req4);

        // Request 5: Vivek -> Divya Krishnan (PENDING)
        MentorshipRequest req5 = new MentorshipRequest(uStudent4, uAlumni2, "Hi Divya, I am preparing for Kubernetes and Cloud certification. Would appreciate your architectural tips.");
        req5.setStatus(RequestStatus.PENDING);
        req5 = mentorshipRequestRepository.save(req5);

        // Request 6: Sneha -> Rahul Sharma (REJECTED for demo)
        MentorshipRequest req6 = new MentorshipRequest(uStudent5, uAlumni1, "Hi Rahul, requesting mentorship on backend engineering.");
        req6.setStatus(RequestStatus.REJECTED);
        req6.setRespondedAt(LocalDateTime.now().minusDays(2));
        req6 = mentorshipRequestRepository.save(req6);

        // 6. Seed Mentorship Sessions
        // Session 1: Completed Session (Rahul & Priya)
        MentorshipSession sess1 = new MentorshipSession(req1, "Introduction to Microservices Architecture", LocalDateTime.now().minusDays(7), "Overview of microservices design patterns, API gateways, and database-per-service principles.");
        sess1.setStatus(SessionStatus.COMPLETED);
        sess1 = mentorshipSessionRepository.save(sess1);

        // Feedback for Session 1
        Feedback fb1 = new Feedback(sess1, uStudent1, uAlumni1, 5, "Rahul was incredibly knowledgeable and structured our session brilliantly. He answered all my questions about system design!");
        feedbackRepository.save(fb1);

        // Session 2: Confirmed Upcoming Session (Rahul & Priya)
        MentorshipSession sess2 = new MentorshipSession(req1, "Mock System Design Interview", LocalDateTime.now().plusDays(3).withHour(17).withMinute(0), "Practice system design for URL Shortener and rate-limiting components.");
        sess2.setStatus(SessionStatus.CONFIRMED);
        mentorshipSessionRepository.save(sess2);

        // Session 3: Completed Session (Neha & Rohan)
        MentorshipSession sess3 = new MentorshipSession(req3, "React 19 & Component Design Review", LocalDateTime.now().minusDays(4), "Walkthrough of state management with Zustand and reusable UI component best practices.");
        sess3.setStatus(SessionStatus.COMPLETED);
        sess3 = mentorshipSessionRepository.save(sess3);

        // Feedback for Session 3
        Feedback fb2 = new Feedback(sess3, uStudent2, uAlumni4, 5, "Neha gave fantastic feedback on my frontend code! I feel much more confident about component hierarchies.");
        feedbackRepository.save(fb2);

        // Session 4: Confirmed Session (Amit & Ananya)
        MentorshipSession sess4 = new MentorshipSession(req4, "Data Science Portfolio Review", LocalDateTime.now().plusDays(5).withHour(18).withMinute(30), "Reviewing GitHub machine learning projects and resume enhancement.");
        sess4.setStatus(SessionStatus.CONFIRMED);
        mentorshipSessionRepository.save(sess4);

        // 7. Seed Sample Report (for Admin)
        Report report = new Report(
                uStudent1,
                uAlumni7,
                "Inappropriate profile content",
                "The user profile description contains non-professional placeholder phrases."
        );
        report.setStatus(ReportStatus.PENDING);
        reportRepository.save(report);

        System.out.println(">>> Sample data initialization completed successfully!");
    }
}
