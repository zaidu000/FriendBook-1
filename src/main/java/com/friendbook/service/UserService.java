package com.friendbook.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.friendbook.model.User;
import com.friendbook.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	private PasswordEncoder encoder = new BCryptPasswordEncoder();

	public boolean registerUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			return false;
		}
		user.setUsername(generateUsername(user.getFullName()));
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return true;
	}

	public String generateUsername(String fullName) {
		String part = fullName.replaceAll("[^A-Za-z]", "").substring(0, Math.min(5, fullName.length()));
		part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
		int rand = new Random().nextInt(900) + 100;
		return part + rand;
	}

	public User getUserByUsername(String username) {
		return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public List<User> searchUsersByKeyword(String keyword) {
		return userRepository.searchByUsername(keyword);
	}

	public Optional<User> getUserByUsername1(String username) {
		return userRepository.findByUsername(username);
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
	public List<User> searchUsersByUsername(String keyword) {
	    return userRepository.findByUsernameContainingIgnoreCase(keyword);
	}

}
