package com.hyejin.ruti.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {

    @PostMapping("/saveToken")
    public String saveToken(@RequestBody Map<String, String> tokenData) {
        String token = tokenData.get("token");
        // FCM 토큰을 DB에 저장하는 로직 구현
        System.out.println("Received FCM token: " + token);
        return "Token received and saved";
    }
}
