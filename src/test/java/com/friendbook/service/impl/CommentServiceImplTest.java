package com.friendbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

class CommentServiceImplTest {

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    // Create an instance of class under test and inject the mocks annotated with @Mock
    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // If post exists, we can retrieve comments
    @Test
    void testGetCommentsForPost_PostExists() {
        Post post = new Post();
        post.setId(1L);

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostOrderByCreatedAtAsc(post)).thenReturn(comments);

        List<Comment> result = commentService.getCommentsForPost(1L);

        assertEquals(2, result.size());
        assertEquals(comments, result);
    }

    // If post does not exists, we can retrieve comments
    @Test
    void testGetCommentsForPost_PostNotExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        List<Comment> result = commentService.getCommentsForPost(1L);
        assertTrue(result.isEmpty());
    }

    // Add a comment
    @Test
    void testSaveComment_Success() {
        Post post = new Post();
        post.setId(1L);

        User user = new User();
        user.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        commentService.saveComment(1L, user, "Test comment");

        verify(commentRepository).save(any(Comment.class));
    }

    // Add a comment but post is not found
    @Test
    void testSaveComment_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.saveComment(1L, new User(), "Test comment");
        });

        assertEquals("Post not found", exception.getMessage());
    }

    // Delete a comment successfully
    @Test
    void testDeleteComment_Success() {
        User user = new User();
        user.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        boolean result = commentService.deleteComment(1L, user);

        assertTrue(result);
        verify(commentRepository).delete(comment);
    }

    // Delete that comment, those are not found
    @Test
    void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(1L, new User());
        });

        assertEquals("Comment not found", exception.getMessage());
    }

    // Unauthorized user for delete comment
    @Test
    void testDeleteComment_UnauthorizedUser() {
        User user = new User();
        user.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUser(anotherUser);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(1L, user);
        });

        assertEquals("Unauthorzied deletion attempt", exception.getMessage());
    }
}
