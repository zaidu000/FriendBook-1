package com.friendbook.utility;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaUtility {

    @Value("${google.recaptcha.secret}")
    private String secret;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyCaptcha(String token) {
        RestTemplate restTemplate = new RestTemplate();
        
        Map<String, String> body = Map.of(
            "secret", secret,
            "response", token
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL + "?secret={secret}&response={response}", null, Map.class, body);
        Map<String, Object> result = response.getBody();

        return (Boolean) result.get("success");
    }
}
