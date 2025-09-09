package com.friendbook.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = { "google.recaptcha.secret=dummy-secret", "google.recaptcha.site=dummy-site" })
@Import(com.friendbook.configuration.SecurityConfig.class)
public class PasswordEncoderTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void testPasswordEncoderIsBCrypt() {
		assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
	}
}
