package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.RoutineDTO;
import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.service.RoutineService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    @Autowired
    private final RoutineService routineService;

    @GetMapping("/{userEmail}")
    public ResponseEntity<List<RoutineEntity>> getRoutines(@PathVariable String userEmail) {
        List<RoutineEntity> routines = routineService.getRoutines(userEmail);
        return ResponseEntity.ok(routines);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoutine(@RequestBody RoutineDTO routineDTO, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        RoutineEntity routineEntity = routineService.addRoutine(routineDTO, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(routineEntity);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRoutine(@PathVariable Long id, @RequestBody RoutineDTO routineDTO) {
        boolean isUpdated = routineService.updateRoutine(id, routineDTO);
        if (isUpdated) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoutine(@PathVariable Long id) {
        boolean isDeleted = routineService.deleteRoutine(id);
        if (isDeleted) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }
    }
}