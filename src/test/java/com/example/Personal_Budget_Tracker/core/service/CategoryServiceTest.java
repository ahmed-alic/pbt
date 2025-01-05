package com.example.Personal_Budget_Tracker.core.service;

import com.example.Personal_Budget_Tracker.core.api.categorysuggester.CategorySuggester;
import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.core.repository.CategoryRepository;
import com.example.Personal_Budget_Tracker.core.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private CategorySuggester categorySuggester;
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @InjectMocks
    private CategoryService categoryService;
    
    @Test
    void whenGetAllCategories_thenReturnList() {
        // Arrange
        List<Category> categories = Arrays.asList(
            createCategory(1L, "Groceries"),
            createCategory(2L, "Entertainment")
        );
        when(categoryRepository.findAll()).thenReturn(categories);
        
        // Act
        List<Category> result = categoryService.getAllCategories();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }
    
    @Test
    void whenCreateCategory_withValidName_thenSuccess() {
        // Arrange
        Category category = createCategory(null, "Groceries");
        Category savedCategory = createCategory(1L, "Groceries");
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        
        // Act
        Category result = categoryService.createCategory(category);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Groceries", result.getName());
        verify(categoryRepository).save(category);
    }
    
    @Test
    void whenCreateCategory_withEmptyName_thenThrowException() {
        // Arrange
        Category category = new Category();
        category.setName("");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(category);
        });
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    void whenUpdateCategory_withValidData_thenSuccess() {
        // Arrange
        Category category = createCategory(1L, "Updated Groceries");
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        // Act
        Category result = categoryService.updateCategory(category);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Groceries", result.getName());
        verify(categoryRepository).save(category);
    }
    
    @Test
    void whenUpdateCategory_withNonExistentId_thenThrowException() {
        // Arrange
        Category category = createCategory(1L, "Updated Groceries");
        when(categoryRepository.existsById(1L)).thenReturn(false);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(category);
        });
        verify(categoryRepository, never()).save(any());
    }
    
    @Test
    void whenDeleteCategory_thenSuccess() {
        // Arrange
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);
        
        // Act
        categoryService.deleteCategory(categoryId);
        
        // Assert
        verify(categoryRepository).deleteById(categoryId);
    }
    
    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}
