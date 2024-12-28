package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {
    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Getting all categories");
        List<Category> categories = categoryService.getAllCategories();
        logger.info("Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());
        Category created = categoryService.createCategory(category);
        logger.info("Created category with id: {}", created.getId());
        return ResponseEntity.ok(created);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/suggest/{description}")
    public ResponseEntity<String> getCategorySuggestionForTransaction(@PathVariable String description) {
        logger.info("Getting category suggestion for description: {}", description);
        String suggestion = categoryService.suggestCategory(Long.valueOf(description));
        logger.info("Suggested category: {}", suggestion);
        return ResponseEntity.ok(suggestion);
    }
}
