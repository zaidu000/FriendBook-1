package com.friendbook.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.model.Comment;
import com.friendbook.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	@Autowired
	private CommentService commentService;

	@PostMapping("/{postId}")
	public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestParam String text,
			Principal principal) {
		commentService.addComment(principal.getName(), postId, text);
		return ResponseEntity.ok("Comment added");
	}

	@GetMapping("/{postId}")
	public List<Comment> getComments(@PathVariable Long postId) {
		return commentService.getCommentsForPost(postId);
	}
}
