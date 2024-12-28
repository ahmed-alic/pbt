package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OpenAiService openAiService;
    private final BudgetGoalService budgetGoalService;

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
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type must be specified");
        }
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDate.parse(new java.util.Date().toString()));
        }
        
        // Save the transaction first
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Update budget goal spending if applicable
        if (savedTransaction.getBudgetgoal() != null) {
            budgetGoalService.updateBudgetGoalSpending(savedTransaction);
        }
        
        return savedTransaction;
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(id);
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setType(updatedTransaction.getType());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setDate(updatedTransaction.getDate());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        return transactionRepository.save(existingTransaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public OpenAiService getOpenAiService() {
        return openAiService;
    }
}
