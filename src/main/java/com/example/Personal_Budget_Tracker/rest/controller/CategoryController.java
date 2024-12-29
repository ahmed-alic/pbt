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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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
            logger.info("CategoryController: Getting all categories");
            List<Category> categories = categoryService.getAllCategories();
            logger.info("CategoryController: Found categories: {}", categories);
            if (categories == null) {
                logger.error("CategoryController: Categories list is null");
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("CategoryController: Error getting categories: {}", e.getMessage());
            logger.error("", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());
        Category created = categoryService.createCategory(category);
        logger.info("Created category with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping(path = "/suggest")
    public ResponseEntity<String> suggestCategory(@RequestParam(name = "description") String description) {
        try {
            logger.info("Getting category suggestion for description: {}", description);
            String suggestedCategory = categorySuggester.suggestCategory(description);
            logger.info("Suggested category: {}", suggestedCategory);
            return ResponseEntity.ok().body(suggestedCategory);
        } catch (Exception e) {
            logger.error("Error suggesting category: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to suggest category: " + e.getMessage());
        }
    }
}
