package com.example.Personal_Budget_Tracker.rest.dto;

import java.util.Map;

public class MonthlyTrendData {
    private String month; // Format: YYYY-MM
    private Map<String, Double> categoryData;

    public MonthlyTrendData() {}

    public MonthlyTrendData(String month, Map<String, Double> categoryData) {
        this.month = month;
        this.categoryData = categoryData;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Map<String, Double> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(Map<String, Double> categoryData) {
        this.categoryData = categoryData;
    }
}
