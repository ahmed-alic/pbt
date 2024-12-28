package com.example.Personal_Budget_Tracker.core.repository;

import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetGoalRepository extends JpaRepository<BudgetGoal, Long> {
}

