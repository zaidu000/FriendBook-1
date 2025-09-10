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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.model.Post;
import com.friendbook.model.PostLike;
import com.friendbook.model.User;
import com.friendbook.repository.PostRepository;
import com.friendbook.service.impl.LikeServiceImpl;

@WebMvcTest(LikeController.class)
@Import(TestSecurityConfig.class)
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeServiceImpl likeService;

    @MockBean
    private PostRepository postRepository;

    private User user1;
    private Post post;
    private Principal principal;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(2L);
        user1.setUsername("user1"); // or getUsernameField() depending on actual method
        user1.setFullName("User One");
        user1.setProfileImage("profile1.png");

        PostLike like = new PostLike();
        like.setId(1L);
        like.setUser(user1);

        post = new Post();
        post.setId(1L);
        post.setLikes(List.of(like));

        principal = () -> "user@example.com";
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void testToggleLike() throws Exception {
        when(likeService.toggleLike(any(Long.class), any(String.class))).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/likes/1")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(likeService, times(1)).toggleLike(1L, principal.getName());
    }
    
    @Test
    @WithMockUser(username = "user@example.com")
    void testGetLikesForPost() throws Exception {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/likes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1")) // or adjust if method is different
                .andExpect(jsonPath("$[0].fullName").value("User One"))
                .andExpect(jsonPath("$[0].profileImage").value("profile1.png"));

        verify(postRepository, times(1)).findById(1L);
    }
}
