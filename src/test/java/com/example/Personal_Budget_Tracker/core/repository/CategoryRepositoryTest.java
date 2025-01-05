package com.example.Personal_Budget_Tracker.core.repository;

import com.example.Personal_Budget_Tracker.core.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void whenSaveCategory_thenReturnSavedCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Groceries");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category savedCategory = categoryRepository.save(category);

        // Assert
        assertNotNull(savedCategory);
        assertEquals("Groceries", savedCategory.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void whenFindById_thenReturnCategory() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Groceries");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> found = categoryRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Groceries", found.get().getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void whenFindAll_thenReturnCategoryList() {
        // Arrange
        Category category1 = new Category();
        category1.setName("Groceries");
        Category category2 = new Category();
        category2.setName("Entertainment");
        List<Category> categories = Arrays.asList(category1, category2);
        
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> foundCategories = categoryRepository.findAll();

        // Assert
        assertNotNull(foundCategories);
        assertEquals(2, foundCategories.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void whenDeleteCategory_thenVerifyDeletion() {
        // Arrange
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act
        categoryRepository.deleteById(categoryId);

        // Assert
        verify(categoryRepository).deleteById(categoryId);
    }
}
