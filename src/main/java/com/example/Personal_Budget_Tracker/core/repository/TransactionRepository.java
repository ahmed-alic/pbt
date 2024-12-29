package com.example.Personal_Budget_Tracker.core.repository;

import java.util.List;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBudgetgoal(BudgetGoal budgetGoal);
}
