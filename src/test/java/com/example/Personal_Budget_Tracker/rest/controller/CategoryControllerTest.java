package com.example.Personal_Budget_Tracker.rest.controller;

import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.service.CategoryService;
import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.rest.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategorySuggester categorySuggester;

    @InjectMocks
    private CategoryController categoryController;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Groceries");
    }

    @Test
    void getAllCategories_ReturnsListOfCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
        verify(categoryService).getAllCategories();
    }

    @Test
    void getAllCategories_WhenError_ReturnsInternalServerError() {
        // Arrange
        when(categoryService.getAllCategories()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService).getAllCategories();
    }

    @Test
    void createCategory_WithValidData_ReturnsCreatedCategory() {
        // Arrange
        when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);

        // Act
        ResponseEntity<?> response = categoryController.createCategory(testCategory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCategory, response.getBody());
        verify(categoryService).createCategory(testCategory);
    }

    @Test
    void createCategory_WithEmptyName_ReturnsBadRequest() {
        // Arrange
        Category invalidCategory = new Category();
        invalidCategory.setName("");

        // Act
        ResponseEntity<?> response = categoryController.createCategory(invalidCategory);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        assertEquals("Category name cannot be empty", ((ErrorResponse) response.getBody()).getMessage());
    }

    @Test
    void createCategory_WhenError_ReturnsInternalServerError() {
        // Arrange
        when(categoryService.createCategory(any(Category.class)))
            .thenThrow(new RuntimeException("Error creating category"));

        // Act
        ResponseEntity<?> response = categoryController.createCategory(testCategory);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService).createCategory(testCategory);
    }

    @Test
    void updateCategory_WithValidData_ReturnsUpdatedCategory() {
        // Arrange
        Long categoryId = 1L;
        when(categoryService.updateCategory(any(Category.class))).thenReturn(testCategory);

        // Act
        ResponseEntity<?> response = categoryController.updateCategory(categoryId, testCategory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCategory, response.getBody());
        verify(categoryService).updateCategory(testCategory);
    }

    @Test
    void updateCategory_WithEmptyName_ReturnsBadRequest() {
        // Arrange
        Long categoryId = 1L;
        Category invalidCategory = new Category();
        invalidCategory.setName("");

        // Act
        ResponseEntity<?> response = categoryController.updateCategory(categoryId, invalidCategory);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        assertEquals("Category name cannot be empty", ((ErrorResponse) response.getBody()).getMessage());
    }

    @Test
    void updateCategory_WhenError_ReturnsInternalServerError() {
        // Arrange
        Long categoryId = 1L;
        when(categoryService.updateCategory(any(Category.class)))
            .thenThrow(new RuntimeException("Error updating category"));

        // Act
        ResponseEntity<?> response = categoryController.updateCategory(categoryId, testCategory);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService).updateCategory(testCategory);
    }

    @Test
    void deleteCategory_WithValidId_ReturnsNoContent() {
        // Arrange
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(categoryId);

        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_WhenError_ReturnsInternalServerError() {
        // Arrange
        Long categoryId = 1L;
        doThrow(new RuntimeException("Error deleting category"))
            .when(categoryService).deleteCategory(categoryId);

        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void suggestCategory_WithValidDescription_ReturnsSuggestion() {
        // Arrange
        String description = "grocery shopping at walmart";
        String expectedSuggestion = "Groceries";
        when(categorySuggester.suggestCategory(description)).thenReturn(expectedSuggestion);

        // Act
        ResponseEntity<String> response = categoryController.suggestCategory(description);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSuggestion, response.getBody());
        verify(categorySuggester).suggestCategory(description);
    }

    @Test
    void suggestCategory_WhenError_ReturnsInternalServerError() {
        // Arrange
        String description = "grocery shopping at walmart";
        when(categorySuggester.suggestCategory(description))
            .thenThrow(new RuntimeException("Error suggesting category"));

        // Act
        ResponseEntity<String> response = categoryController.suggestCategory(description);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(categorySuggester).suggestCategory(description);
    }
}
