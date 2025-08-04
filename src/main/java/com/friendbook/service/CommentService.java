package com.friendbook.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	public List<Comment> getCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId).orElse(null);
		return post != null ? commentRepository.findByPostOrderByCreatedAtAsc(post) : List.of();
	}

	public void addComment(String userEmail, Long postId, String text) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		if (user != null && post != null && !text.trim().isEmpty()) {
			Comment c = new Comment();
			c.setUser(user);
			c.setPost(post);
			c.setText(text);
			commentRepository.save(c);
		}
	}
}
