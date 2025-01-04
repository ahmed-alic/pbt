package com.example.Personal_Budget_Tracker.api;

import com.example.Personal_Budget_Tracker.api.config.TestConfig;
import com.example.Personal_Budget_Tracker.core.model.Transaction;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ActiveProfiles("test")
class TransactionApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private Transaction createTestTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setType("EXPENSE");
        transaction.setDescription("Test Transaction");
        transaction.setDate(LocalDate.now());
        return transaction;
    }

    @Test
    void getAllTransactions_ReturnsSuccessfully() {
        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
            createURLWithPort("/api/transaction/"), 
            List.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createTransaction_WithValidData_ReturnsCreatedTransaction() {
        // Arrange
        Transaction transaction = createTestTransaction();

        // Act
        ResponseEntity<Transaction> response = restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals("EXPENSE", response.getBody().getType());
    }

    @Test
    void createTransaction_WithInvalidAmount_ReturnsBadRequest() {
        // Arrange
        Transaction transaction = createTestTransaction();
        transaction.setAmount(-100.0); // Invalid negative amount

        // Act
        ResponseEntity<Transaction> response = restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTransactionById_ExistingTransaction_ReturnsTransaction() {
        // Arrange
        // First create a transaction
        Transaction transaction = createTestTransaction();
        ResponseEntity<Transaction> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );
        Transaction createdTransaction = createResponse.getBody();

        // Act
        ResponseEntity<Transaction> response = restTemplate.getForEntity(
            createURLWithPort("/api/transaction/" + createdTransaction.getId()),
            Transaction.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdTransaction.getId(), response.getBody().getId());
    }

    @Test
    void updateTransaction_WithValidData_ReturnsUpdatedTransaction() {
        // Arrange
        // First create a transaction
        Transaction transaction = createTestTransaction();
        ResponseEntity<Transaction> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );
        Transaction createdTransaction = createResponse.getBody();

        // Update the transaction
        createdTransaction.setDescription("Updated Transaction");

        // Act
        ResponseEntity<Transaction> response = restTemplate.exchange(
            createURLWithPort("/api/transaction/update/" + createdTransaction.getId()),
            HttpMethod.PUT,
            new HttpEntity<>(createdTransaction),
            Transaction.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Transaction", response.getBody().getDescription());
    }

    @Test
    void deleteTransaction_ExistingTransaction_ReturnsNoContent() {
        // Arrange
        // First create a transaction
        Transaction transaction = createTestTransaction();
        ResponseEntity<Transaction> createResponse = restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );
        Transaction createdTransaction = createResponse.getBody();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
            createURLWithPort("/api/transaction/" + createdTransaction.getId()),
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getTransactionsByDateRange_ReturnsTransactions() {
        // Arrange
        Transaction transaction = createTestTransaction();
        restTemplate.postForEntity(
            createURLWithPort("/api/transaction/create"),
            transaction,
            Transaction.class
        );

        String startDate = LocalDate.now().minusDays(1).toString();
        String endDate = LocalDate.now().plusDays(1).toString();

        // Act
        ResponseEntity<List> response = restTemplate.getForEntity(
            createURLWithPort("/api/transaction/date-range?startDate=" + startDate + "&endDate=" + endDate),
            List.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }
}
