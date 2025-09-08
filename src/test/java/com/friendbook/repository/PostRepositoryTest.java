package com.friendbook.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.Post;
import com.friendbook.model.User;

@DataJpaTest
public class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	private User user1;
	private User user2;
	private Post post1;
	private Post post2;
	private Post post3;

	LocalDateTime now = LocalDateTime.of(2025, 9, 9, 12, 0, 0);

	@BeforeEach
	void setUp() {
		// Clean DB Before each test case
		postRepository.deleteAll();
		userRepository.deleteAll();

		// Create users
		user1 = new User();
		user1.setEmail("user1@example.com");
		user1.setUsername("user1");
		user1.setFullName("User One");
		user1.setPassword("password1");
		userRepository.save(user1);

		user2 = new User();
		user2.setEmail("user2@example.com");
		user2.setUsername("user2");
		user2.setFullName("User Two");
		user2.setPassword("password2");
		userRepository.save(user2);

		// Create posts
		post1 = new Post();
		post1.setUser(user1);
		post1.setCaption("Post 1 content");
		post1.setCreatedAt(now.minusDays(2));
		postRepository.save(post1);

		post2 = new Post();
		post2.setUser(user1);
		post2.setCaption("Post 2 content");
		post2.setCreatedAt(now.minusDays(1));
		postRepository.save(post2);

		post3 = new Post();
		post3.setUser(user2);
		post3.setCaption("Post 3 content");
		post3.setCreatedAt(now.minusDays(0));
		postRepository.save(post3);
	}

	@Test
	void testFindByUserOrderByCreatedAtDesc() {
		List<Post> posts = postRepository.findByUserOrderByCreatedAtDesc(user1);
		assertEquals(2, posts.size());
		assertEquals("Post 2 content", posts.get(0).getCaption()); // newest first
		assertEquals("Post 1 content", posts.get(1).getCaption());
	}

	@Test
	void testFindByUserInOrderByCreatedAtDesc() {
		List<Post> posts = postRepository.findByUserInOrderByCreatedAtDesc(Set.of(user1, user2));
		assertEquals(3, posts.size());
		assertEquals("Post 3 content", posts.get(0).getCaption());
		assertEquals("Post 2 content", posts.get(1).getCaption());
		assertEquals("Post 1 content", posts.get(2).getCaption());
	}

	@Test
	void testFindAllByOrderByCreatedAtDesc() {
		List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
		assertEquals(3, posts.size());
		assertEquals("Post 3 content", posts.get(0).getCaption());
		assertEquals("Post 2 content", posts.get(1).getCaption());
		assertEquals("Post 1 content", posts.get(2).getCaption());
	}

}
