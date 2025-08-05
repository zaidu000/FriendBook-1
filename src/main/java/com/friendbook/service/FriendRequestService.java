package com.friendbook.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FriendRequestRepository;

@Service
public class FriendRequestService {

	@Autowired
	private FriendRequestRepository repo;
	
	@Autowired
	private UserService userService;
	
	public String getRequestStatus(User sender, User receiver) {
        if (sender == null || receiver == null) {
            return null;
        }

        Optional<FriendRequest> request = repo.findBySenderAndReceiver(sender, receiver);

        if (request.isPresent()) {
            return request.get().getStatus();
        }

        return null;
    }

	public void sendRequest(User sender, User receiver) {
		if (repo.existsBySenderAndReceiver(sender, receiver)) {
	        throw new RuntimeException("Request already sent");
	    }
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAccepted(false);
        request.setStatus("pending");
        repo.save(request);
    }
	
    public List<FriendRequest> getPendingRequests(User user) {
        return repo.findByReceiverAndAcceptedFalse(user);
    }

    public void acceptRequest(Long requestId) {
        FriendRequest req = repo.findById(requestId).orElseThrow();
        req.setAccepted(true);
        req.getSender().getFollowing().add(req.getReceiver());
        req.getReceiver().getFollowers().add(req.getSender());
        repo.save(req);
    }

    public void declineRequest(Long requestId) {
        repo.deleteById(requestId);
    }

    public boolean alreadyRequested(User sender, User receiver) {
        return repo.findBySenderAndReceiver(sender, receiver).isPresent();
    }
}
