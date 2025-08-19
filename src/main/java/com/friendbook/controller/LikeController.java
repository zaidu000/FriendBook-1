package com.friendbook.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.service.impl.LikeServiceImpl;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
	
	@Autowired
	private LikeServiceImpl likeService;

	@PostMapping("/{postId}")
	public ResponseEntity<String> toggleLike(@PathVariable Long postId, Principal principal) {
	    int likeCount = likeService.toggleLike(postId, principal.getName());
	    return ResponseEntity.ok(String.valueOf(likeCount));
	}

}
