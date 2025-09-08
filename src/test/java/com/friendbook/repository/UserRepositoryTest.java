package com.friendbook.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.User;

@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	private User user1;
	private User user2;
	
	@BeforeEach
	void setUp() {
		//Clean DB Before test case
		userRepository.deleteAll();
		
		user1 = new User();
		user1.setUsername("paras");
		user1.setEmail("paras@gmail.com");
		user1.setFullName("Paras Jain");
		user1.setPassword("12345678");
		
		user2 = new User();
		user2.setUsername("param");
		user2.setEmail("example@gmail.com");
		user2.setFullName("Example Test");
		user2.setPassword("12345678");
		
		userRepository.save(user1);
		userRepository.save(user2);
	}
	
	// Test case passed
	@Test
	void testFindByEmail() {
		Optional<User> found = userRepository.findByEmail("paras@gmail.com");
		assertTrue(found.isPresent());
		assertEquals("paras",found.get().getUsernameField());
	}
	
	// Test case passed
	@Test
	void testExistsByEmail() {
		assertTrue(userRepository.existsByEmail("paras@gmail.com"));
		assertFalse(userRepository.existsByEmail("exampletest@gmail.com"));
	}
	
	// Test case passed
	@Test
	void testFindByUsername() {
		Optional<User> found = userRepository.findByUsername("paras");
		assertTrue(found.isPresent());
		assertEquals("paras",found.get().getUsernameField());
	}
	
	// Test case passed
	@Test
	void testSearchByUsername() {
		List<User> results = userRepository.searchByUsername("par");
		assertEquals(2, results.size());
		
		results = userRepository.searchByUsername("paras");
		assertEquals(1,results.size());
		assertEquals("paras",results.get(0).getUsernameField());
	}
	
	// Test case passed
	@Test
    void testFindByUsernameContainingIgnoreCase() {
        List<User> results = userRepository.findByUsernameContainingIgnoreCase("PARA");
        assertEquals(2, results.size());
    }
}
