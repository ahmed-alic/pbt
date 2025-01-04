package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import com.example.Personal_Budget_Tracker.core.service.BudgetGoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/budget-goal")
public class BudgetGoalController {
    private final BudgetGoalService budgetGoalService;

    public BudgetGoalController(BudgetGoalService budgetGoalService) {
        this.budgetGoalService = budgetGoalService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BudgetGoal>> getAllBudgetGoals() {
        return ResponseEntity.ok(budgetGoalService.getAllBudgetGoals());
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetGoal> createBudgetGoal(@RequestBody BudgetGoal budgetGoal) {
        return ResponseEntity.ok(budgetGoalService.createBudgetGoal(budgetGoal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetGoal> getBudgetGoalById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetGoalService.getBudgetGoalById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BudgetGoal> updateBudgetGoal(@PathVariable Long id, @RequestBody BudgetGoal budgetGoal) {
        return ResponseEntity.ok(budgetGoalService.updateBudgetGoal(id, budgetGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudgetGoal(@PathVariable Long id) {
        budgetGoalService.deleteBudgetGoal(id);
        return ResponseEntity.noContent().build();
    }
}
