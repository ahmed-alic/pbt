package com.example.Personal_Budget_Tracker.core.repository;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

