package com.friendbook.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FriendRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

class FriendRequestServiceImplTest {

	// Create a mock instance of class
	// It does not call real methods unless we explicitly tell it
    @Mock
    private FriendRequestRepository friendRequestRepository;

    // Create an instance of class under test and inject the mocks annotated with @Mock
    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Get request status when request exists
    @Test
    void testGetRequestStatus_RequestExists() {
        User sender = new User();
        User receiver = new User();
        FriendRequest request = new FriendRequest();
        request.setStatus("pending");

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(request));

        String status = friendRequestService.getRequestStatus(sender, receiver);

        assertEquals("pending", status);
    }

    // Get request status when request does not exists
    @Test
    void testGetRequestStatus_RequestNotExists() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.empty());

        String status = friendRequestService.getRequestStatus(sender, receiver);

        assertNull(status);
    }

    // Get request status when users are null
    @Test
    void testGetRequestStatus_NullUsers() {
        assertNull(friendRequestService.getRequestStatus(null, new User()));
        assertNull(friendRequestService.getRequestStatus(new User(), null));
    }

    // Send request successfully
    @Test
    void testSendRequest_Success() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver))
                .thenReturn(false);

        friendRequestService.sendRequest(sender, receiver);

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    // Send request that is already sent
    @Test
    void testSendRequest_AlreadySent() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver))
                .thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            friendRequestService.sendRequest(sender, receiver);
        });

        assertEquals("Request already sent", exception.getMessage());
    }

    // Get all pending requests
    @Test
    void testGetPendingRequests() {
        User receiver = new User();
        List<FriendRequest> requests = Arrays.asList(new FriendRequest(), new FriendRequest());

        when(friendRequestRepository.findByReceiverAndAcceptedFalse(receiver))
                .thenReturn(requests);

        List<FriendRequest> result = friendRequestService.getPendingRequests(receiver);

        assertEquals(2, result.size());
    }

    // Accept requests
    @Test
    void testAcceptRequest() {
        User sender = new User();
        User receiver = new User();
        sender.setFollowing(new HashSet<>());
        receiver.setFollowers(new HashSet<>());

        FriendRequest request = new FriendRequest();
        request.setId(1L);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAccepted(false);

        when(friendRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        friendRequestService.acceptRequest(1L);

        assertTrue(request.isAccepted());
        assertTrue(sender.getFollowing().contains(receiver));
        assertTrue(receiver.getFollowers().contains(sender));

        verify(friendRequestRepository).save(request);
    }

    // Decline request
    @Test
    void testDeclineRequest() {
        friendRequestService.declineRequest(1L);
        verify(friendRequestRepository).deleteById(1L);
    }

    // Already requested when exists 
    @Test
    void testAlreadyRequested_WhenExists() {
        User sender = new User();
        User receiver = new User();
        FriendRequest request = new FriendRequest();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(request));

        assertTrue(friendRequestService.alreadyRequested(sender, receiver));
    }

    // Already requested when not exists
    @Test
    void testAlreadyRequested_WhenNotExists() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.empty());

        assertFalse(friendRequestService.alreadyRequested(sender, receiver));
    }
}
