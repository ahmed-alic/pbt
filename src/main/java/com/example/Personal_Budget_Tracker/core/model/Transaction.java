package com.example.Personal_Budget_Tracker.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String type; // Income or Expense
    private String description;
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budgetgoal_id", nullable = true)
    private BudgetGoal budgetgoal;

    // Constructor for Dependency Injection
    public Transaction(Double amount, String type, String description, LocalDate date, Category category) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
        this.category = category;
    }

    // Default Constructor
    public Transaction() {}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BudgetGoal getBudgetgoal() {
        return budgetgoal;
    }

    public void setBudgetgoal(BudgetGoal budgetgoal) {
        this.budgetgoal = budgetgoal;
    }
}
