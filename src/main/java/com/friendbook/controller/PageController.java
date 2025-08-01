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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;

@Controller
public class PageController {
	
	@Autowired
	private UserRepository userRepo;
	
    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }
    
    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/upload")
    public String uploadProfileImage(@RequestParam("image") MultipartFile file, Principal principal) throws IOException {
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (file != null && !file.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/images/" + filename);
            Files.write(path, file.getBytes());
            user.setProfileImage(filename);
            userRepo.save(user);
        }
        return "redirect:/profile";
    }     

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String favSongs,
                                @RequestParam String favBooks,
                                @RequestParam String favPlaces,
                                Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        user.setFavSongs(favSongs);
        user.setFavBooks(favBooks);
        user.setFavPlaces(favPlaces);
        userRepo.save(user);
        return "redirect:/profile";
    }
    
}
