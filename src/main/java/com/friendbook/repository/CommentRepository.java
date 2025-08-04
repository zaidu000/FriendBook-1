package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.Comment;
import com.friendbook.model.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPostOrderByCreatedAtAsc(Post post);
}
