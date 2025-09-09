package com.friendbook.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.friendbook.controller.PageController;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.impl.CustomUserDetailsServiceImpl;
import com.friendbook.utility.CaptchaUtility;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(controllers = PageController.class)
@TestPropertySource(properties = {"google.recaptcha.secret=test-secret","google.recaptcha.site=test-site"})
public class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomUserDetailsServiceImpl uds;

	@MockBean
	private PasswordEncoder passwordEncoder;
	
	@MockBean
    private CaptchaUtility captchaUtility;

	@Test
	void testPublicEndPointsAccessible() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
		mockMvc.perform(get("/about")).andExpect(status().isOk());
		mockMvc.perform(get("/signup")).andExpect(status().isOk());
		mockMvc.perform(get("/login")).andExpect(status().isOk());
	}
	
}
