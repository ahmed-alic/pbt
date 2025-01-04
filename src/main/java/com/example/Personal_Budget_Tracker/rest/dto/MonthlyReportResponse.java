package com.example.Personal_Budget_Tracker.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class MonthlyReportResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalSpending;
    private List<MonthlySpendingDTO> spendingByCategory;

    // Default constructor
    public MonthlyReportResponse() {}

    // Constructor with all fields
    public MonthlyReportResponse(LocalDate startDate, LocalDate endDate, Double totalSpending, List<MonthlySpendingDTO> spendingByCategory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSpending = totalSpending;
        this.spendingByCategory = spendingByCategory;
    }

    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(Double totalSpending) {
        this.totalSpending = totalSpending;
    }

    public List<MonthlySpendingDTO> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void setSpendingByCategory(List<MonthlySpendingDTO> spendingByCategory) {
        this.spendingByCategory = spendingByCategory;
    }
}
