package com.friendbook.service;

import com.friendbook.model.User;

public interface FollowService {

    boolean followUser(String currentUserEmail, Long targetUserId);

    long countFollowers(User user);

    long countFollowings(User user);

    boolean isFollowing(User follower, User following);
}

