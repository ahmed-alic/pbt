package com.example.Personal_Budget_Tracker.rest.dto;

import java.time.LocalDate;
import java.util.List;

public class PDFExportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> selectedCategories;
    private String template;
    private String reportType;

    // Default constructor
    public PDFExportRequest() {
    }

    // Constructor with all fields
    public PDFExportRequest(LocalDate startDate, LocalDate endDate, List<String> selectedCategories, String template) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedCategories = selectedCategories;
        this.template = template;
    }

    public PDFExportRequest(LocalDate startDate, LocalDate endDate, String reportType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportType = reportType;
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

    public List<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    @Override
    public String toString() {
        return "PDFExportRequest{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", selectedCategories=" + selectedCategories +
                ", template='" + template + '\'' +
                ", reportType='" + reportType + '\'' +
                '}';
    }
}
