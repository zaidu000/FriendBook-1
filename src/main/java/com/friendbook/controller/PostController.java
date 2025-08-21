package com.friendbook.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.impl.PostServiceImpl;

@Controller
public class PostController {

	@Autowired
	private PostServiceImpl postService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@GetMapping("/posts")
	public String viewAllPosts(Model model, Principal principal) {
		User currentUser = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		Set<User> following = currentUser.getFollowing();
		Set<User> usersToFetchfrom = new HashSet<>(following);
		usersToFetchfrom.add(currentUser);

		List<Post> posts = postRepository.findByUserInOrderByCreatedAtDesc(usersToFetchfrom);

		model.addAttribute("posts", posts);
		model.addAttribute("user", currentUser);
		return "posts";
	}

	@PostMapping("/posts/create")
	public String createPost(@RequestParam String caption, @RequestParam("files") MultipartFile[] files, Principal principal,
			RedirectAttributes redirectAttributes) throws IOException {
		try {
			postService.createPost(caption, files, principal.getName());
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/profile";
		}
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
