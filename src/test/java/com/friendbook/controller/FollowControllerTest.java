package com.friendbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.exception.UserNotFoundException;
import com.friendbook.model.User;
import com.friendbook.service.impl.FollowServiceImpl;
import com.friendbook.service.impl.FollowServiceImpl.UnfollowResult;
import com.friendbook.service.impl.UserServiceImpl;

@WebMvcTest(FollowController.class)
@Import(TestSecurityConfig.class)
public class FollowControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FollowServiceImpl followService;

	@MockBean
	private UserServiceImpl userService;

	private User currentUser;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("user@example.com");
		currentUser.setUsername("user");
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testFollow() throws Exception {
		when(followService.followUser("user@example.com", 2L)).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/follow/2")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Followed"));

		verify(followService, times(1)).followUser("user@example.com", 2L);
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testUnfollow() throws Exception {
		UnfollowResult result = new UnfollowResult(true, 5, 3);
		when(followService.unfollow("user@example.com", 2L)).thenReturn(result);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/unfollow/2")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Unfollowed")).andExpect(jsonPath("$.followingCount").value(5))
				.andExpect(jsonPath("$.targetFollowersCount").value(3));

		verify(followService, times(1)).unfollow("user@example.com", 2L);
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testUnfollowUserNotFound() throws Exception {
		when(followService.unfollow("user@example.com", 2L)).thenThrow(new UserNotFoundException("User not found"));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/unfollow/2")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("User not found"));

		verify(followService, times(1)).unfollow("user@example.com", 2L);
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testGetFollowers() throws Exception {
		User follower = new User();
		follower.setId(2L);
		follower.setUsername("followerUser");
		follower.setFullName("Follower User");

		currentUser.setFollowers(Set.of(follower));
		when(userService.getUserByUsername("user@example.com")).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.get("/followers")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(2L)).andExpect(jsonPath("$[0].username").value("followerUser"))
				.andExpect(jsonPath("$[0].fullName").value("Follower User"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testGetFollowing() throws Exception {
		User following = new User();
		following.setId(3L);
		following.setUsername("followingUser");
		following.setFullName("Following User");

		currentUser.setFollowing(Set.of(following));
		when(userService.getUserByUsername("user@example.com")).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.get("/following")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(3L)).andExpect(jsonPath("$[0].username").value("followingUser"))
				.andExpect(jsonPath("$[0].fullName").value("Following User"));
	}
}
