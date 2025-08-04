package com.friendbook.service;

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
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;

@Service
public class PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public void createPost(String caption, MultipartFile image, String userEmail) throws IOException{
		User user = userRepository.findByEmail(userEmail).orElse(null);
		
		if(user != null && image != null && !image.isEmpty()) {
			String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
			Path path = Paths.get("src/main/resources/static/posts/" + fileName);
			Files.write(path, image.getBytes());
			Post post = new Post();
			post.setCaption(caption);
			post.setImagePath(fileName);
			post.setUser(user);
			postRepository.save(post);
		}
	}
	
	public List<Post> getAllPosts(){
		return postRepository.findAllByOrderByCreatedAtDesc();
	}
	
	public List<Post> getUserPosts(User user){
		return postRepository.findByUserOrderByCreatedAtDesc(user);
	}
	
	public void deletePost(Long postId, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		if(post != null && post.getUser().equals(user)) {
			postRepository.delete(post);
		}
	}
	
	public Post getPostById(Long id) {
		return postRepository.findById(id).orElse(null);
	}
	
	public void updatePostCaption(Long postId, String newCaption, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		
		if(post != null && post.getUser().equals(user)) {
			post.setCaption(newCaption);
			postRepository.save(post);
		}
	}
	
}
