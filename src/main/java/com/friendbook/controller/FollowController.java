package com.friendbook.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.exception.UserNotFoundException;
import com.friendbook.model.User;
import com.friendbook.service.impl.FollowServiceImpl;
import com.friendbook.service.impl.FollowServiceImpl.UnfollowResult;
import com.friendbook.service.impl.UserServiceImpl;

@RestController
public class FollowController {

	@Autowired
	private FollowServiceImpl followService;

	@Autowired
	private UserServiceImpl userService;

	@PostMapping("/api/follow/{targetUserId}")
	public ResponseEntity<?> follow(@PathVariable Long targetUserId, Principal principal) {
		boolean ok = followService.followUser(principal.getName(), targetUserId);
		return ok ? ResponseEntity.ok(Map.of("message", "Followed"))
				: ResponseEntity.badRequest().body(Map.of("error", "Error"));
	}

	@PostMapping("/api/unfollow/{targetUserId}")
	public ResponseEntity<?> unfollowUser(@PathVariable("targetUserId") Long targetUserId, Principal principal) {
	    String currentUsername = principal.getName();
	    System.out.println("=== DEBUG START ===");
	    System.out.println("targetUserId = " + targetUserId);
	    System.out.println("principal = " + (principal != null ? principal.getName() : "null"));
	    System.out.println("=== DEBUG END ===");
	    try {
	        UnfollowResult result = followService.unfollow(currentUsername, targetUserId);
	        if (!result.ok()) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Unfollow failed"));
	        }
	        return ResponseEntity.ok(Map.of(
	            "message", "Unfollowed",
	            "followingCount", result.followingCount(),
	            "targetFollowersCount", result.targetFollowersCount()
	        ));
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
	    }
	}

	@GetMapping("/followers")
	@ResponseBody
	public List<Map<String, Object>> getFollowers1(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<User> followers = new ArrayList<>(user.getFollowers());
		return followers.stream().map(f -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", f.getId());
			map.put("username", f.getUsernameField());
			map.put("fullName", f.getFullName());
			return map;
		}).collect(Collectors.toList());
	}

	@GetMapping("/following")
	@ResponseBody
	public List<Map<String, Object>> getFollowing1(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<User> following = new ArrayList<>(user.getFollowing());
		return following.stream().map(f -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", f.getId());
			map.put("username", f.getUsernameField());
			map.put("fullName", f.getFullName());
			return map;
		}).collect(Collectors.toList());
	}
}
