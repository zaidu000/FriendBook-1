package com.friendbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;

class PostServiceTestImpl {

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private PostRepository postRepository;

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private UserRepository userRepository;

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private LikeRepository likeRepository;

    // Create an instance of class under test and inject the mocks annotated with @Mock
    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Create post with valid file
    @Test
    void testCreatePost_WithValidFile() throws IOException {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "dummy content".getBytes()
        );

        postService.createPost("Test Caption", new MockMultipartFile[]{file}, "user@example.com");

        verify(postRepository, times(2)).save(any(Post.class)); // Once before and once after adding media
    }

    // Create post with invalid file
    @Test
    void testCreatePost_InvalidFile_ThrowsException() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost("Caption", new MockMultipartFile[]{file}, "user@example.com");
        });

        assertEquals("Only image/video files are allowed.", exception.getMessage());
    }

    // Get All posts
    @Test
    void testGetAllPosts() {
        Post post1 = new Post();
        Post post2 = new Post();
        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(Arrays.asList(post1, post2));

        List<Post> posts = postService.getAllPosts();

        assertEquals(2, posts.size());
        verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    // Delete those post where user can own the post
    @Test
    void testDeletePost_UserOwnsPost() {
        User user = new User();
        user.setEmail("user@example.com");

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, "user@example.com");

        verify(postRepository, times(1)).delete(post);
    }

    // Delete those post where user does not own the post
    @Test
    void testDeletePost_UserDoesNotOwnPost() {
        User user = new User();
        user.setEmail("user@example.com");

        User otherUser = new User();
        otherUser.setEmail("other@example.com");

        Post post = new Post();
        post.setId(1L);
        post.setUser(otherUser); // Set to other user

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, "user@example.com");

        // Ensure delete is never called because user is not the owner
        verify(postRepository, never()).delete(any());
    }

    // Get like count if post exists
    @Test
    void testGetLikeCount_PostExists() {
        Post post = new Post();
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post)).thenReturn(5L);

        Long count = postService.getLikeCount(1L);

        assertEquals(5L, count);
    }
    
    // Get like count if post does not exists
    @Test
    void testGetLikeCount_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Long count = postService.getLikeCount(1L);

        assertEquals(0L, count);
    }
}
