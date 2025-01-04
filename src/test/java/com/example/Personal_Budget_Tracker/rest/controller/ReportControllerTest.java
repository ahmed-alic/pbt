package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.service.ReportService;
import com.example.Personal_Budget_Tracker.core.service.PDFExportService;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
import com.example.Personal_Budget_Tracker.rest.dto.PDFExportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private PDFExportService pdfExportService;

    @InjectMocks
    private ReportController reportController;

    private Transaction testTransaction;
    private LocalDate startDate;
    private LocalDate endDate;
    private MonthlyReportResponse testReport;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(100.0);
        testTransaction.setDescription("Grocery shopping");
        testTransaction.setDate(LocalDate.now());

        startDate = LocalDate.now().minusMonths(1);
        endDate = LocalDate.now();

        // Create test report data
        testReport = new MonthlyReportResponse();
        testReport.setTotalSpending(500.0);
        List<MonthlySpendingDTO> spendingList = new ArrayList<>();
        MonthlySpendingDTO spending = new MonthlySpendingDTO();
        spending.setCategory("Groceries");
        spending.setAmount(500.0);
        spending.setPercentage(100.0);
        spendingList.add(spending);
        testReport.setSpendingByCategory(spendingList);
    }

    @Test
    void getMonthlySpending_ReturnsReportData() {
        // Arrange
        when(reportService.getMonthlySpending(eq(startDate), eq(endDate), any()))
            .thenReturn(testReport);

        // Act
        ResponseEntity<MonthlyReportResponse> response = reportController.getMonthlySpending(startDate, endDate, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500.0, response.getBody().getTotalSpending());
        verify(reportService).getMonthlySpending(eq(startDate), eq(endDate), any());
    }

    @Test
    void getMonthlySpending_WhenError_ReturnsInternalServerError() {
        // Arrange
        when(reportService.getMonthlySpending(any(), any(), any()))
            .thenThrow(new RuntimeException("Error generating report"));

        // Act
        ResponseEntity<MonthlyReportResponse> response = reportController.getMonthlySpending(startDate, endDate, null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reportService).getMonthlySpending(eq(startDate), eq(endDate), any());
    }

    @Test
    void exportReportPDF_ReturnsPdfResource() {
        // Arrange
        byte[] pdfContent = "PDF Content".getBytes();
        when(pdfExportService.generateReportPDF(eq(startDate), eq(endDate), any()))
            .thenReturn(pdfContent);

        // Act
        ResponseEntity<byte[]> response = reportController.exportReportPDF(startDate, endDate, null, "monthly");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getHeaders().getContentType().equals(MediaType.APPLICATION_PDF));
        verify(pdfExportService).generateReportPDF(eq(startDate), eq(endDate), any());
    }

    @Test
    void exportReportPDF_WhenError_ReturnsInternalServerError() {
        // Arrange
        when(pdfExportService.generateReportPDF(eq(startDate), eq(endDate), any()))
            .thenThrow(new RuntimeException("Error generating PDF"));

        // Act
        ResponseEntity<byte[]> response = reportController.exportReportPDF(startDate, endDate, null, "monthly");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(pdfExportService).generateReportPDF(eq(startDate), eq(endDate), any());
    }
}
