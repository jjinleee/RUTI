package com.hyejin.ruti.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(String targetToken, String title, String body) {
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 9시에 알림 발송
    public void scheduleRoutineNotifications() {
        String targetToken = getUserToken(); // DB에서 사용자의 FCM 토큰을 가져오는 로직 구현
        if (targetToken != null) {
            sendNotification(targetToken, "루틴 알림", "루틴 시간이 되었습니다!");
        }
    }

    private String getUserToken() {
        // 사용자 토큰을 DB에서 가져오는 로직
        return "user-fcm-token";
    }
}