package com.friendbook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.Follow;
import com.friendbook.model.User;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
	boolean existsByFollowerAndFollowing(User follower, User following);

	void deleteByFollowerAndFollowing(User follower, User following);

	List<Follow> findByFollowing(User following);

	List<Follow> findByFollower(User follower);

	long countByFollower(User follower);

	long countByFollowing(User following);
}
