package com.friendbook.service.impl;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// Test the registration where user is already exists
	@Test
	void testRegisterUser_UserAlreadyExists() {
		User user = new User();
		user.setEmail("test@example.com");

		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

		boolean result = userService.registerUser(user);

		assertFalse(result);
		verify(userRepository, never()).save(any(User.class));
	}

	// Test the registration where user is new
	@Test
	void testRegisterUser_NewUser() {
		User user = new User();
		user.setEmail("newuser@example.com");
		user.setFullName("John Doe");
		user.setPassword("password123");

		when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

		boolean result = userService.registerUser(user);

		assertTrue(result);
		assertNotNull(user.getUsernameField());
		assertNotEquals("password123", user.getPassword());
		verify(userRepository, times(1)).save(user);
	}

	// Get user by username where user is found
	@Test
	void testGetUserByUsername_UserFound() {
		User user = new User();
		user.setEmail("user@example.com");

		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

		User result = userService.getUserByUsername("user@example.com");

		assertNotNull(result);
		assertEquals("user@example.com", result.getEmail());
	}

	// Get user by username where user is not found
	@Test
	void testGetUserByUsername_UserNotFound() {
		when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUserByUsername("nonexistent@example.com");
		});
	}

	// Test search user by keyword
	@Test
	void testSearchUsersByKeyword() {
		List<User> users = new ArrayList<>();
		users.add(new User());
		when(userRepository.searchByUsername("john")).thenReturn(users);

		List<User> result = userService.searchUsersByKeyword("john");

		assertEquals(1, result.size());
	}

	// save the user
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
