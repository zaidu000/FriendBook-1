package com.friendbook.service.impl;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

class CustomUserDetailsServiceImplTest {

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
	@Mock
	private UserRepository userRepository;

	// Create an instance of class under test and inject the mocks annotated with
	// @Mock
	@InjectMocks
	private CustomUserDetailsServiceImpl userDetailsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// This is for UserExists
	@Test
	void testLoadUserByUsername_UserExists() {
		User user = new User();
		user.setEmail("test@example.com");

		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

		assertNotNull(result);
		assertEquals("test@example.com", result.getUsername());
	}

	// This is for UserNotFound
	@Test
	void testLoadUserByUsername_UserNotFound() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			userDetailsService.loadUserByUsername("missing@example.com");
		});
	}
}
