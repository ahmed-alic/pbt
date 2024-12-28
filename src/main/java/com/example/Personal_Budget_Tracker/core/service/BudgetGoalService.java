package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.repository.BudgetGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetGoalService {

    private final BudgetGoalRepository budgetGoalRepository;

    // Constructor for dependency injection
    public BudgetGoalService(BudgetGoalRepository budgetGoalRepository) {
        this.budgetGoalRepository = budgetGoalRepository;
    }

    public List<BudgetGoal> getAllBudgetGoals() {
        return budgetGoalRepository.findAll();
    }

    public BudgetGoal getBudgetGoalById(Long id) {
        return budgetGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget Goal not found with ID: " + id));
    }

    public BudgetGoal addBudgetGoal(BudgetGoal budgetGoal) {
        return budgetGoalRepository.save(budgetGoal);
    }

    public BudgetGoal updateBudgetGoal(Long id, BudgetGoal updatedGoal) {
        BudgetGoal existingGoal = getBudgetGoalById(id);
        existingGoal.setAmount(updatedGoal.getAmount());
        existingGoal.setTimePeriod(updatedGoal.getTimePeriod());
        existingGoal.setCurrentSpending(updatedGoal.getCurrentSpending());
        return budgetGoalRepository.save(existingGoal);
    }

    public void deleteBudgetGoal(Long id) {
        budgetGoalRepository.deleteById(id);
    }

    public BudgetGoal createBudgetGoal(BudgetGoal budgetGoal) {
        return null;
    }
}
