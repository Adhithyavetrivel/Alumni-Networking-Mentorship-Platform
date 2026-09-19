# Alumni Networking & Mentorship Platform

> A comprehensive, full-stack college web application built with **Spring Boot 3.3.4**, **Spring Data JPA**, **Spring Security**, **MySQL**, and **Bootstrap 5**. Designed to seamlessly connect college students with verified alumni for career guidance, 1-on-1 mentorship sessions, and smart algorithmic mentor matching.

---

## 1. Project Overview

The **Alumni Networking & Mentorship Platform** bridges the communication gap between university students and graduated alumni working across top global companies (Google, Microsoft, Amazon, Meta, Apple, Uber, TCS, Infosys).

### Core Highlights
- **Role-Based Portals**: Dedicated, interactive dashboards and workflows for **Students**, **Alumni**, and **Administrators**.
- **Rule-Based Mentor Recommendation Engine**: Automatic match score calculation based on **Skills (50%)**, **Interests (30%)**, and **Career Goals (20%)** computed locally in Java without third-party AI APIs.
- **End-to-End Mentorship Lifecycle**: Search mentors &rarr; Send request &rarr; Alumni accepts/rejects &rarr; Schedule 1-on-1 session &rarr; Mark session complete &rarr; Submit ratings & reviews.
- **Administrative Governance**: Account verification workflows, user management, misconduct reporting queue, and real-time Chart.js analytics.
- **Self-Seeded Demo Data**: Includes 1 Admin, 5 Students, 8 Alumni, 5 Departments, requests, sessions, and reviews pre-populated on first run.

---

## 2. Technology Stack

### Backend
- **Language**: Java 17 / 21
- **Framework**: Spring Boot 3.3.4
- **Web Layer**: Spring Web (REST APIs + Static Web Page Serving)
- **Data Persistence**: Spring Data JPA & Hibernate 6
- **Security**: Spring Security 6 (BCrypt password hashing, session management, role-based authorization)
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Apache Maven

### Frontend
- **Markup & Styling**: HTML5, CSS3, Bootstrap 5.3.3
- **Icons**: Bootstrap Icons 1.11.3
- **Scripting**: Vanilla JavaScript (Async/Await `fetch()`, zero external frameworks like React/Angular/Node)
- **Visual Analytics**: Chart.js 4.x

### Database
- **Primary**: MySQL 8.0+
- **Fallback / Quick Test**: In-Memory H2 Database (`-Dspring-boot.run.profiles=h2`)

---

## 3. Application Roles & Capabilities

| Role | Key Capabilities |
| :--- | :--- |
| **STUDENT** | Register, login, update academic profile & skills, search & filter alumni, view intelligent match percentages, send mentorship requests, cancel pending requests, schedule sessions, view history, and submit star ratings and reviews. |
| **ALUMNI** | Register, login, maintain corporate profile (company, role, experience, skills, expertise, LinkedIn), toggle mentoring availability, review incoming requests (accept/reject), view active mentees, manage sessions (confirm, complete, cancel), and receive student feedback. |
| **ADMIN** | System dashboard with analytics charts, audit student & alumni directories, verify/unverify alumni accounts, delete inappropriate accounts, manage academic departments, and review reported safety flags. |

---

## 4. Rule-Based Mentor Recommendation Algorithm

When a student accesses the **Find Mentors** page, the system automatically compares the student's profile attributes against all verified, available alumni using the formula:

$$\text{Match Percentage} = (\text{Skill Match} \times 0.50) + (\text{Interest Match} \times 0.30) + (\text{Career Goal Match} \times 0.20)$$

### Weight Breakdown
1. **Skill Match (50%)**: Tokenizes student skills and alumni skills (case-insensitive substring and set intersection) to determine coverage.
2. **Interest Match (30%)**: Evaluates student technical interests against alumni areas of expertise and professional bio.
3. **Career Goal Match (20%)**: Compares the student's target job title (e.g. *Software Engineer*) with the alumni's current job role.

Alumni mentors are automatically ranked and presented in descending order of match percentage.

---

## 5. Database Design & Entity Relationships

The MySQL database is named `alumni_network`. The schema consists of 8 relational tables:

```
                  +---------------+
                  |  departments  |
                  +---------------+
                          
                  +---------------+
                  |     users     |
                  +---------------+
                     |         |
          1-to-1     |         |     1-to-1
    +----------------+         +----------------+
    |                                           |
    v                                           v
+------------------+                    +------------------+
| student_profiles |                    | alumni_profiles  |
+------------------+                    +------------------+
        |                                       |
        +------------------+ +------------------+
                           | |
                           v v
                +---------------------+
                | mentorship_requests |
                +---------------------+
                           | 1-to-Many
                           v
                +---------------------+
                | mentorship_sessions |
                +---------------------+
                           | 1-to-1
                           v
                +---------------------+
                |      feedbacks      |
                +---------------------+

       +---------------------------------------------+
       | reports (reporter_id, reported_user_id)    |
       +---------------------------------------------+
```

### Table Specifications
1. **`users`**: `id`, `name`, `email` (unique), `password` (BCrypt encoded), `role` (STUDENT, ALUMNI, ADMIN), `enabled`, `created_at`.
2. **`departments`**: `id`, `name` (unique), `code` (unique).
3. **`student_profiles`**: `id`, `user_id` (FK), `register_number`, `department`, `graduation_year`, `bio`, `skills`, `interests`, `career_goal`.
4. **`alumni_profiles`**: `id`, `user_id` (FK), `graduation_year`, `department`, `company`, `job_role`, `experience`, `bio`, `skills`, `expertise`, `linkedin_url`, `available_for_mentoring`, `verified`.
5. **`mentorship_requests`**: `id`, `student_id` (FK), `alumni_id` (FK), `message`, `status` (PENDING, ACCEPTED, REJECTED, CANCELLED), `requested_at`, `responded_at`.
6. **`mentorship_sessions`**: `id`, `mentorship_request_id` (FK), `topic`, `session_date`, `description`, `status` (REQUESTED, CONFIRMED, COMPLETED, CANCELLED), `created_at`.
7. **`feedbacks`**: `id`, `session_id` (FK), `student_id` (FK), `alumni_id` (FK), `rating` (1 to 5), `comment`, `created_at`.
8. **`reports`**: `id`, `reporter_id` (FK), `reported_user_id` (FK), `reason`, `description`, `status` (PENDING, REVIEWED, RESOLVED, DISMISSED), `created_at`.

---

## 6. Complete REST API Reference

### Authentication APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register/student` | Register a new student account and profile | Public |
| `POST` | `/api/auth/register/alumni` | Register a new alumni account (pending verification) | Public |
| `POST` | `/api/auth/login` | Authenticate user credentials & create session | Public |
| `GET` | `/api/auth/me` | Fetch currently authenticated user session | Authenticated |
| `POST` | `/api/auth/logout` | Invalidate session & logout | Authenticated |

### Student APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/students/profile` | Retrieve student's academic profile | STUDENT |
| `PUT` | `/api/students/profile` | Update student profile, skills, and goals | STUDENT |
| `GET` | `/api/students/alumni` | List all verified alumni mentors | STUDENT, ADMIN |
| `GET` | `/api/students/alumni/{id}` | View detailed mentor profile | STUDENT, ADMIN |
| `GET` | `/api/students/alumni/search` | Filter mentors by skill, company, role, dept, year | STUDENT, ADMIN |
| `GET` | `/api/students/recommendations` | Get rule-based mentor recommendations with % match | STUDENT |
| `GET` | `/api/students/dashboard` | Student dashboard counts | STUDENT |

### Alumni APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/alumni/profile` | Retrieve alumni professional profile | ALUMNI |
| `PUT` | `/api/alumni/profile` | Update corporate details, experience, skills | ALUMNI |
| `PUT` | `/api/alumni/availability` | Toggle availability for new mentorship requests | ALUMNI |
| `GET` | `/api/alumni/mentees` | View active student mentees | ALUMNI |
| `GET` | `/api/alumni/dashboard` | Alumni dashboard metrics and average review score | ALUMNI |

### Mentorship Request APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/mentorship/request` | Send mentorship request to verified alumni | STUDENT |
| `GET` | `/api/mentorship/student` | View student's sent mentorship requests | STUDENT |
| `GET` | `/api/mentorship/alumni` | View incoming mentorship requests | ALUMNI |
| `PUT` | `/api/mentorship/{id}/accept` | Accept pending mentorship request | ALUMNI |
| `PUT` | `/api/mentorship/{id}/reject` | Reject pending mentorship request | ALUMNI |
| `PUT` | `/api/mentorship/{id}/cancel` | Cancel pending mentorship request | STUDENT |

### Mentoring Session APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/sessions` | Schedule 1-on-1 session for accepted mentorship | STUDENT, ALUMNI |
| `GET` | `/api/sessions` | View scheduled sessions for logged-in user | Authenticated |
| `GET` | `/api/sessions/{id}` | View specific session details | Authenticated |
| `PUT` | `/api/sessions/{id}/confirm` | Confirm requested session | ALUMNI |
| `PUT` | `/api/sessions/{id}/complete` | Mark session as completed | STUDENT, ALUMNI |
| `PUT` | `/api/sessions/{id}/cancel` | Cancel session | STUDENT, ALUMNI |

### Feedback APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/feedback` | Submit star rating (1-5) and review for completed session | STUDENT |
| `GET` | `/api/feedback/alumni/{id}` | View all reviews and ratings for an alumni mentor | Authenticated |

### Administrator APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/dashboard` | System metrics (total users, sessions, reports) | ADMIN |
| `GET` | `/api/admin/students` | List all students | ADMIN |
| `GET` | `/api/admin/alumni` | List all alumni | ADMIN |
| `PUT` | `/api/admin/alumni/{id}/verify` | Verify or unverify an alumni account | ADMIN |
| `DELETE` | `/api/admin/users/{id}` | Delete user account and associated profile | ADMIN |
| `GET` | `/api/admin/reports` | View reported misconduct flags | ADMIN |
| `PUT` | `/api/admin/reports/{id}` | Update report status (REVIEWED, RESOLVED, DISMISSED) | ADMIN |
| `POST` | `/api/admin/reports` | Submit report against an inappropriate profile | Authenticated |

### Department APIs
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/departments` | List all academic departments | Public |
| `POST` | `/api/departments` | Create a new department | ADMIN |
| `DELETE` | `/api/departments/{id}` | Remove a department | ADMIN |

---

## 7. Pre-Seeded Demo Login Credentials

The `DataInitializer` automatically populates the following accounts on first startup:

| Role | Name | Email | Password | Details |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | System Administrator | `admin@alumni.com` | `admin123` | Full access to administration, verification, and reports |
| **STUDENT** | Priya Nair | `priya@student.com` | `student123` | CSE, Skills: Java, Spring Boot, MySQL (Goal: Software Engineer) |
| **STUDENT** | Rohan Gupta | `rohan@student.com` | `student123` | IT, Skills: React, JavaScript, CSS (Goal: Full Stack Developer) |
| **STUDENT** | Ananya Sharma | `ananya@student.com` | `student123` | ECE, Skills: Python, Machine Learning (Goal: Data Scientist) |
| **STUDENT** | Vivek Patel | `vivek@student.com` | `student123` | CSE, Skills: Docker, Kubernetes, AWS (Goal: Cloud Engineer) |
| **STUDENT** | Sneha Reddy | `sneha@student.com` | `student123` | IT, Skills: Java, Python, SQL (Goal: Software Engineer) |
| **ALUMNI** | Rahul Sharma | `rahul.sharma@google.com` | `alumni123` | **Google** (Senior Software Engineer, 6 yrs exp) &bull; Verified |
| **ALUMNI** | Divya Krishnan | `divya.krishnan@microsoft.com` | `alumni123` | **Microsoft** (Cloud Architect, 8 yrs exp) &bull; Verified |
| **ALUMNI** | Amit Verma | `amit.verma@amazon.com` | `alumni123` | **Amazon** (Data Scientist, 5 yrs exp) &bull; Verified |
| **ALUMNI** | Neha Kulkarni | `neha.kulkarni@meta.com` | `alumni123` | **Meta** (Frontend Tech Lead, 7 yrs exp) &bull; Verified |
| **ALUMNI** | Vikram Joshi | `vikram.joshi@tcs.com` | `alumni123` | **TCS** (Systems Engineer) &bull; *Pending Admin Verification* |

---

## 8. Installation & Setup Instructions

### Prerequisites
- **Java Development Kit (JDK)**: JDK 17, 21, or 26
- **Apache Maven**: 3.8+
- **MySQL Server**: 8.0+

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd "Student Alumini Networking"
```

### Step 2: Configure MySQL
Create a database in your local MySQL instance (the application will also create it automatically if user permissions allow):
```sql
CREATE DATABASE IF NOT EXISTS alumni_network;
```

Set your MySQL password using an environment variable (recommended) or update `src/main/resources/application.properties`:

**Windows PowerShell:**
```powershell
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your_mysql_password"
```

**Linux / macOS:**
```bash
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="your_mysql_password"
```

### Step 3: Build the Application
```bash
mvn clean install
```

### Step 4: Run the Application
```bash
mvn spring-boot:run
```

> **Instant Standalone Mode (H2 in-memory)**:
> If MySQL is not configured locally or you wish to run a quick test without starting MySQL:
> ```bash
> mvn spring-boot:run -Dspring-boot.run.profiles=h2
> ```

### Step 5: Access the Platform
Open your browser and navigate to:
```
http://localhost:8080
```

---

## 9. End-to-End Demonstration Walkthrough

Follow these steps during your project demonstration or college viva:

1. **Student Registration & Login**:
   - Navigate to `/register.html`, select **I am a Student**, fill the registration form, and submit.
   - Or click **Student (Priya)** on the `/login.html` demo buttons.
2. **Profile & Recommendation Demonstration**:
   - Go to `/student-profile.html` to review skills (`Java, Spring Boot, MySQL`) and target role (`Software Engineer`).
   - Click **Find Mentors** (`/find-mentors.html`) &rarr; Observe how **Rahul Sharma at Google** receives the highest match percentage based on the 50%/30%/20% rule!
3. **Sending a Mentorship Request**:
   - Click **Request Mentorship** on Rahul Sharma's card, enter a personalized note, and submit.
   - View your pending request in `/mentorship-requests.html`.
4. **Alumni Login & Acceptance**:
   - Log out, then log in as Rahul Sharma (`rahul.sharma@google.com` / `alumni123`).
   - Open `/incoming-requests.html` and click **Accept**.
   - Navigate to `/mentees.html` to see Priya listed as an active mentee.
5. **Scheduling & Completing a Session**:
   - On `/alumni-sessions.html`, click **Propose Session**, select Priya, pick tomorrow's time, and submit.
   - Click **Mark Complete** once the mentoring session concludes.
6. **Student Feedback Submission**:
   - Switch back to Priya's student account and open `/sessions.html`.
   - Click **Give Feedback**, rate 5 stars, leave a review, and submit.
7. **Admin Governance & Verification**:
   - Log in as Admin (`admin@alumni.com` / `admin123`).
   - View the analytics charts on `/admin-dashboard.html`.
   - Go to `/manage-alumni.html` to review Vikram Joshi's pending profile and click **Verify**.
   - Check `/manage-reports.html` to inspect any safety flags.

---

## 10. Viva / Examination Discussion Points

When presenting this project in a viva, explain the key architectural decisions:
- **Layered Architecture**: Clear separation of concerns (`Controller` &rarr; `Service` &rarr; `Repository` &rarr; `Entity`).
- **DTO Isolation**: Entities are never exposed directly to REST APIs; DTOs protect sensitive credentials (e.g. passwords) and avoid Hibernate circular serialization loops.
- **Rule-Based Matching**: Explain how the Java scoring algorithm parses, cleans, and weights skills, interests, and goals to provide deterministic, transparent recommendations.
- **Spring Security Integration**: Discuss `BCryptPasswordEncoder` for cryptographic password hashing, role-based authorization rules in `SecurityConfig`, and custom authentication handlers.

---

## 11. Future Enhancements
- Real-time chat messaging using WebSockets (`STOMP`).
- Video conference integration (e.g., Jitsi Meet / Zoom API).
- Automated email notifications for session invitations and request confirmations.
- Resume upload and automated keyword extraction using Apache Tika.
