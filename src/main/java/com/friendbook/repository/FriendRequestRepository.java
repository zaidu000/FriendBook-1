package com.friendbook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
	
	boolean existsBySenderAndReceiver(User sender, User receiver);
	List<FriendRequest> findByReceiverAndAcceptedFalse(User receiver);
	Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
	
	List<FriendRequest> findByReceiverAndStatus(User receiver, String status);

	Optional<FriendRequest> findBySenderAndReceiverAndStatus(User sender, User receiver, String status);

}
