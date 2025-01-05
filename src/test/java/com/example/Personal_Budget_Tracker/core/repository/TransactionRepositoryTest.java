package com.example.Personal_Budget_Tracker.core.repository;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.model.BudgetGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TransactionRepositoryTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void whenSaveTransaction_thenReturnSavedTransaction() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setDescription("Grocery shopping");
        transaction.setDate(LocalDate.now());
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Assert
        assertNotNull(savedTransaction);
        assertEquals(100.0, savedTransaction.getAmount());
        assertEquals("Grocery shopping", savedTransaction.getDescription());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void whenFindByDateBetween_thenReturnTransactionList() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDate.now().minusDays(5));
        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDate.now().minusDays(3));
        
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findByDateBetween(startDate, endDate))
            .thenReturn(transactions);

        // Act
        List<Transaction> foundTransactions = transactionRepository.findByDateBetween(startDate, endDate);

        // Assert
        assertNotNull(foundTransactions);
        assertEquals(2, foundTransactions.size());
        verify(transactionRepository).findByDateBetween(startDate, endDate);
    }

    @Test
    void whenFindByBudgetGoal_thenReturnTransactionList() {
        // Arrange
        BudgetGoal goal = new BudgetGoal();
        goal.setId(1L);
        goal.setAmount(1000.0);
        
        Transaction transaction1 = new Transaction();
        transaction1.setBudgetgoal(goal);
        Transaction transaction2 = new Transaction();
        transaction2.setBudgetgoal(goal);
        
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findByBudgetgoal(goal))
            .thenReturn(transactions);

        // Act
        List<Transaction> foundTransactions = transactionRepository.findByBudgetgoal(goal);

        // Assert
        assertNotNull(foundTransactions);
        assertEquals(2, foundTransactions.size());
        verify(transactionRepository).findByBudgetgoal(goal);
    }

    @Test
    void whenFindById_thenReturnTransaction() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(150.0);
        transaction.setDescription("Entertainment");
        
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        Optional<Transaction> found = transactionRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(150.0, found.get().getAmount());
        assertEquals("Entertainment", found.get().getDescription());
        verify(transactionRepository).findById(1L);
    }
}
