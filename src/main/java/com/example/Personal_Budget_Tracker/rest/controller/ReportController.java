package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.service.ReportService;
import com.example.Personal_Budget_Tracker.core.service.PDFExportService;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.PDFExportRequest;
import com.example.Personal_Budget_Tracker.rest.dto.ErrorResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("End date cannot be before start date"));
        }
        Map<String, Object> report = reportService.generateMonthlyReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/category")
    public ResponseEntity<?> getCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("End date cannot be before start date"));
        }
        Map<String, Object> report = reportService.generateCategoryReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly/export")
    public ResponseEntity<?> exportMonthlyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("End date cannot be before start date"));
        }
        byte[] pdfBytes = reportService.exportMonthlyReportPdf(startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "monthly-report.pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/category/export")
    public ResponseEntity<?> exportCategoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("End date cannot be before start date"));
        }
        byte[] pdfBytes = reportService.exportCategoryReportPdf(startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "category-report.pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportReportPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<String> categories,
            @RequestParam String reportType) {
        
        logger.info("Received request to export {} report as PDF from {} to {}", reportType, startDate, endDate);
        
        try {
            String filename = reportType.toLowerCase() + "-report-" + 
                    startDate.format(DateTimeFormatter.ISO_DATE) + "-to-" + 
                    endDate.format(DateTimeFormatter.ISO_DATE) + ".pdf";
            
            byte[] pdfContent = pdfExportService.generateReportPDF(startDate, endDate, categories);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
