package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
import com.example.Personal_Budget_Tracker.rest.dto.PDFExportRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PDFExportService {
    private static final Logger logger = LoggerFactory.getLogger(PDFExportService.class);
    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 16;
    private static final float FONT_SIZE_HEADING = 14;
    private static final float FONT_SIZE_NORMAL = 12;
    private static final float LINE_HEIGHT = 1.5f;

    private final ReportService reportService;

    public PDFExportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public byte[] generatePDF(PDFExportRequest request) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            Map<String, Object> reportData;
            String title;

            if ("monthly".equalsIgnoreCase(request.getReportType())) {
                reportData = reportService.generateMonthlyReport(request.getStartDate(), request.getEndDate());
                title = "Monthly Spending Report";
            } else if ("category".equalsIgnoreCase(request.getReportType())) {
                reportData = reportService.generateCategoryReport(request.getStartDate(), request.getEndDate());
                title = "Category Spending Report";
            } else {
                throw new IllegalArgumentException("Invalid report type: " + request.getReportType());
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - MARGIN;

                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_TITLE);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= FONT_SIZE_TITLE * LINE_HEIGHT;

                // Add date range
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(String.format("Period: %s to %s", 
                    request.getStartDate(), request.getEndDate()));
                contentStream.endText();
                yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT * 2;

                // Add report data
                contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
                if (reportData.containsKey("monthlyTotals")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> monthlyTotals = (Map<String, Double>) reportData.get("monthlyTotals");
                    for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, yPosition);
                        contentStream.showText(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
                        contentStream.endText();
                        yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;
                    }
                } else if (reportData.containsKey("categoryTotals")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Double> categoryTotals = (Map<String, Double>) reportData.get("categoryTotals");
                    for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(MARGIN, yPosition);
                        contentStream.showText(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
                        contentStream.endText();
                        yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;
                    }
                }

                // Add total
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition - FONT_SIZE_NORMAL * LINE_HEIGHT);
                contentStream.showText(String.format("Total Spending: $%.2f", reportData.get("totalSpending")));
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            logger.error("Error generating PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    public byte[] generateReportPDF(LocalDate startDate, LocalDate endDate, List<String> categories) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Get report data
            MonthlyReportResponse reportData = reportService.getMonthlySpending(startDate, endDate, categories);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - MARGIN;

                // Add report header
                yPosition = addReportHeader(contentStream, yPosition, startDate, endDate);

                // Add summary section
                yPosition = addSummarySection(contentStream, yPosition, reportData);

                // Add detailed breakdown
                yPosition = addDetailedBreakdown(contentStream, yPosition, reportData.getSpendingByCategory());

                // Add footer
                addFooter(contentStream, page);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            logger.error("Error generating PDF report", e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private float addReportHeader(PDPageContentStream contentStream, float yPosition, 
                                LocalDate startDate, LocalDate endDate) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        
        // Add title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_TITLE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Monthly Spending Report");
        contentStream.endText();
        
        yPosition -= FONT_SIZE_TITLE * LINE_HEIGHT;

        // Add date range
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(String.format("Period: %s - %s", 
            formatter.format(startDate), formatter.format(endDate)));
        contentStream.endText();

        return yPosition - (FONT_SIZE_NORMAL * LINE_HEIGHT * 2);
    }

    private float addSummarySection(PDPageContentStream contentStream, float yPosition, 
                                  MonthlyReportResponse report) throws IOException {
        // Add summary title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_HEADING);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Summary");
        contentStream.endText();
        
        yPosition -= FONT_SIZE_HEADING * LINE_HEIGHT;

        // Add total spending
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(String.format("Total Spending: $%.2f", report.getTotalSpending()));
        contentStream.endText();

        return yPosition - (FONT_SIZE_NORMAL * LINE_HEIGHT * 2);
    }

    private float addDetailedBreakdown(PDPageContentStream contentStream, float yPosition,
                                     List<MonthlySpendingDTO> spendingByCategory) throws IOException {
        // Add breakdown title
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_HEADING);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Category Breakdown");
        contentStream.endText();
        
        yPosition -= FONT_SIZE_HEADING * LINE_HEIGHT;

        // Add column headers
        float col1X = MARGIN;
        float col2X = MARGIN + 150;
        float col3X = MARGIN + 250;
        float col4X = MARGIN + 350;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(col1X, yPosition);
        contentStream.showText("Category");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(col2X, yPosition);
        contentStream.showText("Amount");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(col3X, yPosition);
        contentStream.showText("Percentage");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(col4X, yPosition);
        contentStream.showText("Count");
        contentStream.endText();

        yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;

        // Add category rows
        contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
        for (MonthlySpendingDTO spending : spendingByCategory) {
            contentStream.beginText();
            contentStream.newLineAtOffset(col1X, yPosition);
            contentStream.showText(spending.getCategory());
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(col2X, yPosition);
            contentStream.showText(String.format("$%.2f", spending.getAmount()));
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(col3X, yPosition);
            contentStream.showText(String.format("%.1f%%", spending.getPercentage()));
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(col4X, yPosition);
            contentStream.showText(String.valueOf(spending.getTransactionCount()));
            contentStream.endText();

            yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;
        }

        return yPosition;
    }

    private void addFooter(PDPageContentStream contentStream, PDPage page) throws IOException {
        float footerY = MARGIN;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(MARGIN, footerY);
        contentStream.showText("Generated on " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        contentStream.endText();
    }
}
