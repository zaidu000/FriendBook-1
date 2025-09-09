package com.friendbook.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.Follow;
import com.friendbook.model.User;

@DataJpaTest
public class FollowRepositoryTest {

	@Autowired
	private FollowRepository followRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User follower;
	private User following;
	private 	Follow follow;
	
	@BeforeEach
	void setUp() {
		follower = new User();
		follower.setUsername("followerUser");
		follower.setFullName("Follower User");
		follower.setEmail("follower@example.com");
		follower.setPassword("password");
		userRepository.save(follower);
		
		following = new User();
		following.setUsername("followingUser");
		following.setFullName("Following User");
		following.setEmail("following@example.com");
		following.setPassword("password");
		userRepository.save(following);
		
		follow = new Follow();
		follow.setFollower(follower);
		follow.setFollowing(following);
		followRepository.save(follow);
	}
	
	@Test
	void testExistsByFollowerAndFollowing() {
		boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);
		assertThat(exists).isTrue();
	}
	
	@Test
	void testDeleteByFollowerAndFollowing() {
		followRepository.deleteByFollowerAndFollowing(follower, following);
		boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);
		assertThat(exists).isFalse();
	}
	
	@Test
	void testFindByFollowing() {
		List<Follow> follows = followRepository.findByFollowing(following);
		assertThat(follows).hasSize(1);
		assertThat(follows.get(0).getFollower().getUsernameField()).isEqualTo("followerUser");
	}
	
	@Test
	void testFindByFollower() {
		List<Follow> follows = followRepository.findByFollower(follower);
		assertThat(follows).hasSize(1);
		assertThat(follows.get(0).getFollowing().getUsernameField()).isEqualTo("followingUser");
	}
	
	@Test
	void testCountByFollowers() {
		long count = followRepository.countByFollower(follower);
		assertThat(count).isEqualTo(1);
	}
	
	@Test
	void testCountByFollowings() {
		long count = followRepository.countByFollowing(following);
		assertThat(count).isEqualTo(1);
	}
	
	@Test
	void testFindByFollowerAndFollowing() {
		Optional<Follow> optionalFollow = followRepository.findByFollowerAndFollowing(follower, following);
		assertThat(optionalFollow).isPresent();
		assertThat(optionalFollow.get().getFollower().getUsernameField()).isEqualTo("followerUser");
	}
}
