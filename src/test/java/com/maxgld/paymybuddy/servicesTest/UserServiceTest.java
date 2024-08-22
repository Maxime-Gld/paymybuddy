package com.maxgld.paymybuddy.servicesTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.maxgld.paymybuddy.dto.UserCreateDto;
import com.maxgld.paymybuddy.entity.UserEntity;
import com.maxgld.paymybuddy.repository.UsersRepository;
import com.maxgld.paymybuddy.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

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
}
