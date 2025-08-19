package com.friendbook.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FriendRequestRepository;
import com.friendbook.service.FriendRequestService;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	@Override
	public String getRequestStatus(User sender, User receiver) {
		if (sender == null || receiver == null) {
			return null;
		}
		Optional<FriendRequest> request = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
		if (request.isPresent()) {
			return request.get().getStatus();
		}
		return null;
	}

	@Override
	public void sendRequest(User sender, User receiver) {
		if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new RuntimeException("Request already sent");
		}
		FriendRequest request = new FriendRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setAccepted(false);
		request.setStatus("pending");
		friendRequestRepository.save(request);
	}

	@Override
	public List<FriendRequest> getPendingRequests(User user) {
		return friendRequestRepository.findByReceiverAndAcceptedFalse(user);
	}

	@Override
	public void acceptRequest(Long requestId) {
		FriendRequest req = friendRequestRepository.findById(requestId).orElseThrow();
		req.setAccepted(true);
		req.getSender().getFollowing().add(req.getReceiver());
		req.getReceiver().getFollowers().add(req.getSender());
		friendRequestRepository.save(req);
	}

	@Override
	public void declineRequest(Long requestId) {
		friendRequestRepository.deleteById(requestId);
	}

	@Override
	public boolean alreadyRequested(User sender, User receiver) {
		return friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent();
	}
}
