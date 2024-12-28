package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.api.impl.OpenAICategorySuggester;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.CategoryRepository;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategorySuggester openAICategorySuggester;
    private final TransactionRepository transactionRepository;

    // Constructor for dependency injection
    public CategoryService(CategoryRepository categoryRepository, OpenAICategorySuggester openAICategorySuggester, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.openAICategorySuggester = openAICategorySuggester;
        this.transactionRepository = transactionRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public String suggestCategory(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if(transaction.isPresent()) {
            return openAICategorySuggester.suggestCategory(transaction.get().getDescription());
        }
        return "Task does not exist";
    }

    public Category getCategoryById(Long id) {
        return null;
    }

    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return categoryRepository.save(category);
    }
}
