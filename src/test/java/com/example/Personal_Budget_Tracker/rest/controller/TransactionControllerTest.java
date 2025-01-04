package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(100.0);
        testTransaction.setDescription("Grocery shopping");
        testTransaction.setDate(LocalDate.now());
    }

    @Test
    void getAllTransactions_ReturnsListOfTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act
        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
        verify(transactionService).getAllTransactions();
    }

    @Test
    void getTransactionById_WithValidId_ReturnsTransaction() {
        // Arrange
        Long transactionId = 1L;
        when(transactionService.getTransactionById(transactionId)).thenReturn(testTransaction);

        // Act
        ResponseEntity<Transaction> response = transactionController.getTransactionById(transactionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void getTransactionById_WhenNotFound_ReturnsNotFound() {
        // Arrange
        Long transactionId = 1L;
        when(transactionService.getTransactionById(transactionId))
            .thenThrow(new RuntimeException("Transaction not found"));

        // Act
        ResponseEntity<Transaction> response = transactionController.getTransactionById(transactionId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void createTransaction_WithValidData_ReturnsCreatedTransaction() {
        // Arrange
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        ResponseEntity<Transaction> response = transactionController.createTransaction(testTransaction);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
        verify(transactionService).createTransaction(testTransaction);
    }

    @Test
    void createTransaction_WhenError_ReturnsBadRequest() {
        // Arrange
        when(transactionService.createTransaction(any(Transaction.class)))
            .thenThrow(new IllegalArgumentException("Invalid transaction data"));

        // Act
        ResponseEntity<Transaction> response = transactionController.createTransaction(testTransaction);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(transactionService).createTransaction(testTransaction);
    }

    @Test
    void updateTransaction_WithValidData_ReturnsUpdatedTransaction() {
        // Arrange
        Long transactionId = 1L;
        when(transactionService.updateTransaction(eq(transactionId), any(Transaction.class)))
            .thenReturn(testTransaction);

        // Act
        ResponseEntity<Transaction> response = transactionController.updateTransaction(transactionId, testTransaction);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testTransaction, response.getBody());
        verify(transactionService).updateTransaction(transactionId, testTransaction);
    }

    @Test
    void updateTransaction_WhenNotFound_ReturnsNotFound() {
        // Arrange
        Long transactionId = 1L;
        when(transactionService.updateTransaction(eq(transactionId), any(Transaction.class)))
            .thenThrow(new RuntimeException("Transaction not found"));

        // Act
        ResponseEntity<Transaction> response = transactionController.updateTransaction(transactionId, testTransaction);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService).updateTransaction(transactionId, testTransaction);
    }

    @Test
    void deleteTransaction_WithValidId_ReturnsNoContent() {
        // Arrange
        Long transactionId = 1L;
        doNothing().when(transactionService).deleteTransaction(transactionId);

        // Act
        ResponseEntity<Void> response = transactionController.deleteTransaction(transactionId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void deleteTransaction_WhenNotFound_ReturnsNotFound() {
        // Arrange
        Long transactionId = 1L;
        doThrow(new RuntimeException("Transaction not found"))
            .when(transactionService).deleteTransaction(transactionId);

        // Act
        ResponseEntity<Void> response = transactionController.deleteTransaction(transactionId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void deleteTransaction_WhenError_ReturnsInternalServerError() {
        // Arrange
        Long transactionId = 1L;
        doThrow(new RuntimeException("Error deleting transaction"))
            .when(transactionService).deleteTransaction(transactionId);

        // Act
        ResponseEntity<Void> response = transactionController.deleteTransaction(transactionId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(transactionService).deleteTransaction(transactionId);
    }
}
