package com.example.Personal_Budget_Tracker.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class BudgetGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String timePeriod; // e.g., Monthly, Yearly
    private Double currentSpending;

    // Constructor for Dependency Injection
    public BudgetGoal(Double amount, String timePeriod, Double currentSpending) {
        this.amount = amount;
        this.timePeriod = timePeriod;
        this.currentSpending = currentSpending;
    }

    // Default Constructor
    public BudgetGoal() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Double getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(Double currentSpending) {
        this.currentSpending = currentSpending;
    }
}
