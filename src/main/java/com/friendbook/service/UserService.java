package com.friendbook.service;

import java.util.List;
import java.util.Optional;

import com.friendbook.model.User;

public interface UserService {

	boolean registerUser(User user);

	String generateUsername(String fullName);

	User getUserByUsername(String username);

	List<User> searchUsersByKeyword(String keyword);

	Optional<User> getUserByUsername1(String username);

	void save(User user);

	List<User> searchUsersByUsername(String keyword);
}
