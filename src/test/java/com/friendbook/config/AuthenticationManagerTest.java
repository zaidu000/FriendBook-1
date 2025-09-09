package com.friendbook.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootTest(properties = { "google.recaptcha.secret=dummy-secret", "google.recaptcha.site=dummy-site" })
public class AuthenticationManagerTest {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Test
	void testAuthenticationManagerIsNotNull() {
		assertThat(authenticationManager).isNotNull();
	}
}
