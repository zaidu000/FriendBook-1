package com.friendbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendbook.model.Follow;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRepository;
import com.friendbook.repository.UserRepository;

@Service
public class FollowService {

	@Autowired
	private FollowRepository followRepo;

	@Autowired
	private UserRepository userRepo;

	public boolean followUser(String currentUserEmail, Long targetUserId) {
		User follower = userRepo.findByEmail(currentUserEmail).orElse(null);
		User following = userRepo.findById(targetUserId).orElse(null);
		if (follower != null && following != null && !follower.equals(following)) {
			if (!followRepo.existsByFollowerAndFollowing(follower, following)) {
				Follow follow = new Follow();
				follow.setFollower(follower);
				follow.setFollowing(following);
				followRepo.save(follow);
				return true;
			}
		}
		return false;
	}

	public boolean unfollowUser(String currentUserEmail, Long targetUserId) {
		User follower = userRepo.findByEmail(currentUserEmail).orElse(null);
		User following = userRepo.findById(targetUserId).orElse(null);
		if (follower != null && following != null) {
			followRepo.deleteByFollowerAndFollowing(follower, following);
			return true;
		}
		return false;
	}

	public long countFollowers(User user) {
		return followRepo.countByFollowing(user);
	}

	public long countFollowings(User user) {
		return followRepo.countByFollower(user);
	}

	public boolean isFollowing(User follower, User following) {
		return followRepo.existsByFollowerAndFollowing(follower, following);
	}
}
