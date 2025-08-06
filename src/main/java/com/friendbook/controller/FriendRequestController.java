package com.friendbook.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FriendRequestRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.FriendRequestService;
import com.friendbook.service.UserService;

@Controller
public class FriendRequestController {

	@Autowired
	private FriendRequestService requestService;

	@Autowired
	private UserService userService;

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/friend-request/send/{username}")
	@ResponseBody
	public ResponseEntity<String> sendRequest(@PathVariable String username, Principal principal) {
		try {
			String senderUsername = principal.getName();
			User sender = userRepository.findByEmail(principal.getName())
					.orElseThrow(() -> new RuntimeException("Sender not found"));

			User receiver = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("Receiver not found"));

			requestService.sendRequest(sender, receiver);
			return ResponseEntity.ok("Friend request sent");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending request");
		}
	}

	@PostMapping("/friend-request/accept/{username}")
	@ResponseBody
	public ResponseEntity<String> acceptRequest(@PathVariable String username, Principal principal) {
		try {
			User currentUser = userRepository.findByEmail(principal.getName())
					.orElseThrow(() -> new RuntimeException("Current user not found"));

			User sender = userRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Sender not found"));

			FriendRequest request = friendRequestRepository
					.findBySenderAndReceiverAndStatus(sender, currentUser, "pending")
					.orElseThrow(() -> new RuntimeException("Friend request not found"));

			request.setStatus("accepted");
			request.setAccepted(true);
			friendRequestRepository.save(request);

			currentUser.getFollowers().add(sender);
			sender.getFollowing().add(currentUser);

			userRepository.save(currentUser);
			userRepository.save(sender);

			return ResponseEntity.ok("Request accepted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@PostMapping("/friend-request/decline/{username}")
	@ResponseBody
	public ResponseEntity<String> declineRequest(@PathVariable("username") String username, Principal principal) {
		try {
			User currentUser = userRepository.findByEmail(principal.getName())
					.orElseThrow(() -> new RuntimeException("Current user not found"));

			User sender = userRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Sender not found"));

			FriendRequest request = friendRequestRepository.findBySenderAndReceiver(sender, currentUser)
					.orElseThrow(() -> new RuntimeException("Friend request not found"));

			friendRequestRepository.delete(request);

			return ResponseEntity.ok("Request declined");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@ModelAttribute("pendingRequestCount")
	public int pendingRequestCount(Principal principal) {
		if (principal == null)
			return 0;
		User user = userService.getUserByUsername1(principal.getName()).orElse(null);
		return requestService.getPendingRequests(user).size();
	}

	@ModelAttribute("pendingRequests")
	public List<FriendRequest> pendingRequests(Principal principal) {
		if (principal == null)
			return List.of();
		User user = userService.getUserByUsername1(principal.getName()).orElse(null);
		return requestService.getPendingRequests(user);
	}

	@GetMapping("/notifications")
	public String showNotifications(Model model, Principal principal) {
		User currentUser = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<FriendRequest> pendingRequests = friendRequestRepository.findByReceiverAndStatus(currentUser, "pending");

		model.addAttribute("pendingRequests", pendingRequests);
		return "notifications";
	}

}
