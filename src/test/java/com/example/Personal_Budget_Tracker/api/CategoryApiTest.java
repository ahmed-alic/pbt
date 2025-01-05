package com.example.Personal_Budget_Tracker.api;

import com.example.Personal_Budget_Tracker.api.config.TestConfig;
import com.example.Personal_Budget_Tracker.core.model.Category;
import com.example.Personal_Budget_Tracker.rest.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
class CategoryApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    void getAllCategories_ReturnsSuccessfully() {
        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
            createURLWithPort("/api/category"),
            List.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createCategory_WithValidData_ReturnsCreatedCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Test Category");

        // Act
        ResponseEntity<Category> response = restTemplate.postForEntity(
            createURLWithPort("/api/category"),
            category,
            Category.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Test Category", response.getBody().getName());
    }

    @Test
    void createCategory_WithInvalidData_ReturnsBadRequest() {
        // Arrange
        Category category = new Category();
        category.setName("");  // Invalid empty name

        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            createURLWithPort("/api/category"),
            category,
            ErrorResponse.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void updateCategory_WithValidData_ReturnsUpdatedCategory() {
        // Arrange
        // First create a category
        Category category = new Category();
        category.setName("Initial Category");
        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/category"),
            category,
            Category.class
        );
        Category createdCategory = createResponse.getBody();

        // Update the category
        createdCategory.setName("Updated Category");

        // Act
        ResponseEntity<Category> response = restTemplate.exchange(
            createURLWithPort("/api/category/" + createdCategory.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(createdCategory),
            Category.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Category", response.getBody().getName());
    }

    @Test
    void updateCategory_WithInvalidData_ReturnsBadRequest() {
        // Arrange
        // First create a category
        Category category = new Category();
        category.setName("Initial Category");
        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/category"),
            category,
            Category.class
        );
        Category createdCategory = createResponse.getBody();

        // Update with invalid data
        createdCategory.setName("");

        // Act
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
            createURLWithPort("/api/category/" + createdCategory.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(createdCategory),
            ErrorResponse.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void deleteCategory_ExistingCategory_ReturnsNoContent() {
        // Arrange
        // First create a category
        Category category = new Category();
        category.setName("Category to Delete");
        ResponseEntity<Category> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/category"),
            category,
            Category.class
        );
        Category createdCategory = createResponse.getBody();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/api/category/" + createdCategory.getId()),
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void suggestCategory_WithValidDescription_ReturnsCategory() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            createURLWithPort("/api/category/suggest?description=grocery shopping at walmart"),
            String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
