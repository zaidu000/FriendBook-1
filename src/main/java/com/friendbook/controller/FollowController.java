package com.friendbook.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.model.User;
import com.friendbook.service.FollowService;
import com.friendbook.service.UserService;

@RestController
public class FollowController {
	
	@Autowired
	private FollowService followService;
	
	@Autowired
	private UserService userService;

	@PostMapping("/api/follow/{targetUserId}")
	public ResponseEntity<String> follow(@PathVariable Long targetUserId, Principal principal) {
		boolean ok = followService.followUser(principal.getName(), targetUserId);
		return ok ? ResponseEntity.ok("Followed") : ResponseEntity.badRequest().body("Error");
	}

	@DeleteMapping("/api/follow/{targetUserId}")
	public ResponseEntity<String> unfollow(@PathVariable Long targetUserId, Principal principal) {
		boolean ok = followService.unfollowUser(principal.getName(), targetUserId);
		return ok ? ResponseEntity.ok("Unfollowed") : ResponseEntity.badRequest().body("Error");
	}
	
	@GetMapping("/followers")
	@ResponseBody
	public List<Map<String, String>> getFollowers1(Principal principal) {
	    User user = userService.getUserByUsername(principal.getName());
	    List<User> followers = new ArrayList<>(user.getFollowers());
	    return followers.stream().map(f -> {
	        Map<String, String> map = new HashMap<>();
	        map.put("username", f.getUsernameField());
	        map.put("fullName", f.getFullName());
	        return map;
	    }).collect(Collectors.toList());
	}

	@GetMapping("/following")
	@ResponseBody
	public List<Map<String, String>> getFollowing1(Principal principal) {
	    User user = userService.getUserByUsername(principal.getName());
	    List<User> following = new ArrayList<>(user.getFollowing());
	    return following.stream().map(f -> {
	        Map<String, String> map = new HashMap<>();
	        map.put("username", f.getUsernameField());
	        map.put("fullName", f.getFullName());
	        return map;
	    }).collect(Collectors.toList());
	}

}
