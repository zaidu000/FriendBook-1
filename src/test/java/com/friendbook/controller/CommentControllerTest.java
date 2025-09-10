package com.friendbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.List;

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
import com.friendbook.model.Comment;
import com.friendbook.model.User;
import com.friendbook.service.impl.CommentServiceImpl;
import com.friendbook.service.impl.UserServiceImpl;

@WebMvcTest(CommentRestController.class)
@Import(TestSecurityConfig.class)
public class CommentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CommentServiceImpl commentService;

	@MockBean
	private UserServiceImpl userService;

	private User currentUser;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("user@example.com");
		currentUser.setUsername("user");
		currentUser.setFullName("User Test");
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testAddComment() throws Exception {
		when(userService.getUserByUsername("user@example.com")).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.post("/comments/1").param("text", "This is a test comment")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk())
				.andExpect(content().string("Comment added successfully"));

		verify(commentService, times(1)).saveComment(eq(1L), eq(currentUser), eq("This is a test comment"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testGetComments() throws Exception {
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setText("A sample comment");

		when(commentService.getCommentsForPost(1L)).thenReturn(List.of(comment));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[0].text").value("A sample comment"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testDeleteComment() throws Exception {
		when(userService.getUserByUsername("user@example.com")).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.delete("/comments/delete/1")).andExpect(status().isOk())
				.andExpect(content().string("Comment deleted successfully"));

		verify(commentService, times(1)).deleteComment(1L, currentUser);
	}
}
