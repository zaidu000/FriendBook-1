package com.friendbook.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.model.Comment;
import com.friendbook.model.User;
import com.friendbook.service.CommentService;
import com.friendbook.service.UserService;

@RestController
public class CommentRestController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private UserService userService;

	@PostMapping("comments/{postId}")
	@ResponseBody
	public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestParam("text") String text,
			Principal principal) {
		try {
			User user = userService.getUserByUsername(principal.getName());
			commentService.saveComment(postId, user, text);
			return ResponseEntity.ok("Comment added successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add comment");
		}
	}

	@GetMapping("api/comments/{postId}")
	public List<Comment> getComments(@PathVariable Long postId) {
		return commentService.getCommentsForPost(postId);
	}
}
