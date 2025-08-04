package com.friendbook.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.service.FollowService;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
	
	@Autowired
	private FollowService followService;

	@PostMapping("/{targetUserId}")
	public ResponseEntity<String> follow(@PathVariable Long targetUserId, Principal principal) {
		boolean ok = followService.followUser(principal.getName(), targetUserId);
		return ok ? ResponseEntity.ok("Followed") : ResponseEntity.badRequest().body("Error");
	}

	@DeleteMapping("/{targetUserId}")
	public ResponseEntity<String> unfollow(@PathVariable Long targetUserId, Principal principal) {
		boolean ok = followService.unfollowUser(principal.getName(), targetUserId);
		return ok ? ResponseEntity.ok("Unfollowed") : ResponseEntity.badRequest().body("Error");
	}
}
