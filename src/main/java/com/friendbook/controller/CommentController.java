package com.friendbook.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.friendbook.model.User;
import com.friendbook.service.CommentService;
import com.friendbook.service.UserService;

@Controller
public class CommentController {
//
//	@Autowired
//	private CommentService commentService;
//	
//	@Autowired
//	private UserService userService;
//
//	@PostMapping("comments/{postId}")
//	public String addComment(@PathVariable Long postId, @RequestParam("text") String text,
//			Principal principal) {
//		try {
//	        User user = userService.getUserByUsername(principal.getName());
//	        commentService.saveComment(postId, user, text);
//	        return "redirect:/posts";
//	    } catch (Exception e) {
//	        return "error";
//	    }
//	}
}
