package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OpenAiService openAiService;

    // Constructor for dependency injection
    public TransactionService(TransactionRepository transactionRepository, OpenAiService openAiService) {
        this.transactionRepository = transactionRepository;
        this.openAiService = openAiService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
    }

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
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

    public Transaction createTransaction(Transaction transaction) {
        return null;
    }
}
