package com.example.Personal_Budget_Tracker.rest.dto;

public class MonthlySpendingDTO {
    private String category;
    private Double amount;
    private Double percentage;
    private Integer transactionCount;

    // Default constructor
    public MonthlySpendingDTO() {}

    // Constructor with all fields
    public MonthlySpendingDTO(String category, Double amount, Double percentage, Integer transactionCount) {
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
        this.transactionCount = transactionCount;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
