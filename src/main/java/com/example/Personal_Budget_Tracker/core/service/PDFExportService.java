package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
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
        contentStream.showText("Spending by Category");
        contentStream.endText();
        
        yPosition -= FONT_SIZE_HEADING * LINE_HEIGHT * 1.5f;

        // Add table headers
        float[] columnWidths = {200f, 100f, 100f, 100f};
        String[] headers = {"Category", "Amount", "Percentage", "Transactions"};
        
        float xPosition = MARGIN;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(xPosition, yPosition);
        
        for (int i = 0; i < headers.length; i++) {
            contentStream.showText(headers[i]);
            contentStream.newLineAtOffset(columnWidths[i], 0);
        }
        contentStream.endText();
        
        yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;

        // Add table rows
        for (MonthlySpendingDTO spending : spendingByCategory) {
            if (yPosition < MARGIN + 50) { // Check if we need a new page
                contentStream.close();
                PDPage newPage = new PDPage(PDRectangle.A4);
                yPosition = newPage.getMediaBox().getHeight() - MARGIN;
                // TODO: Handle page breaks properly
            }

            xPosition = MARGIN;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE_NORMAL);
            contentStream.newLineAtOffset(xPosition, yPosition);
            
            // Category
            contentStream.showText(spending.getCategory());
            contentStream.newLineAtOffset(columnWidths[0], 0);
            
            // Amount
            contentStream.showText(String.format("$%.2f", spending.getAmount()));
            contentStream.newLineAtOffset(columnWidths[1], 0);
            
            // Percentage
            contentStream.showText(String.format("%.1f%%", spending.getPercentage()));
            contentStream.newLineAtOffset(columnWidths[2], 0);
            
            // Transaction count
            contentStream.showText(String.valueOf(spending.getTransactionCount()));
            
            contentStream.endText();
            
            yPosition -= FONT_SIZE_NORMAL * LINE_HEIGHT;
        }

        return yPosition;
    }

    private void addFooter(PDPageContentStream contentStream, PDPage page) throws IOException {
        float yPosition = MARGIN;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Generated on " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        contentStream.endText();
    }
}
