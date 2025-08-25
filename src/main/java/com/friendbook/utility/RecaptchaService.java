package com.friendbook.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaService {
	
    @Value("${google.recaptcha.site}")
    private String siteKey;

    @Value("${google.recaptcha.secret}")
    private String secretKey;

}

