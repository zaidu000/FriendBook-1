package com.friendbook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendbook.model.PostLike;
import com.friendbook.model.Post;
import com.friendbook.model.User;
import com.friendbook.repository.LikeRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.LikeService;

import jakarta.transaction.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Override
	@Transactional
	public int toggleLike(Long postId, String userEmail) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		if (user != null && post != null) {
			boolean liked = likeRepository.existsByUserAndPost(user, post);
			if (liked) {
				likeRepository.deleteByUserAndPost(user, post);
			} else {
				PostLike like = new PostLike();
				like.setUser(user);
				like.setPost(post);
				likeRepository.save(like);
			}
			return (int) likeRepository.countByPost(post);
		}
		return -1;
	}

	@Override
	public long getLikeCount(Post post) {
		return likeRepository.countByPost(post);
	}

	@Override
	public boolean hasUserLiked(String userEmail, Long postId) {
		User user = userRepository.findByEmail(userEmail).orElse(null);
		Post post = postRepository.findById(postId).orElse(null);
		return user != null && post != null && likeRepository.existsByUserAndPost(user, post);
	}
}
