package com.friendbook.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.Post;
import com.friendbook.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByUserOrderByCreatedAtDesc(User user);
	
	List<Post> findByUserInOrderByCreatedAtDesc(Set<User> users);

	List<Post> findAllByOrderByCreatedAtDesc();
}
