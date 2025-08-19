package com.friendbook.service;

import java.util.List;
import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;

public interface FriendRequestService {

	String getRequestStatus(User sender, User receiver);

	void sendRequest(User sender, User receiver);

	List<FriendRequest> getPendingRequests(User user);

	void acceptRequest(Long requestId);

	void declineRequest(Long requestId);

	boolean alreadyRequested(User sender, User receiver);
}
