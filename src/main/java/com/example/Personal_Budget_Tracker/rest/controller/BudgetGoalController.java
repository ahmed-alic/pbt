package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.service.BudgetGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/budget-goal")
public class BudgetGoalController {
    private final BudgetGoalService budgetGoalService;
    private final Logger logger = LoggerFactory.getLogger(BudgetGoalController.class);

    public BudgetGoalController(BudgetGoalService budgetGoalService) {
        this.budgetGoalService = budgetGoalService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BudgetGoal>> getAllBudgetGoals() {
        try {
            return ResponseEntity.ok(budgetGoalService.getAllBudgetGoals());
        } catch (Exception e) {
            logger.error("Error getting all budget goals: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetGoal> createBudgetGoal(@RequestBody BudgetGoal budgetGoal) {
        try {
            BudgetGoal createdGoal = budgetGoalService.createBudgetGoal(budgetGoal);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid budget goal data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating budget goal: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetGoal> getBudgetGoalById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(budgetGoalService.getBudgetGoalById(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.error("Budget goal not found with ID {}: {}", id, e.getMessage());
                return ResponseEntity.notFound().build();
            }
            logger.error("Error getting budget goal with ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BudgetGoal> updateBudgetGoal(@PathVariable Long id, @RequestBody BudgetGoal budgetGoal) {
        try {
            return ResponseEntity.ok(budgetGoalService.updateBudgetGoal(id, budgetGoal));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.error("Budget goal not found with ID {}: {}", id, e.getMessage());
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating budget goal with ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudgetGoal(@PathVariable Long id) {
        try {
            budgetGoalService.deleteBudgetGoal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.error("Budget goal not found with ID {}: {}", id, e.getMessage());
                return ResponseEntity.notFound().build();
            }
            logger.error("Error deleting budget goal with ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
