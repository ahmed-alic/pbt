package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.BudgetGoalRepository;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetGoalServiceTest {

    @Mock
    private BudgetGoalRepository budgetGoalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetGoalService budgetGoalService;

    private BudgetGoal testBudgetGoal;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testBudgetGoal = new BudgetGoal();
        testBudgetGoal.setId(1L);
        testBudgetGoal.setName("Test Budget Goal");
        testBudgetGoal.setAmount(1000.0);
        testBudgetGoal.setTimePeriod("Monthly");
        testBudgetGoal.setCurrentSpending(500.0);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(100.0);
        testTransaction.setType("Expense");
        testTransaction.setBudgetgoal(testBudgetGoal);
    }

    @Test
    void getAllBudgetGoals_ReturnsAllGoals() {
        // Arrange
        List<BudgetGoal> expectedGoals = Arrays.asList(testBudgetGoal);
        when(budgetGoalRepository.findAll()).thenReturn(expectedGoals);

        // Act
        List<BudgetGoal> actualGoals = budgetGoalService.getAllBudgetGoals();

        // Assert
        assertEquals(expectedGoals, actualGoals);
        verify(budgetGoalRepository).findAll();
    }

    @Test
    void getBudgetGoalById_WithValidId_ReturnsBudgetGoal() {
        // Arrange
        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.of(testBudgetGoal));

        // Act
        BudgetGoal actualGoal = budgetGoalService.getBudgetGoalById(1L);

        // Assert
        assertEquals(testBudgetGoal, actualGoal);
        verify(budgetGoalRepository).findById(1L);
    }

    @Test
    void getBudgetGoalById_WithInvalidId_ThrowsException() {
        // Arrange
        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> budgetGoalService.getBudgetGoalById(1L));
        verify(budgetGoalRepository).findById(1L);
    }

    @Test
    void createBudgetGoal_WithValidData_ReturnsSavedGoal() {
        // Arrange
        when(budgetGoalRepository.save(any(BudgetGoal.class))).thenReturn(testBudgetGoal);

        // Act
        BudgetGoal createdGoal = budgetGoalService.createBudgetGoal(testBudgetGoal);

        // Assert
        assertEquals(testBudgetGoal, createdGoal);
        assertEquals(0.0, createdGoal.getCurrentSpending());
        verify(budgetGoalRepository).save(any(BudgetGoal.class));
    }

    @Test
    void createBudgetGoal_WithNegativeAmount_ThrowsException() {
        // Arrange
        testBudgetGoal.setAmount(-100.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> budgetGoalService.createBudgetGoal(testBudgetGoal));
        verify(budgetGoalRepository, never()).save(any(BudgetGoal.class));
    }

    @Test
    void createBudgetGoal_WithEmptyTimePeriod_ThrowsException() {
        // Arrange
        testBudgetGoal.setTimePeriod("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> budgetGoalService.createBudgetGoal(testBudgetGoal));
        verify(budgetGoalRepository, never()).save(any(BudgetGoal.class));
    }

    @Test
    void updateBudgetGoal_WithValidData_ReturnsUpdatedGoal() {
        // Arrange
        BudgetGoal updatedGoal = new BudgetGoal();
        updatedGoal.setName("Updated Goal");
        updatedGoal.setAmount(2000.0);
        updatedGoal.setTimePeriod("Weekly");
        updatedGoal.setCurrentSpending(700.0);

        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.of(testBudgetGoal));
        when(budgetGoalRepository.save(any(BudgetGoal.class))).thenReturn(updatedGoal);

        // Act
        BudgetGoal result = budgetGoalService.updateBudgetGoal(1L, updatedGoal);

        // Assert
        assertEquals(updatedGoal.getName(), result.getName());
        assertEquals(updatedGoal.getAmount(), result.getAmount());
        assertEquals(updatedGoal.getTimePeriod(), result.getTimePeriod());
        assertEquals(updatedGoal.getCurrentSpending(), result.getCurrentSpending());
        verify(budgetGoalRepository).save(any(BudgetGoal.class));
    }

    @Test
    void deleteBudgetGoal_WithValidId_DeletesGoalAndUpdatesTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.of(testBudgetGoal));
        when(transactionRepository.findByBudgetgoal(testBudgetGoal)).thenReturn(transactions);

        // Act
        budgetGoalService.deleteBudgetGoal(1L);

        // Assert
        verify(budgetGoalRepository).findById(1L);
        verify(transactionRepository).findByBudgetgoal(testBudgetGoal);
        verify(transactionRepository).save(testTransaction);
        verify(budgetGoalRepository).deleteById(1L);
    }

    @Test
    void updateBudgetGoalSpending_WithExpenseTransaction_UpdatesSpending() {
        // Arrange
        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.of(testBudgetGoal));
        when(budgetGoalRepository.save(any(BudgetGoal.class))).thenReturn(testBudgetGoal);

        // Act
        BudgetGoal updatedGoal = budgetGoalService.updateBudgetGoalSpending(testTransaction);

        // Assert
        assertNotNull(updatedGoal);
        assertEquals(600.0, updatedGoal.getCurrentSpending()); // 500 + 100
        verify(budgetGoalRepository).save(any(BudgetGoal.class));
    }

    @Test
    void updateBudgetGoalSpending_WithIncomeTransaction_DoesNotUpdateSpending() {
        // Arrange
        testTransaction.setType("Income");

        // Act
        BudgetGoal result = budgetGoalService.updateBudgetGoalSpending(testTransaction);

        // Assert
        assertNull(result);
        verify(budgetGoalRepository, never()).save(any(BudgetGoal.class));
    }

    @Test
    void updateBudgetGoalSpending_WithNullBudgetGoal_DoesNotUpdateSpending() {
        // Arrange
        testTransaction.setBudgetgoal(null);

        // Act
        BudgetGoal result = budgetGoalService.updateBudgetGoalSpending(testTransaction);

        // Assert
        assertNull(result);
        verify(budgetGoalRepository, never()).save(any(BudgetGoal.class));
    }
}
