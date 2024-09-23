package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.UserBadgeDTO;
import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.service.TodoService;
import com.hyejin.ruti.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/my")
public class MyController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @GetMapping("/todo")
    public ResponseEntity<Map<String, Object>> getTodoStats(HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> stats = todoService.getTodoStatistics(userEmail);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/todo/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyTodoStats(@PathVariable int year, @PathVariable int month, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> stats = todoService.getMonthlyTodoStatistics(userEmail, year, month);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/badge")
    public ResponseEntity<UserBadgeDTO> getUserBadgeInfo(HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 이메일로 사용자 정보를 가져옴
        UserDTO userDTO = userService.getUserByEmail(userEmail);
        if (userDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 유저가 존재하지 않을 경우 404 Not Found 반환
        }

        // 사용자 ID로 배지 정보를 가져옴
        UserBadgeDTO badgeInfo = userService.getUserBadgeInfo(userDTO.getId());
        return ResponseEntity.ok(badgeInfo); // 배지 정보 반환
    }

}
