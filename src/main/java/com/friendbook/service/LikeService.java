package com.friendbook.service;

import com.friendbook.model.Post;

public interface LikeService {

	int toggleLike(Long postId, String userEmail);

	long getLikeCount(Post post);

	boolean hasUserLiked(String userEmail, Long postId);
}
