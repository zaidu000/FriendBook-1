package com.friendbook.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;
import com.friendbook.model.User;

@DataJpaTest
public class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	private User user;
	private Post post;
	private Comment comment1;
	private Comment comment2;
	
	@BeforeEach
	void setUp() {
		user = new User();
		user.setEmail("commentuser@example.com");
		user.setFullName("Comment user");
		user.setPassword("password");
		user.setUsername("commentuser");
		userRepository.save(user);
		
		post = new Post();
		post.setUser(user);
		post.setCaption("Commented Post");
		postRepository.save(post);
		
		comment1 = new Comment();
		comment1.setPost(post);
		comment1.setUser(user);
		comment1.setText("This is first comment");
		commentRepository.save(comment1);
		
		comment2 = new Comment();
		comment2.setPost(post);
		comment2.setUser(user);
		comment2.setText("This is second comment");
		commentRepository.save(comment2);
	}
	
	@Test
	void testFindByPostOrderByCreatedAtAsc() {
		List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
		assertThat(comments).hasSize(2);
		assertThat(comments.get(0).getText()).isEqualTo("This is first comment");
		assertThat(comments.get(1).getText()).isEqualTo("This is second comment");
	}
}
