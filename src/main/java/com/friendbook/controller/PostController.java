package com.friendbook.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.Post;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.PostService;

@Controller
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/posts")
	public String viewAllPosts(Model model, Principal principal) {
		model.addAttribute("posts", postService.getAllPosts());
		return "posts";
	}

	@PostMapping("/posts/create")
	public String createPost(@RequestParam String caption, @RequestParam MultipartFile image, Principal principal)
			throws IOException {
		postService.createPost(caption, image, principal.getName());
		return "redirect:/profile";
	}

	@PostMapping("/posts/delete/{postId}")
	public String deletePost(@PathVariable Long postId, Principal principal) {
		postService.deletePost(postId, principal.getName());
		return "redirect:/profile";
	}

	@GetMapping("/posts/edit/{postId}")
	public String showEditPostForm(@PathVariable Long postId, Model model, Principal principal) {
		Post post = postService.getPostById(postId);
		if (post != null && post.getUser().getEmail().equals(principal.getName())) {
			model.addAttribute("post", post);
			return "edit_post";
		}
		return "redirect:/profile";
	}

	@PostMapping("/posts/update/{postId}")
	public String updatePost(@PathVariable Long postId, @RequestParam String caption, Principal principal) {
		postService.updatePostCaption(postId, caption, principal.getName());
		return "redirect:/profile";
	}
}
