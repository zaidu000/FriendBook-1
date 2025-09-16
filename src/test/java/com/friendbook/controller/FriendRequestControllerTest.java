package com.friendbook.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.friendbook.config.TestSecurityConfig;
import com.friendbook.model.FriendRequest;
import com.friendbook.model.User;
import com.friendbook.repository.FriendRequestRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.impl.FriendRequestServiceImpl;
import com.friendbook.service.impl.UserServiceImpl;

@WebMvcTest(FriendRequestController.class)
@Import(TestSecurityConfig.class)
public class FriendRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendRequestServiceImpl requestService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private FriendRequestRepository friendRequestRepository;

    @MockBean
    private UserRepository userRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        sender.setUsername("sender");
        sender.setFullName("Sender Name");

        receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@example.com");
        receiver.setUsername("receiver");
        receiver.setFullName("Receiver name");
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void testSendRequest() throws Exception {
        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
        when(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver));

        mockMvc.perform(MockMvcRequestBuilders.post("/friend-request/send/receiver"))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request sent"));

        verify(requestService, times(1)).sendRequest(sender, receiver);
    }

//    @Test
//    @WithMockUser(username = "receiver@example.com")
//    void testAcceptRequest() throws Exception {
//        // Logged-in user (receiver)
//        when(userService.getUserByUsername1("receiver@example.com")).thenReturn(Optional.of(receiver));
//        // Sender from URL
//        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
//
//        FriendRequest request = new FriendRequest();
//        request.setId(1L);
//        request.setSender(sender);
//        request.setReceiver(receiver);
//        request.setStatus("pending");
//        request.setAccepted(false);
//
//        when(friendRequestRepository.findBySenderAndReceiverAndStatus(sender, receiver, "pending"))
//            .thenReturn(Optional.of(request));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/friend-request/accept/sender"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.message").value("Request accepted"))
//            .andExpect(jsonPath("$.showFollowBack").value(true))
//            .andExpect(jsonPath("$.senderId").value(1L))
//            .andExpect(jsonPath("$.senderUsername").value("sender"));
//
//        verify(friendRequestRepository, times(1)).save(request);
//        verify(userRepository, times(1)).save(sender);
//        verify(userRepository, times(1)).save(receiver);
//    }
//
//
//    @Test
//    @WithMockUser(username = "receiver@example.com")
//    void testDeclineRequest() throws Exception {
//        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
//        when(userRepository.findByEmail("sender@example.com")).thenReturn(Optional.of(sender));
//
//        FriendRequest request = new FriendRequest();
//        request.setId(1L);
//        request.setSender(sender);
//        request.setReceiver(receiver);
//
//        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.of(request));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/friend-request/decline/sender"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Request declined"));
//
//        verify(friendRequestRepository, times(1)).delete(request);
//    }

    @Test
    @WithMockUser(username = "receiver@example.com")
    void testShowNotifications() throws Exception {
        when(userRepository.findByEmail("receiver@example.com")).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findByReceiverAndStatus(receiver, "pending")).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attributeExists("pendingRequests"));
    }

//    @Test
//    @WithMockUser(username = "sender")
//    void testFollowBack() throws Exception {
//        // Mock sender via userService (principal)
//        when(userService.getUserByUsername1("sender")).thenReturn(Optional.of(sender));
//        // Mock receiver via username (URL path variable)
//        when(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/friend-request/follow-back/receiver"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Followed back successfully"))
//                .andExpect(jsonPath("$.senderId").value(receiver.getId()))
//                .andExpect(jsonPath("$.senderUsername").value(receiver.getUsername()));
//
//        verify(userRepository, times(1)).save(sender);
//        verify(userRepository, times(1)).save(receiver);
//    }

//    @Test
//    @WithMockUser(username = "receiver@example.com")
//    void testPendingRequestCount() throws Exception {
//        when(userService.getUserByUsername1("receiver@example.com")).thenReturn(Optional.of(receiver));
//        when(requestService.getPendingRequests(receiver)).thenReturn(List.of(new FriendRequest()));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "receiver@example.com")
//    void testPendingRequests() throws Exception {
//        when(userService.getUserByUsername1("receiver@example.com")).thenReturn(Optional.of(receiver));
//        when(requestService.getPendingRequests(receiver)).thenReturn(List.of(new FriendRequest()));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/notifications"))
//                .andExpect(status().isOk());
//    }
}
