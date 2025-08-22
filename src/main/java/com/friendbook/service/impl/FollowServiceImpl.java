package com.friendbook.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.friendbook.exception.UserNotFoundException;
import com.friendbook.model.Follow;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.FollowService;

@Service
public class FollowServiceImpl implements FollowService {

	public static record UnfollowResult(boolean ok, long followingCount, long targetFollowersCount) {
	}

	@Autowired
	private FollowRepository followRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
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

	public UnfollowResult unfollowUserWithCounts(String currentUserEmail, Long targetUserId) {
		User follower = userRepo.findByEmail(currentUserEmail).orElse(null);
		User following = userRepo.findById(targetUserId).orElse(null);

		if (follower == null || following == null) {
			return new UnfollowResult(false, -1, -1);
		}

		// If your FollowRepository exposes Optional<Follow>
		// findByFollowerAndFollowing(...)
		Optional<Follow> existing = followRepo.findByFollowerAndFollowing(follower, following);
		if (existing.isPresent()) {
			followRepo.delete(existing.get());
		} else {
			// nothing to delete
			return new UnfollowResult(false, countFollowings(follower), countFollowers(following));
		}

		long newFollowingCount = countFollowings(follower);
		long newTargetFollowersCount = countFollowers(following);
		return new UnfollowResult(true, newFollowingCount, newTargetFollowersCount);
	}

	@Override
	public long countFollowers(User user) {
		return followRepo.countByFollowing(user);
	}

	@Override
	public long countFollowings(User user) {
		return followRepo.countByFollower(user);
	}

	@Override
	public boolean isFollowing(User follower, User following) {
		return followRepo.existsByFollowerAndFollowing(follower, following);
	}

	@Transactional
	public UnfollowResult unfollow(String currentUsername, Long targetId) {
	    System.out.println("=== UNFOLLOW DEBUG START ===");
	    System.out.println("currentUser = " + currentUsername + ", targetUserId = " + targetId);

	    User follower = userRepo.findByEmail(currentUsername)
	            .orElseThrow(() -> new UserNotFoundException("Current user not found"));
	    User following = userRepo.findById(targetId)
	            .orElseThrow(() -> new UserNotFoundException("Target user not found"));

	    System.out.println("Follower user found: " + follower.getId() + " (" + follower.getEmail() + ")");
	    System.out.println("Target user found: " + following.getId() + " (" + following.getEmail() + ")");

	    // Check if a follow relationship exists
	    Optional<Follow> followOpt = followRepo.findByFollowerAndFollowing(follower, following);
	    System.out.println("Follow relation exists? " + followOpt.isPresent());

	    if (followOpt.isPresent()) {
	        followRepo.delete(followOpt.get());
	        long followingCount = followRepo.countByFollower(follower);
	        long targetFollowersCount = followRepo.countByFollowing(following);

	        System.out.println("Follow deleted. Updated counts: followingCount=" + followingCount + ", targetFollowersCount=" + targetFollowersCount);
	        System.out.println("=== UNFOLLOW DEBUG END ===");
	        return new UnfollowResult(true, followingCount, targetFollowersCount);
	    }

	    System.out.println("⚠️ No follow relation found → treating as success (idempotent unfollow)");
	    long followingCount = followRepo.countByFollower(follower);
	    long targetFollowersCount = followRepo.countByFollowing(following);

	    System.out.println("=== UNFOLLOW DEBUG END ===");
	    return new UnfollowResult(true, followingCount, targetFollowersCount);
	}

}
