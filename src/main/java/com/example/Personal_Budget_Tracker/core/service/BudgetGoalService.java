package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.BudgetGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public BudgetGoal updateBudgetGoalSpending(Transaction transaction) {
        // Only update spending for Expense transactions
        if (transaction.getType() != null && transaction.getType().equals("Expense") && 
            transaction.getBudgetgoal() != null) {
            
            BudgetGoal budgetGoal = getBudgetGoalById(transaction.getBudgetgoal().getId());
            
            // Update current spending
            double currentSpending = budgetGoal.getCurrentSpending() != null 
                ? budgetGoal.getCurrentSpending() 
                : 0.0;
            budgetGoal.setCurrentSpending(currentSpending + transaction.getAmount());
            
            return budgetGoalRepository.save(budgetGoal);
        }
        return null;
    }

    public BudgetGoal createBudgetGoal(BudgetGoal budgetGoal) {
        if (budgetGoal.getAmount() <= 0) {
            throw new IllegalArgumentException("Budget goal amount must be positive");
        }
        if (budgetGoal.getTimePeriod() == null || budgetGoal.getTimePeriod().trim().isEmpty()) {
            throw new IllegalArgumentException("Time period must be specified");
        }
        budgetGoal.setCurrentSpending(0.0); // Initialize current spending to 0
        return budgetGoalRepository.save(budgetGoal);
    }
}
