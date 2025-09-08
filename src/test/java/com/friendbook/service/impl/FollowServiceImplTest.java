package com.friendbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.exception.UserNotFoundException;
import com.friendbook.model.Follow;
import com.friendbook.model.User;
import com.friendbook.repository.FollowRepository;
import com.friendbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

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

	@Test
	void testUnfollowUserWithCounts_UserMissing() {
		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.empty());

		FollowServiceImpl.UnfollowResult result = followService.unfollowUserWithCounts("follower@example.com", 2L);

		assertFalse(result.ok());
		assertEquals(-1, result.followingCount());
		assertEquals(-1, result.targetFollowersCount());
	}

	@Test
	void testCountFollowers() {
		User user = new User();
		when(followRepo.countByFollowing(user)).thenReturn(10L);

		long count = followService.countFollowers(user);

		assertEquals(10L, count);
	}

	@Test
	void testCountFollowings() {
		User user = new User();
		when(followRepo.countByFollower(user)).thenReturn(7L);

		long count = followService.countFollowings(user);

		assertEquals(7L, count);
	}

	@Test
	void testIsFollowing() {
		User follower = new User();
		User following = new User();

		when(followRepo.existsByFollowerAndFollowing(follower, following)).thenReturn(true);

		assertTrue(followService.isFollowing(follower, following));
	}

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

	@Test
	void testUnfollow_UserNotFound() {
		when(userRepo.findByEmail("follower@example.com")).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> {
			followService.unfollow("follower@example.com", 2L);
		});
	}

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
