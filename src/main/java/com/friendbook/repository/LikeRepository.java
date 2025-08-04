package com.friendbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.	PostLike;
import com.friendbook.model.Post;
import com.friendbook.model.User;

@Repository
public interface LikeRepository extends JpaRepository<PostLike,Long>{
	
	boolean existsByUserAndPost(User user, Post post);

	void deleteByUserAndPost(User user, Post post);

	long countByPost(Post post);
}
