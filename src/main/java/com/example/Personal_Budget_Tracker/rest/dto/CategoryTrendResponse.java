package com.example.Personal_Budget_Tracker.rest.dto;

import java.util.List;
import java.util.Map;

public class CategoryTrendResponse {
    private List<MonthlyTrendData> trends;

    public CategoryTrendResponse() {}

    public CategoryTrendResponse(List<MonthlyTrendData> trends) {
        this.trends = trends;
    }

    public List<MonthlyTrendData> getTrends() {
        return trends;
    }

    public void setTrends(List<MonthlyTrendData> trends) {
        this.trends = trends;
    }
}
