package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OpenAiService openAiService;
    private final BudgetGoalService budgetGoalService;
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    // Constructor for dependency injection
    public TransactionService(
        TransactionRepository transactionRepository, 
        OpenAiService openAiService,
        BudgetGoalService budgetGoalService
    ) {
        this.transactionRepository = transactionRepository;
        this.openAiService = openAiService;
        this.budgetGoalService = budgetGoalService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        validateTransaction(transaction);
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.now());
        }
        
        // Save the transaction first
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update budget goal spending if applicable
        if (savedTransaction.getBudgetgoal() != null) {
            budgetGoalService.updateBudgetGoalSpending(savedTransaction);
        }
        
        return savedTransaction;
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        logger.info("Updating transaction with ID: {} with data: {}", id, updatedTransaction);
        
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));

        // Validate the updated transaction
        validateTransaction(updatedTransaction);

        // Update the existing transaction with new values
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setDate(updatedTransaction.getDate() != null ? 
                                  updatedTransaction.getDate() : existingTransaction.getDate());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setBudgetgoal(updatedTransaction.getBudgetgoal());

        logger.info("Saving updated transaction: {}", existingTransaction);
        return transactionRepository.save(existingTransaction);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type must be specified");
        }
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be empty");
        }
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public OpenAiService getOpenAiService() {
        return openAiService;
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        return transactionRepository.findByDateBetween(startDate, endDate);
    }
}
