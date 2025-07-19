package com.example.userapi.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.exception.DuplicateUserException;
import com.example.userapi.exception.InvalidUserDataException;
import com.example.userapi.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User("testuser", "test@example.com", "Test", "User");
    }

    @Test
    @DisplayName("Should create user successfully with valid data")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // When
        User result = userService.createUser(validUser);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("Should throw exception for duplicate username")
    void shouldThrowExceptionForDuplicateUsername() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, 
            () -> userService.createUser(validUser));
        
        assertTrue(exception.getMessage().contains("Username 'testuser' is already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception for duplicate email")
    void shouldThrowExceptionForDuplicateEmail() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, 
            () -> userService.createUser(validUser));
        
        assertTrue(exception.getMessage().contains("Email 'test@example.com' is already registered"));
    }

    @Test
    @DisplayName("Should throw exception for invalid username length")
    void shouldThrowExceptionForInvalidUsernameLength() {
        // Given
        validUser.setUsername("ab"); // Too short

        // When & Then
        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class, 
            () -> userService.createUser(validUser));
        
        assertTrue(exception.getMessage().contains("Username must be between 3 and 20 characters"));
    }

    @Test
    @DisplayName("Should throw exception for invalid email format")
    void shouldThrowExceptionForInvalidEmailFormat() {
        // Given
        validUser.setEmail("invalid-email");

        // When & Then
        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class, 
            () -> userService.createUser(validUser));
        
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        validUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));

        // When
        User result = userService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(validUser.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    @DisplayName("Should return true for available username")
    void shouldReturnTrueForAvailableUsername() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        // When
        boolean result = userService.isUsernameAvailable("newuser");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false for taken username")
    void shouldReturnFalseForTakenUsername() {
        // Given
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When
        boolean result = userService.isUsernameAvailable("existinguser");

        // Then
        assertFalse(result);
    }
}
