package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
import com.example.Personal_Budget_Tracker.core.repository.CategoryRepository;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategorySuggester categorySuggester;
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository, CategorySuggester categorySuggester, TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.categorySuggester = categorySuggester;
        this.transactionRepository = transactionRepository;
    }

    public List<Category> getAllCategories() {
        try {
            logger.info("Getting all categories");
            List<Category> categories = categoryRepository.findAll();
            logger.info("Found {} categories", categories.size());
            logger.debug("Categories: {}", categories);
            return categories;
        } catch (Exception e) {
            logger.error("Error getting categories: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Category createCategory(Category category) {
        try {
            logger.info("Creating new category: {}", category);
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }
            Category created = categoryRepository.save(category);
            logger.info("Created category with id: {}", created.getId());
            return created;
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Category updateCategory(Category category) {
        try {
            logger.info("Updating category: {}", category);
            if (category.getId() == null) {
                logger.error("Cannot update category without ID");
                throw new IllegalArgumentException("Category ID cannot be null");
            }
            
            // Check if category exists
            if (!categoryRepository.existsById(category.getId())) {
                logger.error("Category not found with ID: {}", category.getId());
                throw new RuntimeException("Category not found with ID: " + category.getId());
            }
            
            Category updated = categoryRepository.save(category);
            logger.info("Successfully updated category: {}", updated);
            return updated;
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteCategory(Long id) {
        try {
            logger.info("Deleting category with id: {}", id);
            categoryRepository.deleteById(id);
            logger.info("Successfully deleted category with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting category with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public String suggestCategory(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if(transaction.isPresent()) {
            return categorySuggester.suggestCategory(transaction.get().getDescription());
        }
        return "Task does not exist";
    }

    public Category getCategoryById(Long id) {
        return null;
    }
}
