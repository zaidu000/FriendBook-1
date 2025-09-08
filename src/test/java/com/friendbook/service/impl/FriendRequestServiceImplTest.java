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

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

    @Test
    void testGetRequestStatus_RequestNotExists() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.empty());

        String status = friendRequestService.getRequestStatus(sender, receiver);

        assertNull(status);
    }

    @Test
    void testGetRequestStatus_NullUsers() {
        assertNull(friendRequestService.getRequestStatus(null, new User()));
        assertNull(friendRequestService.getRequestStatus(new User(), null));
    }

    @Test
    void testSendRequest_Success() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.existsBySenderAndReceiver(sender, receiver))
                .thenReturn(false);

        friendRequestService.sendRequest(sender, receiver);

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

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

    @Test
    void testGetPendingRequests() {
        User receiver = new User();
        List<FriendRequest> requests = Arrays.asList(new FriendRequest(), new FriendRequest());

        when(friendRequestRepository.findByReceiverAndAcceptedFalse(receiver))
                .thenReturn(requests);

        List<FriendRequest> result = friendRequestService.getPendingRequests(receiver);

        assertEquals(2, result.size());
    }

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

    @Test
    void testDeclineRequest() {
        friendRequestService.declineRequest(1L);
        verify(friendRequestRepository).deleteById(1L);
    }

    @Test
    void testAlreadyRequested_WhenExists() {
        User sender = new User();
        User receiver = new User();
        FriendRequest request = new FriendRequest();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(request));

        assertTrue(friendRequestService.alreadyRequested(sender, receiver));
    }

    @Test
    void testAlreadyRequested_WhenNotExists() {
        User sender = new User();
        User receiver = new User();

        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.empty());

        assertFalse(friendRequestService.alreadyRequested(sender, receiver));
    }
}
