package com.friendbook.service;

import java.time.LocalDateTime;
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

	public void saveComment(Long postId, User user, String text) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

		Comment comment = new Comment();
		comment.setText(text);
		comment.setPost(post);
		comment.setUser(user);
		comment.setCreatedAt(LocalDateTime.now());
		commentRepository.save(comment);
	}

}
