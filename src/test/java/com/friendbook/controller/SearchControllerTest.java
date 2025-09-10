package com.friendbook.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.model.User;
import com.friendbook.service.impl.FriendRequestServiceImpl;
import com.friendbook.service.impl.UserServiceImpl;

@WebMvcTest(SearchController.class)
@Import(TestSecurityConfig.class)
public class SearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userService;

	@MockBean
	private FriendRequestServiceImpl friendRequestService;

	private User user;
	private User otherUser;
	private Principal principal;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setFullName("Test User");
		user.setEmail("test@example.com");
		user.setPosts(new ArrayList<>());
		user.setProfileImage("default.png");

		otherUser = new User();
		otherUser.setId(2L);
		otherUser.setUsername("otheruser");
		otherUser.setFullName("Other User");
		otherUser.setEmail("other@example.com");
		otherUser.setPosts(new ArrayList<>());
		otherUser.setProfileImage("other.png");

		principal = () -> "test@example.com";
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testSearchUsers() throws Exception {
		List<User> users = List.of(otherUser);
		when(userService.searchUsersByUsername("other")).thenReturn(users);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/search/users")
				.param("query", "other"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username").value("otheruser"));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testViewUserProfile() throws Exception {
		when(userService.getUserByUsername1("otheruser")).thenReturn(Optional.of(otherUser));
		when(userService.getUserByUsername1("test@example.com")).thenReturn(Optional.of(user));
		when(friendRequestService.getRequestStatus(user, otherUser)).thenReturn("none");

		mockMvc.perform(MockMvcRequestBuilders.get("/user/otheruser")
				.principal(principal))
				.andExpect(status().isOk())
				.andExpect(view().name("user-card"))
				.andExpect(model().attributeExists("user"))
				.andExpect(model().attributeExists("posts"))
				.andExpect(model().attributeExists("followersCount"))
				.andExpect(model().attributeExists("followingCount"))
				.andExpect(model().attributeExists("requestStatus"))
				.andExpect(model().attributeExists("isFollowing"));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testGetUserProfileJson() throws Exception {
		when(userService.getUserByUsername1("otheruser")).thenReturn(Optional.of(otherUser));
		when(userService.getUserByUsername1("test@example.com")).thenReturn(Optional.of(user));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/user/otheruser")
				.principal(principal))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Other User"))
				.andExpect(jsonPath("$.username").value("otheruser"))
				.andExpect(jsonPath("$.email").value("other@example.com"));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testFollowUser() throws Exception {
		when(userService.getUserByUsername1("test@example.com")).thenReturn(Optional.of(user));
		when(userService.getUserByUsername1("otheruser")).thenReturn(Optional.of(otherUser));

		mockMvc.perform(MockMvcRequestBuilders.post("/follow/otheruser")
				.principal(principal))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/user/otheruser"));

		verify(userService, times(1)).save(otherUser);
	}
}
