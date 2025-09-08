package com.friendbook.service.impl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_UserAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.registerUser(user);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_NewUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setFullName("John Doe");
        user.setPassword("password123");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

        boolean result = userService.registerUser(user);

        assertTrue(result);
        assertNotNull(user.getUsername());
        assertNotEquals("password123", user.getPassword()); 
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGenerateUsername() {
        String username = userService.generateUsername("John Doe");

        assertTrue(username.matches("JohnD\\d{3}"));
    }

    @Test
    void testGetUserByUsername_UserFound() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("user@example.com");

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testSearchUsersByKeyword() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        when(userRepository.searchByUsername("john")).thenReturn(users);

        List<User> result = userService.searchUsersByKeyword("john");

        assertEquals(1, result.size());
    }


    @Test
    void testSave() {
        User user = new User();
        user.setEmail("save@example.com");

        userService.save(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSearchUsersByUsername() {
        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userRepository.findByUsernameContainingIgnoreCase("doe")).thenReturn(users);

        List<User> result = userService.searchUsersByUsername("doe");

        assertEquals(1, result.size());
    }
}

