package com.friendbook.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.friendbook.model.User;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;

@Controller
public class SearchController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/api/search/users")
	public ResponseEntity<List<Map<String, String>>> searchUsers(@RequestParam String query) {
	    List<User> matchedUsers = userService.searchUsersByUsername(query);
	    List<Map<String, String>> response = matchedUsers.stream()
	        .map(user -> Map.of("username", user.getUsernameField()))
	        .collect(Collectors.toList());
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/user/{username}")
    public String viewUserProfile(@PathVariable String username, Model model, Principal principal) {
        Optional<User> userOpt = userService.getUserByUsername1(username);
        if (userOpt.isEmpty()) {
            return "error"; // handle gracefully
        }

        User viewedUser = userOpt.get();
        model.addAttribute("user", viewedUser);
        model.addAttribute("posts", viewedUser.getPosts());
        model.addAttribute("followersCount", viewedUser.getFollowers().size());
        model.addAttribute("followingCount", viewedUser.getFollowing().size());

        // check if current user follows this user
        User currentUser = userService.getUserByUsername1(principal.getName()).orElse(null);
        boolean isFollowing = viewedUser.getFollowers().contains(currentUser);
        model.addAttribute("isFollowing", isFollowing);

        return "user-card"; 
    }
	
	@GetMapping("/api/user/{username}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getUserProfileJson(@PathVariable String username, Principal principal) {
	    Optional<User> userOpt = userService.getUserByUsername1(username);
	    if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

	    User viewedUser = userOpt.get();
	    User currentUser = userService.getUserByUsername1(principal.getName()).orElse(null);

	    Map<String, Object> data = Map.of(
	        "name", viewedUser.getFullName(),
	        "username", viewedUser.getUsernameField(),
	        "email", viewedUser.getEmail(),
	        "followers", viewedUser.getFollowers().size(),
	        "following", viewedUser.getFollowing().size(),
	        "postCount", viewedUser.getPosts().size(),
	        "profilePic", viewedUser.getProfileImage(),
	        "isFollowing", viewedUser.getFollowers().contains(currentUser)
	    );

	    return ResponseEntity.ok(data);
	}
	
	@PostMapping("/follow/{username}")
    public String followUser(@PathVariable String username, Principal principal) {
        User currentUser = userService.getUserByUsername1(principal.getName()).orElse(null);
        User toFollow = userService.getUserByUsername1(username).orElse(null);
        if (currentUser != null && toFollow != null && !toFollow.getFollowers().contains(currentUser)) {
            toFollow.getFollowers().add(currentUser);
            userService.save(toFollow);
        }
        return "redirect:/user/" + username;
    }
}
