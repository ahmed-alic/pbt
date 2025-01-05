package com.example.Personal_Budget_Tracker.api;

import com.example.Personal_Budget_Tracker.api.config.TestConfig;
import com.example.Personal_Budget_Tracker.rest.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
class ReportApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    void getMonthlyReport_ReturnsSuccessfully() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/monthly?startDate=%s&endDate=%s", 
                startDate, endDate)),
            Map.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getCategoryReport_ReturnsSuccessfully() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        // Act
        ResponseEntity<Map> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/category?startDate=%s&endDate=%s", 
                startDate, endDate)),
            Map.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void exportMonthlyReport_ReturnsPdfResource() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        // Act
        ResponseEntity<Resource> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/monthly/export?startDate=%s&endDate=%s", 
                startDate, endDate)),
            Resource.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/pdf"));
    }

    @Test
    void exportCategoryReport_ReturnsPdfResource() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        // Act
        ResponseEntity<Resource> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/category/export?startDate=%s&endDate=%s", 
                startDate, endDate)),
            Resource.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/pdf"));
    }

    @Test
    void getMonthlyReport_WithInvalidDateRange_ReturnsBadRequest() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusMonths(1); // Invalid: end date before start date

        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/monthly?startDate=%s&endDate=%s", 
                startDate, endDate)),
            ErrorResponse.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("End date cannot be before start date", response.getBody().getMessage());
    }

    @Test
    void getCategoryReport_WithInvalidDateRange_ReturnsBadRequest() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusMonths(1); // Invalid: end date before start date

        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            createURLWithPort(String.format("/api/reports/category?startDate=%s&endDate=%s", 
                startDate, endDate)),
            ErrorResponse.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("End date cannot be before start date", response.getBody().getMessage());
    }
}
