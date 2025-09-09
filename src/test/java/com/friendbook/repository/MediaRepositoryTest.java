package com.friendbook.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.Media;
import com.friendbook.model.Post;
import com.friendbook.model.User;

@DataJpaTest
public class MediaRepositoryTest {

	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	private Post post;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("testuser@example.com");
		user.setFullName("Test user");
		user.setPassword("password");
		user.setUsername("testuser");
		userRepository.save(user);
		
		post = new Post();
		post.setUser(user);
		post.setCaption("Test post");
		postRepository.save(post);
		
		Media media1 = new Media();
		media1.setFilePath("/downloads/sample.jpg");
		media1.setPost(post);
		
		Media media2 = new Media();
		media2.setFilePath("documents/test.jpeg");
		media2.setPost(post);
		
		mediaRepository.save(media1);
		mediaRepository.save(media2);
	}
	
	@Test
	void testFindByPost() {
		List<Media> mediaList = mediaRepository.findByPost(post);
		assertThat(mediaList).isNotEmpty();
		assertThat(mediaList.size()).isEqualTo(2);
		assertThat(mediaList.get(0).getPost().getId()).isEqualTo(post.getId());
	}
}
