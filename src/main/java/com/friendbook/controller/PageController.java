package com.friendbook.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.FollowService;
import com.friendbook.service.PostService;

@Controller
public class PageController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private FollowService followService;
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/")
	public String showHomePage() {
		return "index";
	}
	
	@GetMapping("/about")
	public String showAboutPage() {
		return "about";
	}

	@GetMapping("/signup")
	public String showSignupPage() {
		return "signup";
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@GetMapping("/profile")
	public String showProfilePage(Model model, Principal principal) {
		User user = userRepo.findByEmail(principal.getName()).orElse(null);
		model.addAttribute("user", user);
		model.addAttribute("followersCount", user.getFollowers().size());
		model.addAttribute("followingCount", user.getFollowing().size());
		model.addAttribute("viewingOtherUser", false);
		model.addAttribute("loggedInUserId", user.getId());
		model.addAttribute("userPosts", postService.getUserPosts(user));
		return "profile";
	}

	public String viewOtherProfilePage(@PathVariable Long id, Model model, Principal principal) {
		User viewer = userRepo.findByEmail(principal.getName()).orElse(null);
		User user = userRepo.findById(id).orElse(null);
		if (user == null) {
			return "redirect:/";
		}
		model.addAttribute("user", user);
		model.addAttribute("followersCount", followService.countFollowers(user));
		model.addAttribute("followingCount", followService.countFollowings(user));
		model.addAttribute("viewingOtherUser", true);
		model.addAttribute("loggedInUserId", viewer.getId());
		model.addAttribute("isFollowing", followService.isFollowing(viewer, user));
		return "profile";
	}

	@PostMapping("/profile/upload")
	public String uploadProfileImage(@RequestParam("image") MultipartFile file, Principal principal) throws IOException {
		User user = userRepo.findByEmail(principal.getName()).orElse(null);
		if (user != null && file != null && !file.isEmpty()) {
			String contentType = file.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				throw new IllegalArgumentException("Only image files are allowed");
			}
			String originalFilename = file.getOriginalFilename().toLowerCase();
			if (!(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg") || originalFilename.endsWith(".png") || originalFilename.endsWith(".gif"))) {
				throw new IllegalArgumentException("Only JPG, JPEG, PNG, or GIF files are allowed");
			}

			String filename = UUID.randomUUID() + "_" + originalFilename;
			Path path = Paths.get("uploads/images/" + filename);
			Files.write(path, file.getBytes());

			user.setProfileImage(filename);
			userRepo.save(user);
		}

		return "redirect:/profile";
	}


	@PostMapping("/profile/update")
	public String updateProfile(@RequestParam String favSongs, @RequestParam String favBooks,
			@RequestParam String favPlaces, Principal principal) {
		User user = userRepo.findByEmail(principal.getName()).orElse(null);
		user.setFavSongs(favSongs);
		user.setFavBooks(favBooks);
		user.setFavPlaces(favPlaces);
		userRepo.save(user);
		return "redirect:/profile";
	}

}
