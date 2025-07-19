package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import com.example.userapi.exception.DuplicateUserException;
import com.example.userapi.exception.InvalidUserDataException;
import com.example.userapi.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "Test", "User");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return bad request for duplicate user")
    void shouldReturnBadRequestForDuplicateUser() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new DuplicateUserException("Username 'testuser' is already taken"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username 'testuser' is already taken"));
    }

    @Test
    @DisplayName("Should return bad request for invalid user data")
    void shouldReturnBadRequestForInvalidUserData() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new InvalidUserDataException("Username must be between 3 and 20 characters"));

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username must be between 3 and 20 characters"));
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Should return not found for non-existent user")
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        // Given
        when(userService.findById(999L)).thenThrow(new UserNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() throws Exception {
        // Given
        User user2 = new User("user2", "user2@example.com", "User", "Two");
        user2.setId(2L);
        List<User> users = Arrays.asList(testUser, user2);
        when(userService.findAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpected(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("Should check username availability")
    void shouldCheckUsernameAvailability() throws Exception {
        // Given
        when(userService.isUsernameAvailable("newuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/check-username/newuser"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Should check email availability")
    void shouldCheckEmailAvailability() throws Exception {
        // Given
        when(userService.isEmailAvailable("new@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.available").value(true));
    }
}
