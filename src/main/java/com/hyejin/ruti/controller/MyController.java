package com.hyejin.ruti.controller;

import com.hyejin.ruti.service.TodoService;
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

    @GetMapping("/todo")
    public ResponseEntity<Map<String, Object>> getTodoStats(HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> stats = todoService.getTodoStatistics(userEmail);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

}
