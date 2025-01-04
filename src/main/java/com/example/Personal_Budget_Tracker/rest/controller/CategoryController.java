package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.service.CategoryService;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategorySuggester categorySuggester;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService, CategorySuggester categorySuggester) {
        this.categoryService = categoryService;
        this.categorySuggester = categorySuggester;
        logger.info("CategoryController initialized with services: " + categoryService + ", " + categorySuggester);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            logger.info("Getting all categories");
            List<Category> categories = categoryService.getAllCategories();
            logger.info("Found {} categories", categories.size());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error getting categories: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            logger.info("Creating new category: {}", category.getName());
            Category created = categoryService.createCategory(category);
            logger.info("Created category with id: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            logger.info("Updating category with id {}: {}", id, category);
            category.setId(id);
            Category updated = categoryService.updateCategory(category);
            logger.info("Successfully updated category: {}", updated);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating category {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            logger.info("Deleting category with id: {}", id);
            categoryService.deleteCategory(id);
            logger.info("Successfully deleted category with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting category {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/suggest")
    public ResponseEntity<String> suggestCategory(@RequestParam String description) {
        try {
            logger.info("Suggesting category for description: {}", description);
            String suggestion = categorySuggester.suggestCategory(description);
            logger.info("Category suggestion: {}", suggestion);
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            logger.error("Error suggesting category: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
