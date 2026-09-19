package com.example.alumni;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class AlumniNetworkApplicationTests {

    @Test
    void contextLoads() {
        // Verifies complete Spring Boot context loading, JPA entity mappings,
        // repositories, and DataInitializer execution with in-memory H2.
    }
}
