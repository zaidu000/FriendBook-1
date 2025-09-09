package com.friendbook.service.impl;


import com.friendbook.exception.UserNotFoundException;
import com.friendbook.model.Follow;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRepository;
import com.friendbook.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FollowServiceImplTest {

	@Mock
	private FollowRepository followRepo;

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private FollowServiceImpl followService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// Follow when it is success
	@Test
	void testFollowUser_Success() {
		User follower = new User();
		follower.setId(1L);
		follower.setEmail("follower@example.com");

		User following = new User();
		following.setId(2L);

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.existsByFollowerAndFollowing(follower, following)).thenReturn(false);

		boolean result = followService.followUser("follower@example.com", 2L);

		assertTrue(result);
		verify(followRepo).save(any(Follow.class));
	}

	// Already following the follow user
	@Test
	void testFollowUser_AlreadyFollowing() {
		User follower = new User();
		User following = new User();

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

		boolean result = followService.followUser("follower@example.com", 2L);

		assertFalse(result);
		verify(followRepo, never()).save(any());
	}

	// Test the follow user when the same user
	@Test
	void testFollowUser_SameUser() {
		User follower = new User();
		follower.setId(1L);
		follower.setEmail("same@example.com");

		when(userRepo.findByEmail("same@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(1L)).thenReturn(Optional.of(follower));

		boolean result = followService.followUser("same@example.com", 1L);

		assertFalse(result);
		verify(followRepo, never()).save(any());
	}

	// Unfollow users with counts successfully
	@Test
	void testUnfollowUserWithCounts_Success() {
		User follower = new User();
		User following = new User();

		Follow follow = new Follow();

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow));
		when(followRepo.countByFollower(follower)).thenReturn(3L);
		when(followRepo.countByFollowing(following)).thenReturn(5L);

		FollowServiceImpl.UnfollowResult result = followService.unfollowUserWithCounts("follower@example.com", 2L);

		assertTrue(result.ok());
		assertEquals(3L, result.followingCount());
		assertEquals(5L, result.targetFollowersCount());
		verify(followRepo).delete(follow);
	}

	// Unfollow user with counts when follow not exists
	@Test
	void testUnfollowUserWithCounts_FollowNotExists() {
		User follower = new User();
		User following = new User();

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
		when(followRepo.countByFollower(follower)).thenReturn(3L);
		when(followRepo.countByFollowing(following)).thenReturn(5L);

		FollowServiceImpl.UnfollowResult result = followService.unfollowUserWithCounts("follower@example.com", 2L);

		assertFalse(result.ok());
		assertEquals(3L, result.followingCount());
		assertEquals(5L, result.targetFollowersCount());
		verify(followRepo, never()).delete(any());
	}

	// Unfollow the user with counts when user is missing
	@Test
	void testUnfollowUserWithCounts_UserMissing() {
		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.empty());

		FollowServiceImpl.UnfollowResult result = followService.unfollowUserWithCounts("follower@example.com", 2L);

		assertFalse(result.ok());
		assertEquals(-1, result.followingCount());
		assertEquals(-1, result.targetFollowersCount());
	}

	// Count the followers
	@Test
	void testCountFollowers() {
		User user = new User();
		when(followRepo.countByFollowing(user)).thenReturn(10L);

		long count = followService.countFollowers(user);

		assertEquals(10L, count);
	}

	// Count the followings
	@Test
	void testCountFollowings() {
		User user = new User();
		when(followRepo.countByFollower(user)).thenReturn(7L);

		long count = followService.countFollowings(user);

		assertEquals(7L, count);
	}

	// Is it following
	@Test
	void testIsFollowing() {
		User follower = new User();
		User following = new User();

		when(followRepo.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

		assertTrue(followService.isFollowing(follower, following));
	}

	// Unfollow successfully
	@Test
	void testUnfollow_Success() {
		User follower = new User();
		follower.setId(1L);
		follower.setEmail("follower@example.com");

		User following = new User();
		following.setId(2L);
		following.setEmail("following@example.com");

		Follow follow = new Follow();

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.of(follow));
		when(followRepo.countByFollower(follower)).thenReturn(2L);
		when(followRepo.countByFollowing(following)).thenReturn(4L);

		FollowServiceImpl.UnfollowResult result = followService.unfollow("follower@example.com", 2L);

		assertTrue(result.ok());
		assertEquals(2L, result.followingCount());
		assertEquals(4L, result.targetFollowersCount());
		verify(followRepo).delete(follow);
	}

	// Unfollow when there is no follow relation
	@Test
	void testUnfollow_NoFollowRelation() {
		User follower = new User();
		follower.setId(1L);
		follower.setEmail("follower@example.com");

		User following = new User();
		following.setId(2L);
		following.setEmail("following@example.com");

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.of(following));
		when(followRepo.findByFollowerAndFollowing(follower, following)).thenReturn(Optional.empty());
		when(followRepo.countByFollower(follower)).thenReturn(2L);
		when(followRepo.countByFollowing(following)).thenReturn(4L);

		FollowServiceImpl.UnfollowResult result = followService.unfollow("follower@example.com", 2L);

		assertTrue(result.ok());
		assertEquals(2L, result.followingCount());
		assertEquals(4L, result.targetFollowersCount());
		verify(followRepo, never()).delete(any());
	}

	// Unfollow when user not found
	@Test
	void testUnfollow_UserNotFound() {
		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			followService.unfollow("follower@example.com", 2L);
		});
	}

	// Unfollow when target user is not found
	@Test
	void testUnfollow_TargetUserNotFound() {
		User follower = new User();
		follower.setEmail("follower@example.com");

		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.of(follower));
		when(userRepo.findById(2L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			followService.unfollow("follower@example.com", 2L);
		});
	}
}
