package com.friendbook.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.Post;
import com.friendbook.model.User;

public interface PostService {

	void createPost(String caption, MultipartFile[] files, String userEmail) throws IOException;

	List<Post> getAllPosts();

	List<Post> getUserPosts(User user);

	void deletePost(Long postId, String userEmail);

	Post getPostById(Long id);

	void updatePostCaption(Long postId, String newCaption, String userEmail);

	Long getLikeCount(Long postId);
}
