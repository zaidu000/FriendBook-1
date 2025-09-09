package com.friendbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.impl.FollowServiceImpl;
import com.friendbook.service.impl.PostServiceImpl;

@WebMvcTest(PageController.class)
@Import(TestSecurityConfig.class)
public class PageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepo;

	@MockBean
	private FollowServiceImpl followService;

	@MockBean
	private PostServiceImpl postService;

	private User user;
	private Principal principal;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setFullName("Test user");
		user.setEmail("test@example.com");
		user.setProfileImage("default.png");

		principal = () -> "test@example.com";
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testShowProfilePage() throws Exception {
		when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		when(postService.getUserPosts(user)).thenReturn(new ArrayList<>());

		mockMvc.perform(MockMvcRequestBuilders.get("/profile")
				.principal(principal))
				.andExpect(status().isOk())
				.andExpect(view().name("profile"))
				.andExpect(model().attributeExists("user"))
				.andExpect(model().attribute("loggedInUserId", 1L));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testUploadProfileImage() throws Exception {
		when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
		MockMultipartFile file = new MockMultipartFile("image", "profile.png", "image/png",
				"fake image content".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(MockMvcRequestBuilders.multipart("/profile/upload")
				.file(file))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));

		verify(userRepo, times(1)).save(any(User.class));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testUpdateProfile() throws Exception {
		when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

		mockMvc.perform(MockMvcRequestBuilders.post("/profile/update")
				.param("favSongs", "Song1, Song2")
				.param("favBooks", "Book1, Book2")
				.param("favPlaces", "Place1, Place2")
				.principal(principal))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));

		verify(userRepo, times(1)).save(any(User.class));
	}

	@Test
	void testShowHomePage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}

	@Test
	void testShowAboutPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/about"))
				.andExpect(status().isOk())
				.andExpect(view().name("about"));
	}

	@Test
	void testShowSignupPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(status().isOk())
				.andExpect(view().name("signup"));
	}

	@Test
	void testShowLoginPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));
	}
}
