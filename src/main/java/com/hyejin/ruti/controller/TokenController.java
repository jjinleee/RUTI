package com.hyejin.ruti.controller;

import com.hyejin.ruti.service.NotificationService;
import com.hyejin.ruti.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/saveToken")
    public String saveToken(@RequestBody Map<String, String> tokenData, HttpSession session) {
        String token = tokenData.get("token");
        String email = (String) session.getAttribute("loginEmail");

        if (token == null || email == null) {
            return "Token or email is missing";
        }

        // FCM 토큰을 업데이트하는 서비스 호출
        notificationService.updateFcmToken(email, token);
        System.out.println("Received FCM token: " + token);

        return "Token received and saved";
    }
}
