package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    private Transaction transaction1;
    private Transaction transaction2;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Groceries");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Entertainment");

        transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(100.0);
        transaction1.setDate(LocalDate.of(2024, 1, 15));
        transaction1.setCategory(category1);

        transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200.0);
        transaction2.setDate(LocalDate.of(2024, 1, 20));
        transaction2.setCategory(category2);
    }

    @Test
    void getMonthlySpending_WithoutCategories_ReturnsAllTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

        // Act
        MonthlyReportResponse response = reportService.getMonthlySpending(startDate, endDate, null);

        // Assert
        assertNotNull(response);
        assertEquals(300.0, response.getTotalSpending());
        assertEquals(2, response.getSpendingByCategory().size());
        verify(transactionRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    void getMonthlySpending_WithCategories_ReturnsFilteredTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        List<String> categories = Arrays.asList("Groceries");
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

        // Act
        MonthlyReportResponse response = reportService.getMonthlySpending(startDate, endDate, categories);

        // Assert
        assertNotNull(response);
        assertEquals(100.0, response.getTotalSpending());
        assertEquals(1, response.getSpendingByCategory().size());
        MonthlySpendingDTO groceriesSpending = response.getSpendingByCategory().get(0);
        assertEquals("Groceries", groceriesSpending.getCategory());
        assertEquals(100.0, groceriesSpending.getAmount());
        assertEquals(100.0, groceriesSpending.getPercentage());
        assertEquals(1, groceriesSpending.getTransactionCount());
    }

    @Test
    void generateMonthlyReport_ReturnsCorrectTotals() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

        // Act
        Map<String, Object> report = reportService.generateMonthlyReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.get("startDate"));
        assertEquals(endDate, report.get("endDate"));
        assertEquals(300.0, report.get("totalSpending"));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> monthlyTotals = (Map<String, Double>) report.get("monthlyTotals");
        assertEquals(300.0, monthlyTotals.get("2024-01"));
    }

    @Test
    void generateCategoryReport_ReturnsCorrectTotals() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

        // Act
        Map<String, Object> report = reportService.generateCategoryReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(startDate, report.get("startDate"));
        assertEquals(endDate, report.get("endDate"));
        assertEquals(300.0, report.get("totalSpending"));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> categoryTotals = (Map<String, Double>) report.get("categoryTotals");
        assertEquals(100.0, categoryTotals.get("Groceries"));
        assertEquals(200.0, categoryTotals.get("Entertainment"));
    }

    @Test
    void getMonthlySpending_WithEmptyTransactions_ReturnsZeroTotals() {
        // Arrange
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // Act
        MonthlyReportResponse response = reportService.getMonthlySpending(startDate, endDate, null);

        // Assert
        assertNotNull(response);
        assertEquals(0.0, response.getTotalSpending());
        assertTrue(response.getSpendingByCategory().isEmpty());
    }

    @Test
    void generateMonthlyReport_WithEmptyTransactions_ReturnsZeroTotals() {
        // Arrange
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // Act
        Map<String, Object> report = reportService.generateMonthlyReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(0.0, report.get("totalSpending"));
        assertTrue(((Map<?, ?>) report.get("monthlyTotals")).isEmpty());
    }

    @Test
    void generateCategoryReport_WithEmptyTransactions_ReturnsZeroTotals() {
        // Arrange
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(Arrays.asList());

        // Act
        Map<String, Object> report = reportService.generateCategoryReport(startDate, endDate);

        // Assert
        assertNotNull(report);
        assertEquals(0.0, report.get("totalSpending"));
        assertTrue(((Map<?, ?>) report.get("categoryTotals")).isEmpty());
    }
}
