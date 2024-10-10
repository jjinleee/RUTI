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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private UserRepository userRepository;

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

    @Scheduled(cron = "0 * * * * *") // 1분마다 실행
    public void checkAndSendRoutineNotifications() {
        System.out.println("스케줄러가 호출되었습니다.");

        LocalDate currentDate = LocalDate.now();
        String currentDay = currentDate.getDayOfWeek().name().substring(0, 3).toLowerCase();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        List<RoutineEntity> routines = routineRepository.findAll();
        for (RoutineEntity routine : routines) {
            System.out.println("루틴 데이터 확인 - 제목: " + routine.getTitle() + ", 요일: " + routine.getActiveDays() + ", 시작 날짜: " + routine.getStartDate() + ", 종료 날짜: " + routine.getEndDate() + ", 설정된 시간: " + routine.getTime());

            // 요일 조건 확인
            if (!routine.getActiveDays().toLowerCase().contains(currentDay)) {
                System.out.println("요일 조건 불일치: " + currentDay + "는 활성화된 요일이 아닙니다.");
                continue;
            }

            // 날짜 조건 확인
            if (routine.getStartDate().compareTo(currentDate.toString()) > 0 || routine.getEndDate().compareTo(currentDate.toString()) < 0) {
                System.out.println("날짜 조건 불일치: 현재 날짜는 루틴 기간 내에 포함되지 않습니다.");
                continue;
            }

            // 시간 조건 확인
            if (!isTimeMatch(routine.getTime(), currentTime)) {
                System.out.println("시간 조건 불일치: 현재 시간 " + currentTime + "은 루틴 시간 " + routine.getTime() + "과 일치하지 않습니다.");
                continue;
            }

            // 알림 전송
            String targetToken = getFcmToken(routine.getUserEmail());
            if (targetToken != null) {
                sendNotification(targetToken, routine.getTitle(), "루틴 시간입니다!");
            } else {
                System.out.println("유효하지 않은 FCM 토큰입니다. 알림을 보낼 수 없습니다.");
            }
        }
    }

    private boolean isTimeMatch(String routineTime, String currentTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime routineLocalTime = LocalTime.parse(routineTime, formatter);
        LocalTime currentLocalTime = LocalTime.parse(currentTime, formatter);

        long differenceInMinutes = Math.abs(ChronoUnit.MINUTES.between(routineLocalTime, currentLocalTime));
        return differenceInMinutes <= 1;
    }

    private String getFcmToken(String userEmail) {
        Optional<UserEntity> userOptional = userRepository.findByUserEmail(userEmail);
        return userOptional.map(UserEntity::getFcmToken).orElse(null);
    }
}