package com.friendbook.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.PostService;

@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Override
	public void createPost(String caption, MultipartFile image, String userEmail) throws IOException{
		User user = userRepository.findByEmail(userEmail).orElse(null);
		
		if(user != null && image != null && !image.isEmpty()) {
			String originalFilename = image.getOriginalFilename();
	        if (originalFilename == null || !originalFilename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
	            throw new IllegalArgumentException("Only image files (jpg, jpeg, png, gif) are allowed.");
	        }

	        String contentType = image.getContentType();
	        if (contentType == null || !contentType.startsWith("image/")) {
	            throw new IllegalArgumentException("Invalid image file type.");
	        }
			String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
			Path path = Paths.get("uploads/posts/" + fileName);
			Files.write(path, image.getBytes());
			Post post = new Post();
			post.setCaption(caption);
			post.setImagePath(fileName);
			post.setUser(user);
			postRepository.save(post);
		}
	}
	
	@Override
	public List<Post> getAllPosts(){
		List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
		posts.forEach(post -> post.getComments().size());
		return posts;
	}
	
	@Override
	public List<Post> getUserPosts(User user){
		return postRepository.findByUserOrderByCreatedAtDesc(user);
	}
	
	@Override
	public void deletePost(Long postId, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		if(post != null && post.getUser().equals(user)) {
			postRepository.delete(post);
		}
	}
	
	@Override
	public Post getPostById(Long id) {
		return postRepository.findById(id).orElse(null);
	}
	
	@Override
	public void updatePostCaption(Long postId, String newCaption, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		
		if(post != null && post.getUser().equals(user)) {
			post.setCaption(newCaption);
			postRepository.save(post);
		}
	}
	
	@Override
	public Long getLikeCount(Long postId) {
		Post post = postRepository.findById(postId).orElse(null);
		return post != null ? likeRepository.countByPost(post) : 0;
	}
	
}
