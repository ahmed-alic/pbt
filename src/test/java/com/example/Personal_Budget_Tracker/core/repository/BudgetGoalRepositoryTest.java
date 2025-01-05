package com.example.Personal_Budget_Tracker.core.repository;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetGoalRepositoryTest {
    @Mock
    private BudgetGoalRepository budgetGoalRepository;

    @Test
    void whenSaveBudgetGoal_thenReturnSavedBudgetGoal() {
        // Arrange
        BudgetGoal goal = new BudgetGoal();
        goal.setAmount(1000.0);
        goal.setName("Monthly Groceries");
        
        when(budgetGoalRepository.save(any(BudgetGoal.class))).thenReturn(goal);

        // Act
        BudgetGoal savedGoal = budgetGoalRepository.save(goal);

        // Assert
        assertNotNull(savedGoal);
        assertEquals(1000.0, savedGoal.getAmount());
        assertEquals("Monthly Groceries", savedGoal.getName());
        verify(budgetGoalRepository).save(any(BudgetGoal.class));
    }

    @Test
    void whenFindById_thenReturnBudgetGoal() {
        // Arrange
        BudgetGoal goal = new BudgetGoal();
        goal.setId(1L);
        goal.setAmount(1000.0);
        goal.setName("Monthly Groceries");
        
        when(budgetGoalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act
        Optional<BudgetGoal> found = budgetGoalRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(1000.0, found.get().getAmount());
        assertEquals("Monthly Groceries", found.get().getName());
        verify(budgetGoalRepository).findById(1L);
    }

    @Test
    void whenFindAll_thenReturnBudgetGoalList() {
        // Arrange
        BudgetGoal goal1 = new BudgetGoal();
        goal1.setAmount(1000.0);
        goal1.setName("Monthly Groceries");
        
        BudgetGoal goal2 = new BudgetGoal();
        goal2.setAmount(500.0);
        goal2.setName("Entertainment");
        
        List<BudgetGoal> goals = Arrays.asList(goal1, goal2);
        
        when(budgetGoalRepository.findAll()).thenReturn(goals);

        // Act
        List<BudgetGoal> foundGoals = budgetGoalRepository.findAll();

        // Assert
        assertNotNull(foundGoals);
        assertEquals(2, foundGoals.size());
        verify(budgetGoalRepository).findAll();
    }

    @Test
    void whenDeleteBudgetGoal_thenVerifyDeletion() {
        // Arrange
        Long goalId = 1L;
        doNothing().when(budgetGoalRepository).deleteById(goalId);

        // Act
        budgetGoalRepository.deleteById(goalId);

        // Assert
        verify(budgetGoalRepository).deleteById(goalId);
    }
}
