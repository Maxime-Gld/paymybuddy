package com.maxgld.paymybuddy.servicesTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.maxgld.paymybuddy.dto.TransactionDto;
import com.maxgld.paymybuddy.entity.TransactionEntity;
import com.maxgld.paymybuddy.entity.UserEntity;
import com.maxgld.paymybuddy.repository.TransactionRepository;
import com.maxgld.paymybuddy.services.TransactionService;
import com.maxgld.paymybuddy.services.UserService;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for transfer method
    @Test
    void transfer_ValidTransfer_ShouldRecordTransaction() throws Exception {
        // Given
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");
        UserEntity receiver = new UserEntity();
        receiver.setId(2);
        receiver.setEmail("receiver@example.com");

        when(userDetails.getUsername()).thenReturn("sender@example.com");
        when(userService.findByEmail("sender@example.com")).thenReturn(sender);
        when(userService.findById(2)).thenReturn(receiver);

        // When
        transactionService.transfer(userDetails, 2, 100.0, "Payment for services");

        // Then
        verify(userService, times(1)).transfer(userDetails, 2, 100.0);
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void transfer_UserServiceThrowsException_ShouldRethrowException() {
        // Given
        doThrow(new IllegalArgumentException("Solde insuffisant"))
                .when(userService)
                .transfer(userDetails, 2, 100.0);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> transactionService.transfer(userDetails, 2, 100.0, "Payment for services"));
        assertEquals("Solde insuffisant", exception.getMessage());
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    // Test for getAllTransactionSent method
    @Test
    void getAllTransactionSent_TransactionsExist_ShouldReturnListOfTransactionDtos() {
        // Given
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");

        UserEntity receiver = new UserEntity();
        receiver.setUsername("receiverUser");

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setSender(sender);
        transactionEntity.setReceiver(receiver);
        transactionEntity.setAmount(100.0);
        transactionEntity.setDescription("Payment");

        List<TransactionEntity> transactionEntities = new ArrayList<>();
        transactionEntities.add(transactionEntity);

        when(userDetails.getUsername()).thenReturn("sender@example.com");
        when(userService.findByEmail("sender@example.com")).thenReturn(sender);
        when(transactionRepository.findAllBySender(sender)).thenReturn(transactionEntities);

        // When
        List<TransactionDto> transactionDtos = transactionService.getAllTransactionSent(userDetails);

        // Then
        assertEquals(1, transactionDtos.size());
        assertEquals("receiverUser", transactionDtos.get(0).getReceiver());
        assertEquals(100.0, transactionDtos.get(0).getAmount());
        assertEquals("Payment", transactionDtos.get(0).getDescription());
    }

    @Test
    void getAllTransactionSent_NoTransactions_ShouldReturnEmptyList() {
        // Given
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");

        when(userDetails.getUsername()).thenReturn("sender@example.com");
        when(userService.findByEmail("sender@example.com")).thenReturn(sender);
        when(transactionRepository.findAllBySender(sender)).thenReturn(new ArrayList<>());

        // When
        List<TransactionDto> transactionDtos = transactionService.getAllTransactionSent(userDetails);

        // Then
        assertTrue(transactionDtos.isEmpty());
    }
}