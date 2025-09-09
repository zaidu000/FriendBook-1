package com.friendbook.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;

@DataJpaTest
public class FriendRequestRepositoryTest {

	@Autowired
	private FriendRequestRepository friendRequestRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private User sender;
	private User receiver;
	private FriendRequest request;
	
	@BeforeEach
	void setUp() {
		sender = new User();
		sender.setEmail("sender@example.com");
		sender.setFullName("Sender user");
		sender.setPassword("password");
		sender.setUsername("senderuser");
		userRepository.save(sender);
		
		receiver = new User();
		receiver.setEmail("receiver@example.com");
		receiver.setFullName("receiver user");
		receiver.setPassword("password");
		receiver.setUsername("receiveruser");
		userRepository.save(receiver);
		
		request = new FriendRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setAccepted(false);
		request.setStatus("PENDING");
		friendRequestRepository.save(request);
	}
	
	@Test
	void testExistsBySenderAndReceiver() {
		boolean exists = friendRequestRepository.existsBySenderAndReceiver(sender, receiver);
		assertThat(exists).isTrue();
	}
	
	@Test
	void testFindByReceiverAndAcceptedFalse() {
		List<FriendRequest> requests = friendRequestRepository.findByReceiverAndAcceptedFalse(receiver);
		assertThat(requests).hasSize(1);
		assertThat(requests.get(0).getSender().getEmail()).isEqualTo("sender@example.com");
	}
	
	@Test
	void testFindBySenderAndReceiver() {
		Optional<FriendRequest> opt = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
		assertThat(opt).isPresent();
		assertThat(opt.get().getStatus()).isEqualTo("PENDING");
	}
	
	@Test
	void testFindByReceiverAndStatus() {
		List<FriendRequest> requests = friendRequestRepository.findByReceiverAndStatus(receiver, "PENDING");
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getSender().getUsername()).isEqualTo("sender@example.com");
	}
	
	@Test
    void testFindBySenderAndReceiverAndStatus() {
        Optional<FriendRequest> opt = friendRequestRepository.findBySenderAndReceiverAndStatus(sender, receiver, "PENDING");
        assertThat(opt).isPresent();
        assertThat(opt.get().isAccepted()).isFalse();
    }
}
