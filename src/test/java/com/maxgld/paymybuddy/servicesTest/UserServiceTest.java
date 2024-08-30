package com.maxgld.paymybuddy.servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.maxgld.paymybuddy.dto.UserCreateDto;
import com.maxgld.paymybuddy.entity.UserEntity;
import com.maxgld.paymybuddy.repository.UsersRepository;
import com.maxgld.paymybuddy.services.UserService;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_UserAlreadyExists_ShouldReturnFalse() {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("existinguser@example.com");
        userCreateDto.setUsername("existinguser");
        userCreateDto.setPassword("password");

        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("existinguser@example.com");

        when(usersRepository.findByEmail(userCreateDto.getEmail())).thenReturn(Optional.of(existingUser));

        // When
        Boolean result = userService.saveUser(userCreateDto);

        // Then
        assertFalse(result);
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void saveUser_NewUser_ShouldReturnTrueAndSaveUser() {
        // Given
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("newuser@example.com");
        userCreateDto.setUsername("newuser");
        userCreateDto.setPassword("password");

        when(usersRepository.findByEmail(userCreateDto.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(userCreateDto.getPassword())).thenReturn("encodedpassword");

        // When
        Boolean result = userService.saveUser(userCreateDto);

        // Then
        assertTrue(result);
        verify(usersRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void findByEmail_UserExists_ShouldReturnUserEntity() {
        // Given
        String email = "existinguser@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // When
        UserEntity result = userService.findByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void findByEmail_UserDoesNotExist_ShouldReturnNull() {
        // Given
        String email = "nonexistentuser@example.com";

        when(usersRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        UserEntity result = userService.findByEmail(email);

        // Then
        assertNull(result);
    }

    // Tests for the addConnection method
    @Test
    void addConnection_ValidConnection_ShouldAddConnection() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        userEntity.setConnections(new HashSet<>());

        UserEntity connectionEntity = new UserEntity();
        connectionEntity.setEmail("friend@example.com");

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
        when(usersRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(connectionEntity));
        when(userDetails.getUsername()).thenReturn("user@example.com");

        // When
        userService.addConnection(userDetails, "friend@example.com");

        // Then
        assertTrue(userEntity.getConnections().contains(connectionEntity));
        verify(usersRepository, times(1)).save(userEntity);
    }

    @Test
    void addConnection_ConnectionAlreadyExists_ShouldThrowException() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        userEntity.setConnections(new HashSet<>());
        UserEntity connectionEntity = new UserEntity();
        connectionEntity.setEmail("friend@example.com");
        userEntity.getConnections().add(connectionEntity);

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
        when(usersRepository.findByEmail("friend@example.com")).thenReturn(Optional.of(connectionEntity));
        when(userDetails.getUsername()).thenReturn("user@example.com");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.addConnection(userDetails, "friend@example.com"));
        assertEquals("Cet utilisateur est déjà dans votre liste d'amis", exception.getMessage());
    }

    // Tests for the transfer method
    @Test
    void transfer_ValidTransfer_ShouldAdjustBalances() {
        // Given
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");
        sender.setBalance(200.0);

        UserEntity receiver = new UserEntity();
        receiver.setId(2);
        receiver.setBalance(50.0);

        when(usersRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(usersRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userDetails.getUsername()).thenReturn("sender@example.com");

        // When
        userService.transfer(userDetails, 2, 100.0);

        // Then
        assertEquals(100.0, sender.getBalance());
        assertEquals(150.0, receiver.getBalance());
        verify(usersRepository, times(1)).save(sender);
        verify(usersRepository, times(1)).save(receiver);
    }

    @Test
    void transfer_InsufficientBalance_ShouldThrowException() {
        // Given
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");
        sender.setBalance(50.0);

        UserEntity receiver = new UserEntity();
        receiver.setId(2);

        when(usersRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(usersRepository.findById(2)).thenReturn(Optional.of(receiver));
        when(userDetails.getUsername()).thenReturn("sender@example.com");

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.transfer(userDetails, 2, 100.0));
        assertEquals("Solde insuffisant", exception.getMessage());
    }

    // Tests for the getBalance method
    @Test
    void getBalance_ValidUser_ShouldReturnBalance() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        userEntity.setBalance(150.0);

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
        when(userDetails.getUsername()).thenReturn("user@example.com");

        // When
        Double balance = userService.getBalance(userDetails);

        // Then
        assertEquals(150.0, balance);
    }

    // Tests for the getUsername method
    @Test
    void getUsername_ValidUser_ShouldReturnUsername() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        userEntity.setUsername("username");

        when(usersRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
        when(userDetails.getUsername()).thenReturn("user@example.com");

        // When
        String username = userService.getUsername(userDetails);

        // Then
        assertEquals("username", username);
    }
}
