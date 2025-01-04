package com.example.Personal_Budget_Tracker.api;

import com.example.Personal_Budget_Tracker.api.config.TestConfig;
import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
class BudgetGoalApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private BudgetGoal createTestBudgetGoal() {
        BudgetGoal budgetGoal = new BudgetGoal();
        budgetGoal.setName("Test Budget Goal");
        budgetGoal.setAmount(500.0);
        budgetGoal.setTimePeriod("Monthly");
        return budgetGoal;
    }

    @Test
    void getAllBudgetGoals_ReturnsSuccessfully() {
        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
            createURLWithPort("/api/budget-goal/"),
            List.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createBudgetGoal_WithValidData_ReturnsCreatedBudgetGoal() {
        // Arrange
        BudgetGoal budgetGoal = createTestBudgetGoal();

        // Act
        ResponseEntity<BudgetGoal> response = restTemplate.postForEntity(
            createURLWithPort("/api/budget-goal/create"),
            budgetGoal,
            BudgetGoal.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Budget Goal", response.getBody().getName());
        assertEquals(500.0, response.getBody().getAmount());
        assertEquals("Monthly", response.getBody().getTimePeriod());
    }

    @Test
    void createBudgetGoal_WithInvalidAmount_ReturnsBadRequest() {
        // Arrange
        BudgetGoal budgetGoal = createTestBudgetGoal();
        budgetGoal.setAmount(-100.0); // Invalid negative amount

        // Act
        ResponseEntity<BudgetGoal> response = restTemplate.postForEntity(
            createURLWithPort("/api/budget-goal/create"),
            budgetGoal,
            BudgetGoal.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getBudgetGoalById_ExistingGoal_ReturnsBudgetGoal() {
        // Arrange
        // First create a budget goal
        BudgetGoal budgetGoal = createTestBudgetGoal();
        ResponseEntity<BudgetGoal> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/budget-goal/create"),
            budgetGoal,
            BudgetGoal.class
        );
        BudgetGoal createdGoal = createResponse.getBody();

        // Act
        ResponseEntity<BudgetGoal> response = restTemplate.getForEntity(
            createURLWithPort("/api/budget-goal/" + createdGoal.getId()),
            BudgetGoal.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdGoal.getId(), response.getBody().getId());
    }

    @Test
    void updateBudgetGoal_WithValidData_ReturnsUpdatedBudgetGoal() {
        // Arrange
        // First create a budget goal
        BudgetGoal budgetGoal = createTestBudgetGoal();
        ResponseEntity<BudgetGoal> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/budget-goal/create"),
            budgetGoal,
            BudgetGoal.class
        );
        BudgetGoal createdGoal = createResponse.getBody();
        assertNotNull(createdGoal, "Budget goal creation failed");

        // Update the goal
        createdGoal.setName("Updated Goal");
        createdGoal.setAmount(400.0);
        createdGoal.setTimePeriod("Weekly");

        // Act
        ResponseEntity<BudgetGoal> response = restTemplate.exchange(
            createURLWithPort("/api/budget-goal/update/" + createdGoal.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(createdGoal),
            BudgetGoal.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Goal", response.getBody().getName());
        assertEquals(400.0, response.getBody().getAmount());
        assertEquals("Weekly", response.getBody().getTimePeriod());
    }

    @Test
    void deleteBudgetGoal_ExistingGoal_ReturnsNoContent() {
        // Arrange
        // First create a budget goal
        BudgetGoal budgetGoal = createTestBudgetGoal();
        ResponseEntity<BudgetGoal> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/budget-goal/create"),
            budgetGoal,
            BudgetGoal.class
        );
        BudgetGoal createdGoal = createResponse.getBody();
        assertNotNull(createdGoal, "Budget goal creation failed");

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/api/budget-goal/" + createdGoal.getId()),
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
