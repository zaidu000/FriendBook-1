package com.friendbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.model.Post;
import com.friendbook.model.PostLike;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

class LikeServiceImplTest {

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private LikeRepository likeRepository;

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private UserRepository userRepository;

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private PostRepository postRepository;

    // Create an instance of class under test and inject the mocks annotated with @Mock
    @InjectMocks
    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Click on like button
    @Test
    void testToggleLike_AddLike() {
        User user = new User();
        user.setEmail("user@example.com");

        Post post = new Post();
        post.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(false);
        when(likeRepository.countByPost(post)).thenReturn(1L);

        int likeCount = likeService.toggleLike(1L, "user@example.com");

        verify(likeRepository).save(any(PostLike.class));
        assertEquals(1, likeCount);
    }

    // Remove like from the post
    @Test
    void testToggleLike_RemoveLike() {
        User user = new User();
        user.setEmail("user@example.com");

        Post post = new Post();
        post.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(true);
        when(likeRepository.countByPost(post)).thenReturn(0L);

        int likeCount = likeService.toggleLike(1L, "user@example.com");

        verify(likeRepository).deleteByUserAndPost(user, post);
        assertEquals(0, likeCount);
    }

    // Click on like button but either user or post is missing
    @Test
    void testToggleLike_UserOrPostMissing() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        int result = likeService.toggleLike(1L, "user@example.com");

        assertEquals(-1, result);
    }

    // Get like count of that post
    @Test
    void testGetLikeCount() {
        Post post = new Post();
        post.setId(1L);

        when(likeRepository.countByPost(post)).thenReturn(5L);

        long count = likeService.getLikeCount(post);

        assertEquals(5, count);
    }

    @Test
    void testHasUserLiked_WhenLiked() {
        User user = new User();
        user.setEmail("user@example.com");

        Post post = new Post();
        post.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(true);

        assertTrue(likeService.hasUserLiked("user@example.com", 1L));
    }

    @Test
    void testHasUserLiked_WhenNotLiked() {
        User user = new User();
        user.setEmail("user@example.com");

        Post post = new Post();
        post.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserAndPost(user, post)).thenReturn(false);

        assertFalse(likeService.hasUserLiked("user@example.com", 1L));
    }
}
