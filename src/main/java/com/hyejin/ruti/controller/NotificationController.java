//test
package com.hyejin.ruti.controller;

import com.hyejin.ruti.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/sendNotification")
    public String sendNotification(@RequestBody Map<String, String> requestData) {
        String token = requestData.get("token");
        String title = requestData.get("title");
        String body = requestData.get("body");

        notificationService.sendNotification(token, title, body);
        return "Notification sent successfully!";
    }
}
