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
import java.time.ZoneId;
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
    @Scheduled(cron = "0 * * * * *", zone="UTC") // 매 분마다 실행
    public void checkAndSendRoutineNotifications() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("UTC")); // 현재 UTC 날짜
        LocalTime currentTime = LocalTime.now(ZoneId.of("UTC")); // 현재 UTC 시간
        String currentTimeString = currentTime.format(DateTimeFormatter.ofPattern("HH:mm"));

//        System.out.println("==== 스케줄러 실행 시작 ====");
//        System.out.println("현재 날짜: " + currentDate);
//        System.out.println("현재 시간: " + currentTimeString);

        // 현재 메모리에 저장된 로그인된 사용자의 이메일 리스트를 순회
        for (String userEmail : loggedInUserEmails) {
            List<RoutineEntity> userRoutines = routineRepository.findByUserEmail(userEmail);
            for (RoutineEntity routine : userRoutines) {
                sendNotificationIfRoutineMatches(routine, userEmail, currentDate, currentTimeString);
            }
        }

    }

    // 각 루틴에 대해 알림을 보내는 로직을 분리
    private void sendNotificationIfRoutineMatches(RoutineEntity routine, String userEmail, LocalDate currentDate, String currentTimeString) {
        String currentDay = currentDate.getDayOfWeek().name().toLowerCase();

        // 루틴 시간(KST)을 UTC로 변환
        String routineTimeUtc = convertToUtcTime(routine.getTime());
//        System.out.println("==== 루틴 조건 검사 ====");
//        System.out.println("현재 요일 (UTC): " + currentDay);
//        System.out.println("현재 시간 (UTC): " + currentTimeString);
//        System.out.println("루틴 시작 날짜: " + routine.getStartDate());
//        System.out.println("루틴 종료 날짜: " + routine.getEndDate());
//        System.out.println("루틴 시간 (KST): " + routine.getTime());
//        System.out.println("루틴 시간 (UTC 변환): " + routineTimeUtc);

        List<String> activeDaysInEnglish = Arrays.stream(routine.getActiveDays().split(", "))
                .map(String::trim)
                .map(this::convertToEnglishDay)
                .collect(Collectors.toList());

        if (activeDaysInEnglish.contains(currentDay)
                && routine.getStartDate().compareTo(currentDate.toString()) <= 0
                && routine.getEndDate().compareTo(currentDate.toString()) >= 0
                && routineTimeUtc.equals(currentTimeString)) {

            String targetToken = getFcmToken(userEmail);
            if (targetToken != null) {
                sendNotification(targetToken, routine.getTitle(), "루틴 시간입니다!");
                System.out.println("알림 전송 성공: " + routine.getTitle());
            } else {
                System.out.println("유효하지 않은 FCM 토큰입니다.");
            }
        }
    }

    // KST -> UTC 변환 메서드
    private String convertToUtcTime(String kstTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTimeKst = LocalTime.parse(kstTime, formatter);
        LocalTime localTimeUtc = localTimeKst.minusHours(9); // KST -> UTC
        return localTimeUtc.format(formatter);
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