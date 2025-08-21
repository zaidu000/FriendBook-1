package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.Media;
import com.friendbook.model.Post;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {

	List<Media> findByPost(Post post);
}
