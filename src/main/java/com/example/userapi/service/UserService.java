package com.example.userapi.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.exception.DuplicateUserException;
import com.example.userapi.exception.InvalidUserDataException;
import com.example.userapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MAX_NAME_LENGTH = 50;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        logger.debug("Creating user: {}", user);
        
        validateUserData(user);
        checkForDuplicates(user);
        
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        return !userRepository.existsByUsername(username.trim());
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return !userRepository.existsByEmail(email.trim().toLowerCase());
    }

    private void validateUserData(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User data cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validateName(user.getFirstName(), "First name");
        validateName(user.getLastName(), "Last name");

        // Normalize data
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
    }

    private void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new InvalidUserDataException("Username is required");
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH || trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            throw new InvalidUserDataException(
                String.format("Username must be between %d and %d characters", 
                    MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH));
        }
        
        if (!trimmedUsername.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUserDataException("Username can only contain letters, numbers, and underscores");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new InvalidUserDataException("Email is required");
        }
        
        String trimmedEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new InvalidUserDataException("Invalid email format");
        }
    }

    private void validateName(String name, String fieldName) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidUserDataException(fieldName + " is required");
        }
        
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new InvalidUserDataException(fieldName + " cannot exceed " + MAX_NAME_LENGTH + " characters");
        }
    }

    private void checkForDuplicates(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Username '" + user.getUsername() + "' is already taken");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Email '" + user.getEmail() + "' is already registered");
        }
    }
}
