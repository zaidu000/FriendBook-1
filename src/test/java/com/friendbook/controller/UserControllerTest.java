package com.friendbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendbook.config.TestSecurityConfig;
import com.friendbook.dto.UserDTO;
import com.friendbook.model.User;
import com.friendbook.service.impl.UserServiceImpl;
import com.friendbook.utility.CaptchaUtility;

@WebMvcTest(UserRestController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserServiceImpl userService;

	@MockBean
	private CaptchaUtility captchaUtility;

	@MockBean
	private ModelMapper modelMapper;

	private ObjectMapper objectMapper;
	private UserDTO userDTO;
	private User user;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		userDTO = new UserDTO();
		userDTO.setEmail("test@example.com");
		userDTO.setPassword("password");
		userDTO.setCaptchaToken("dummyCaptcha");

		user = new User();
		user.setEmail("test@example.com");
		user.setFullName("Test user");
		user.setPassword("password");
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testSignupSuccess() throws Exception {
		when(captchaUtility.verifyCaptcha("dummyCaptcha")).thenReturn(true);
		when(modelMapper.map(any(UserDTO.class), any())).thenReturn(user);
		when(userService.registerUser(any(User.class))).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Signup successful!"));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testSignupCaptchaFailed() throws Exception {
		when(captchaUtility.verifyCaptcha("dummyCaptcha")).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Captcha failed. Please try again."));
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void testSignupEmailExists() throws Exception {
		when(captchaUtility.verifyCaptcha("dummyCaptcha")).thenReturn(true);
		when(modelMapper.map(any(UserDTO.class), any())).thenReturn(user);
		when(userService.registerUser(any(User.class))).thenReturn(false);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("Email exists."));
	}

}
