package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.service.BudgetGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetGoalControllerTest {

    @Mock
    private BudgetGoalService budgetGoalService;

    @InjectMocks
    private BudgetGoalController budgetGoalController;

    private BudgetGoal testBudgetGoal;

    @BeforeEach
    void setUp() {
        testBudgetGoal = new BudgetGoal();
        testBudgetGoal.setId(1L);
        testBudgetGoal.setName("Monthly Groceries");
        testBudgetGoal.setAmount(500.0);
    }

    @Test
    void getAllBudgetGoals_ReturnsListOfBudgetGoals() {
        // Arrange
        List<BudgetGoal> budgetGoals = Arrays.asList(testBudgetGoal);
        when(budgetGoalService.getAllBudgetGoals()).thenReturn(budgetGoals);

        // Act
        ResponseEntity<List<BudgetGoal>> response = budgetGoalController.getAllBudgetGoals();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budgetGoals, response.getBody());
        verify(budgetGoalService).getAllBudgetGoals();
    }

    @Test
    void getAllBudgetGoals_WhenError_ReturnsInternalServerError() {
        // Arrange
        when(budgetGoalService.getAllBudgetGoals()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<BudgetGoal>> response = budgetGoalController.getAllBudgetGoals();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(budgetGoalService).getAllBudgetGoals();
    }

    @Test
    void getBudgetGoalById_WithValidId_ReturnsBudgetGoal() {
        // Arrange
        Long goalId = 1L;
        when(budgetGoalService.getBudgetGoalById(goalId)).thenReturn(testBudgetGoal);

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.getBudgetGoalById(goalId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBudgetGoal, response.getBody());
        verify(budgetGoalService).getBudgetGoalById(goalId);
    }

    @Test
    void getBudgetGoalById_WhenNotFound_ReturnsNotFound() {
        // Arrange
        Long goalId = 1L;
        when(budgetGoalService.getBudgetGoalById(goalId))
            .thenThrow(new RuntimeException("Budget goal not found"));

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.getBudgetGoalById(goalId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(budgetGoalService).getBudgetGoalById(goalId);
    }

    @Test
    void createBudgetGoal_WithValidData_ReturnsCreatedBudgetGoal() {
        // Arrange
        when(budgetGoalService.createBudgetGoal(any(BudgetGoal.class))).thenReturn(testBudgetGoal);

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.createBudgetGoal(testBudgetGoal);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testBudgetGoal, response.getBody());
        verify(budgetGoalService).createBudgetGoal(testBudgetGoal);
    }

    @Test
    void createBudgetGoal_WhenError_ReturnsBadRequest() {
        // Arrange
        when(budgetGoalService.createBudgetGoal(any(BudgetGoal.class)))
            .thenThrow(new IllegalArgumentException("Invalid budget goal data"));

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.createBudgetGoal(testBudgetGoal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(budgetGoalService).createBudgetGoal(testBudgetGoal);
    }

    @Test
    void updateBudgetGoal_WithValidData_ReturnsUpdatedBudgetGoal() {
        // Arrange
        Long goalId = 1L;
        when(budgetGoalService.updateBudgetGoal(eq(goalId), any(BudgetGoal.class))).thenReturn(testBudgetGoal);

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.updateBudgetGoal(goalId, testBudgetGoal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testBudgetGoal, response.getBody());
        verify(budgetGoalService).updateBudgetGoal(goalId, testBudgetGoal);
    }

    @Test
    void updateBudgetGoal_WhenNotFound_ReturnsNotFound() {
        // Arrange
        Long goalId = 1L;
        when(budgetGoalService.updateBudgetGoal(eq(goalId), any(BudgetGoal.class)))
            .thenThrow(new RuntimeException("Budget goal not found"));

        // Act
        ResponseEntity<BudgetGoal> response = budgetGoalController.updateBudgetGoal(goalId, testBudgetGoal);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(budgetGoalService).updateBudgetGoal(goalId, testBudgetGoal);
    }

    @Test
    void deleteBudgetGoal_WithValidId_ReturnsNoContent() {
        // Arrange
        Long goalId = 1L;
        doNothing().when(budgetGoalService).deleteBudgetGoal(goalId);

        // Act
        ResponseEntity<Void> response = budgetGoalController.deleteBudgetGoal(goalId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(budgetGoalService).deleteBudgetGoal(goalId);
    }

    @Test
    void deleteBudgetGoal_WhenError_ReturnsInternalServerError() {
        // Arrange
        Long goalId = 1L;
        doThrow(new RuntimeException("Error deleting budget goal"))
            .when(budgetGoalService).deleteBudgetGoal(goalId);

        // Act
        ResponseEntity<Void> response = budgetGoalController.deleteBudgetGoal(goalId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(budgetGoalService).deleteBudgetGoal(goalId);
    }
}
