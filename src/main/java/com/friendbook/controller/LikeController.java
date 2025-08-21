package com.friendbook.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.model.Post;
import com.friendbook.repository.PostRepository;
import com.friendbook.service.impl.LikeServiceImpl;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

	@Autowired
	private LikeServiceImpl likeService;

	@Autowired
	private PostRepository postRepository;

	@PostMapping("/{postId}")
	public ResponseEntity<String> toggleLike(@PathVariable Long postId, Principal principal) {
		int likeCount = likeService.toggleLike(postId, principal.getName());
		return ResponseEntity.ok(String.valueOf(likeCount));
	}

	@GetMapping("/{postId}")
	public List<Map<String, String>> getLikesForPost(@PathVariable Long postId) {
		Post post = postRepository.findById(postId).orElseThrow();

		return post.getLikes().stream().map(like -> {
			Map<String, String> userData = new HashMap<>();
			userData.put("username", like.getUser().getUsernameField());
			userData.put("fullName", like.getUser().getFullName());
			userData.put("profileImage", like.getUser().getProfileImage());
			return userData;
		}).toList();
	}
}
