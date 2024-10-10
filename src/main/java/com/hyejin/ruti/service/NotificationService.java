package com.hyejin.ruti.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.repository.RoutineRepository;
import com.hyejin.ruti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public void sendNotification(String targetToken, String title, String body) {
        System.out.println("알림 전송 시도 중: " + title + ", " + body + ", " + targetToken);
        if (targetToken == null || targetToken.isEmpty()) {
            System.out.println("알림 전송 실패: 유효하지 않은 FCM 토큰");
            return;
        }
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

    @Scheduled(cron = "*/30 * * * * *") // 30초마다 실행
    public void checkAndSendRoutineNotifications() {
        System.out.println("스케줄러가 호출되었습니다.");

        LocalDate currentDate = LocalDate.now();
        String currentDay = currentDate.getDayOfWeek().name().toLowerCase();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        List<RoutineEntity> routines = routineRepository.findAll();
        for (RoutineEntity routine : routines) {
            // 현재 요일, 날짜, 시간 조건에 맞는 루틴인지 확인
            if (routine.getActiveDays().toLowerCase().contains(currentDay)
                    && routine.getStartDate().compareTo(currentDate.toString()) <= 0
                    && routine.getEndDate().compareTo(currentDate.toString()) >= 0
                    && routine.getTime().equals(currentTime)) {
                // 알림 전송
                String targetToken = getFcmToken(routine.getUserEmail());
                if (targetToken != null) {
                    sendNotification(targetToken, routine.getTitle(), "루틴 시간입니다!");
                } else {
                    System.out.println("유효하지 않은 FCM 토큰입니다. 알림을 보낼 수 없습니다.");
                }
                System.out.println("현재 시간: " + currentTime);
                System.out.println("루틴 제목: " + routine.getTitle());
                System.out.println("알림 전송 대상 토큰: " + targetToken);
            }
        }
    }
    public void updateFcmToken(String userEmail, String fcmToken) {
        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(userEmail);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setFcmToken(fcmToken); // FCM 토큰 설정
            userRepository.save(user);  // 변경된 엔티티 저장
            System.out.println("FCM 토큰이 업데이트되었습니다: " + fcmToken);
        } else {
            System.out.println("사용자를 찾을 수 없습니다. 이메일: " + userEmail);
        }
    }

    private String getFcmToken(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .map(user -> user.getFcmToken())
                .orElse(null);
    }
}