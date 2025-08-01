package com.friendbook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.friendbook.dto.UserDTO;
import com.friendbook.model.User;
import com.friendbook.service.UserService;
import com.friendbook.utility.SignupResponse;

@RestController
@RequestMapping("/api")
public class UserRestController {
    @Autowired 
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody UserDTO dto) {
        User user = new User();
        user.setFullName(dto.fullName);
        user.setEmail(dto.email);
        user.setPassword(dto.password);

        boolean ok = userService.registerUser(user);
        if (ok) return ResponseEntity.ok(new SignupResponse(true, "Signup successful!"));
        else return ResponseEntity.status(HttpStatus.CONFLICT).body(new SignupResponse(false, "Email exists."));
    }
}
