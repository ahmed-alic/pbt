package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private OpenAiService openAiService;
    
    @Mock
    private BudgetGoalService budgetGoalService;
    
    @InjectMocks
    private TransactionService transactionService;
    
    @Test
    void whenGetAllTransactions_thenReturnList() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(
            createTransaction(1L, 100.0, "Groceries"),
            createTransaction(2L, 50.0, "Entertainment")
        );
        when(transactionRepository.findAll()).thenReturn(transactions);
        
        // Act
        List<Transaction> result = transactionService.getAllTransactions();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }
    
    @Test
    void whenGetTransactionById_withValidId_thenReturnTransaction() {
        // Arrange
        Transaction transaction = createTransaction(1L, 100.0, "Groceries");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        
        // Act
        Transaction result = transactionService.getTransactionById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getAmount());
        verify(transactionRepository).findById(1L);
    }
    
    @Test
    void whenGetTransactionById_withInvalidId_thenThrowException() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionById(1L);
        });
        verify(transactionRepository).findById(1L);
    }
    
    @Test
    void whenCreateTransaction_withValidData_thenSuccess() {
        // Arrange
        Transaction transaction = createTransaction(null, 100.0, "Groceries");
        Transaction savedTransaction = createTransaction(1L, 100.0, "Groceries");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        
        // Act
        Transaction result = transactionService.createTransaction(transaction);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getAmount());
        verify(transactionRepository).save(transaction);
    }
    
    @Test
    void whenCreateTransaction_withNoDate_thenSetCurrentDate() {
        // Arrange
        Transaction transaction = createTransaction(null, 100.0, "Groceries");
        transaction.setDate(null);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        
        // Act
        Transaction result = transactionService.createTransaction(transaction);
        
        // Assert
        assertNotNull(result.getDate());
        assertEquals(LocalDate.now(), result.getDate());
    }
    
    @Test
    void whenCreateTransaction_withInvalidAmount_thenThrowException() {
        // Arrange
        Transaction transaction = createTransaction(null, -100.0, "Groceries");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transaction);
        });
        verify(transactionRepository, never()).save(any());
    }
    
    @Test
    void whenFindTransactionsByDateRange_thenReturnList() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Transaction> transactions = Arrays.asList(
            createTransaction(1L, 100.0, "Groceries"),
            createTransaction(2L, 50.0, "Entertainment")
        );
        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);
        
        // Act
        List<Transaction> result = transactionRepository.findByDateBetween(startDate, endDate);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository).findByDateBetween(startDate, endDate);
    }
    
    private Transaction createTransaction(Long id, Double amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(LocalDate.now());
        transaction.setType("EXPENSE"); // Setting a default type
        return transaction;
    }
}
