package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
import com.example.Personal_Budget_Tracker.rest.dto.CategoryTrendResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyTrendData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(ReportService.class);

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlyReportResponse getMonthlySpending(LocalDate startDate, LocalDate endDate, List<String> categories) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        if (categories != null && !categories.isEmpty()) {
            transactions = transactions.stream()
                    .filter(t -> t.getCategory() != null && categories.contains(t.getCategory().getName()))
                    .collect(Collectors.toList());
        }

        // Calculate total spending
        double totalSpending = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Group transactions by category name
        Map<String, List<Transaction>> byCategory = transactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(t -> t.getCategory().getName()));

        // Create spending DTOs for each category
        List<MonthlySpendingDTO> spendingByCategory = byCategory.entrySet().stream()
                .map(entry -> {
                    double amount = entry.getValue().stream()
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    return new MonthlySpendingDTO(
                            entry.getKey(),
                            amount,
                            (amount / totalSpending) * 100,
                            entry.getValue().size()
                    );
                })
                .collect(Collectors.toList());

        return new MonthlyReportResponse(startDate, endDate, totalSpending, spendingByCategory);
    }

    public Map<String, Object> generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        Map<String, Double> monthlyTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("monthlyTotals", monthlyTotals);
        report.put("totalSpending", monthlyTotals.values().stream().mapToDouble(Double::doubleValue).sum());

        return report;
    }

    public Map<String, Object> generateCategoryReport(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        Map<String, Double> categoryTotals = transactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("categoryTotals", categoryTotals);
        report.put("totalSpending", categoryTotals.values().stream().mapToDouble(Double::doubleValue).sum());

        return report;
    }

    public CategoryTrendResponse getCategoryTrends(LocalDate startDate, LocalDate endDate, List<String> categories) {
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        // Group transactions by month and category
        Map<YearMonth, Map<String, Double>> monthlyData = transactions.stream()
            .filter(t -> t.getCategory() != null && 
                        (categories == null || categories.isEmpty() || 
                         categories.contains(t.getCategory().getName())))
            .collect(Collectors.groupingBy(
                t -> YearMonth.from(t.getDate()),
                Collectors.groupingBy(
                    t -> t.getCategory().getName(),
                    Collectors.summingDouble(Transaction::getAmount)
                )
            ));

        // Create a sorted list of all months in the range
        List<YearMonth> allMonths = new ArrayList<>();
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);
        while (!current.isAfter(end)) {
            allMonths.add(current);
            current = current.plusMonths(1);
        }

        // Get all unique categories
        Set<String> allCategories = transactions.stream()
            .filter(t -> t.getCategory() != null)
            .map(t -> t.getCategory().getName())
            .collect(Collectors.toSet());

        // Create trend data for each month
        List<MonthlyTrendData> trends = allMonths.stream()
            .map(month -> {
                Map<String, Double> categoryData = new HashMap<>();
                Map<String, Double> monthData = monthlyData.getOrDefault(month, new HashMap<>());
                
                // Ensure all categories are present in each month's data
                allCategories.forEach(category -> 
                    categoryData.put(category, monthData.getOrDefault(category, 0.0))
                );
                
                return new MonthlyTrendData(
                    month.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    categoryData
                );
            })
            .collect(Collectors.toList());

        return new CategoryTrendResponse(trends);
    }

    public byte[] exportMonthlyReportPdf(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> reportData = generateMonthlyReport(startDate, endDate);
        return generatePdfReport(reportData, "Monthly Spending Report");
    }

    public byte[] exportCategoryReportPdf(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> reportData = generateCategoryReport(startDate, endDate);
        return generatePdfReport(reportData, "Category Spending Report");
    }

    private byte[] generatePdfReport(Map<String, Object> reportData, String title) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(title);
                contentStream.endText();

                // Add date range
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText(String.format("Period: %s to %s",
                        reportData.get("startDate"),
                        reportData.get("endDate")));
                contentStream.endText();

                // Add totals
                float y = 680;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                
                if (reportData.containsKey("monthlyTotals")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> monthlyTotals = (Map<String, Double>) reportData.get("monthlyTotals");
                    for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
                        contentStream.endText();
                        y -= 20;
                    }
                } else if (reportData.containsKey("categoryTotals")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> categoryTotals = (Map<String, Double>) reportData.get("categoryTotals");
                    for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
                        contentStream.endText();
                        y -= 20;
                    }
                }

                // Add total spending
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, y - 20);
                contentStream.showText(String.format("Total Spending: $%.2f", reportData.get("totalSpending")));
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
}
