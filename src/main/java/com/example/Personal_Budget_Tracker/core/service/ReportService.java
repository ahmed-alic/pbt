package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlyReportResponse;
import com.example.Personal_Budget_Tracker.rest.dto.MonthlySpendingDTO;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(ReportService.class);

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlyReportResponse getMonthlySpending(LocalDate startDate, LocalDate endDate, List<String> categories) {
        logger.info("Generating monthly spending report from {} to {}", startDate, endDate);
        
        // Get all transactions for the period
        List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
        
        // Filter by categories if specified
        if (categories != null && !categories.isEmpty()) {
            transactions = transactions.stream()
                .filter(t -> t.getCategory() != null && categories.contains(t.getCategory().getName()))
                .collect(Collectors.toList());
        }

        // Calculate total spending
        double totalSpending = transactions.stream()
            .filter(t -> "Expense".equals(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();

        // Group by category and calculate statistics
        Map<String, List<Transaction>> byCategory = transactions.stream()
            .filter(t -> "Expense".equals(t.getType()) && t.getCategory() != null)
            .collect(Collectors.groupingBy(t -> t.getCategory().getName()));

        List<MonthlySpendingDTO> spendingByCategory = byCategory.entrySet().stream()
            .map(entry -> {
                double categoryTotal = entry.getValue().stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
                double percentage = (totalSpending > 0) ? (categoryTotal / totalSpending) * 100 : 0;
                
                return new MonthlySpendingDTO(
                    entry.getKey(),
                    categoryTotal,
                    percentage,
                    entry.getValue().size()
                );
            })
            .collect(Collectors.toList());

        return new MonthlyReportResponse(startDate, endDate, totalSpending, spendingByCategory);
    }
}
