package com.friendbook.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.Post;
import com.friendbook.model.PostLike;
import com.friendbook.model.User;

@DataJpaTest
public class LikeRepositoryTest {

	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User user;
	private Post post;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("likeuser@example.com");
		user.setFullName("Like user");
		user.setPassword("password");
		user.setUsername("likeUser");
		userRepository.save(user);
		
		post = new Post();
		post.setUser(user);
		post.setCaption("Liked post");
		postRepository.save(post);
		
		PostLike like = new PostLike();
		like.setUser(user);
		like.setPost(post);
		likeRepository.save(like);
	}
	
	@Test
	public void testExistsByUserAndPost() {
		boolean exists = likeRepository.existsByUserAndPost(user, post);
		assertThat(exists).isTrue();
	}
	
	@Test
	public void testDeleteByUserAndPost() {
		likeRepository.deleteByUserAndPost(user, post);
		boolean exists = likeRepository.existsByUserAndPost(user, post);
		assertThat(exists).isFalse();
	}
	
	@Test
	void testCountByPost() {
		long count = likeRepository.countByPost(post);
		assertThat(count).isEqualTo(1);
	}
}
