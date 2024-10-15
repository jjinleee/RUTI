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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private UserRepository userRepository;

    // 로그인된 사용자의 이메일을 메모리에 저장하는 전역 리스트
    private Set<String> loggedInUserEmails = new HashSet<>();

    // 로그인할 때 호출하는 메소드 (예시로 구현)
    public void addLoggedInUser(String userEmail) {
        loggedInUserEmails.add(userEmail);
    }

    // 로그아웃할 때 호출하는 메소드 (예시로 구현)
    public void removeLoggedInUser(String userEmail) {
        loggedInUserEmails.remove(userEmail);
    }

    private String convertToEnglishDay(String day) {
        switch (day) {
            case "일":
                return "sunday";
            case "월":
                return "monday";
            case "화":
                return "tuesday";
            case "수":
                return "wednesday";
            case "목":
                return "thursday";
            case "금":
                return "friday";
            case "토":
                return "saturday";
            default:
                return day.toLowerCase();
        }
    }


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

    // 스케줄러 메소드에서 메모리에 저장된 로그인된 사용자들의 루틴만 검사
    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void checkAndSendRoutineNotifications() {
        // 현재 메모리에 저장된 로그인된 사용자의 이메일 리스트를 순회
        for (String userEmail : loggedInUserEmails) {
            List<RoutineEntity> userRoutines = routineRepository.findByUserEmail(userEmail);
            for (RoutineEntity routine : userRoutines) {
                sendNotificationIfRoutineMatches(routine, userEmail);
            }
        }
    }

    // 각 루틴에 대해 알림을 보내는 로직을 분리
    private void sendNotificationIfRoutineMatches(RoutineEntity routine, String userEmail) {
        LocalDate currentDate = LocalDate.now();
        String currentDay = currentDate.getDayOfWeek().name().toLowerCase();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 루틴에서 설정한 요일을 영어 형식으로 변환하고 소문자로 변환
        List<String> activeDaysInEnglish = Arrays.stream(routine.getActiveDays().split(", "))
                .map(String::trim) // 공백 제거
                .map(this::convertToEnglishDay) // 요일을 영어 형식으로 변환
                .collect(Collectors.toList());

        // 현재 요일, 날짜, 시간 조건에 맞는 루틴인지 확인
        if (activeDaysInEnglish.contains(currentDay)
                && routine.getStartDate().compareTo(currentDate.toString()) <= 0
                && routine.getEndDate().compareTo(currentDate.toString()) >= 0
                && routine.getTime().equals(currentTime)) {
            // 알림 전송
            String targetToken = getFcmToken(userEmail);
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