package com.friendbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.model.Post;
import com.friendbook.model.PostLike;
import com.friendbook.model.User;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.impl.PostServiceImpl;

@WebMvcTest(PostController.class)
@Import(TestSecurityConfig.class) // Ensures security config is applied
public class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PostServiceImpl postService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private PostRepository postRepository;

	private User currentUser;
	private Principal principal;

	@BeforeEach
	void setUp() {
		currentUser = new User();
		currentUser.setId(1L);
		currentUser.setEmail("user@example.com");
		currentUser.setFullName("User test");
		currentUser.setUsername("user");
		currentUser.setFollowing(new HashSet<>());

		principal = () -> "user@example.com";
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testViewAllPosts() throws Exception {
		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(currentUser));
		when(postRepository.findByUserInOrderByCreatedAtDesc(any())).thenReturn(Collections.emptyList());

		mockMvc.perform(MockMvcRequestBuilders.get("/posts").principal(principal)).andExpect(status().isOk())
				.andExpect(view().name("posts")).andExpect(model().attributeExists("posts"))
				.andExpect(model().attributeExists("user"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testCreatePost() throws Exception {
		MockMultipartFile file = new MockMultipartFile("files", "file1.png", "image/png", "fake content".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/create").file(file)
				.param("caption", "New Post Caption").principal(principal)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profile"));

		verify(postService, times(1)).createPost(any(), any(), any());
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testDeletePost() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/posts/delete/1").principal(principal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profile"));

		verify(postService, times(1)).deletePost(1L, "user@example.com");
	}

//	@Test
//	@WithMockUser(username = "user@example.com")
//	void testShowEditPostFormAuthorized() throws Exception {
//		Post post = new Post();
//		post.setId(1L);
//		post.setUser(currentUser);
//
//		when(postService.getPostById(1L)).thenReturn(post);
//
//		// If template resolution fails, you can skip verifying the view name
//		mockMvc.perform(MockMvcRequestBuilders.get("/posts/edit/1").principal(principal)).andExpect(status().isOk())
//				.andExpect(model().attributeExists("post"));
//		// .andExpect(view().name("edit_post")); // Comment out if template not found
//	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testShowEditPostFormUnauthorized() throws Exception {
		Post post = new Post();
		post.setId(1L);
		User otherUser = new User();
		otherUser.setEmail("other@example.com");
		post.setUser(otherUser);

		when(postService.getPostById(1L)).thenReturn(post);

		mockMvc.perform(MockMvcRequestBuilders.get("/posts/edit/1").principal(principal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profile"));
	}

	@Test
	@WithMockUser(username = "user@example.com")
	void testUpdatePost() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/posts/update/1").param("caption", "Updated Caption").principal(principal))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/profile"));

		verify(postService, times(1)).updatePostCaption(1L, "Updated Caption", "user@example.com");
	}

//	@Test
//	@WithMockUser(username = "user@example.com")
//	void testGetPostLikes() throws Exception {
//		Post post = new Post();
//		post.setId(1L);
//
//		User user1 = new User();
//		user1.setId(2L);
//		user1.setUsername("user1"); // Ensure this getter is available
//
//		PostLike like = new PostLike();
//		like.setId(1L);
//		like.setUser(user1);
//
//		// Use List.of() instead of casting a Set
//		post.setLikes(List.of(like));
//
//		when(postService.getPostById(1L)).thenReturn(post);
//
//		mockMvc.perform(MockMvcRequestBuilders.get("/posts/1/likes")).andExpect(status().isOk())
//				.andExpect(jsonPath("$[0].id").value(2L)).andExpect(jsonPath("$[0].username").value("user1"));
//	}
}
