package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.service.ReportService;
import com.example.Personal_Budget_Tracker.core.service.PDFExportService;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.PDFExportRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {
    private final ReportService reportService;
    private final PDFExportService pdfExportService;
    private final Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportService reportService, PDFExportService pdfExportService) {
        this.reportService = reportService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/monthly-spending")
    public ResponseEntity<MonthlyReportResponse> getMonthlySpending(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> categories) {
        
        logger.info("Received request for monthly spending report from {} to {}", startDate, endDate);
        
        try {
            MonthlyReportResponse report = reportService.getMonthlySpending(startDate, endDate, categories);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Error generating monthly spending report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportReportPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> categories) {
        
        logger.info("Received request to export PDF report from {} to {}", startDate, endDate);
        
        try {
            byte[] pdfContent = pdfExportService.generateReportPDF(startDate, endDate, categories);
            
            String filename = String.format("spending-report-%s-to-%s.pdf",
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfContent);

        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
