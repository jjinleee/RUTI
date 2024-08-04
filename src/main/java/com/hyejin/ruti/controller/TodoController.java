package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.TodoDTO;
import com.hyejin.ruti.service.TodoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    //날짜별 to do
    @GetMapping("/{date}")
    public ResponseEntity<List<TodoDTO>> getTodosByDate(@PathVariable String date, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<TodoDTO> todos = todoService.getTodosByDate(date, userEmail);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    //카테고리별 to do 추가
    @PostMapping("/{categoryId}")
    public ResponseEntity<TodoDTO> addTodo(@PathVariable String categoryId, @RequestBody TodoDTO todoDTO, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            todoDTO.setCategoryId(Long.valueOf(categoryId));
            TodoDTO savedTodo = todoService.saveTodo(todoDTO, userEmail);
            return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //to do 업데이트 - completed 업데이트 시 사용
    @PutMapping("/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long todoId, @RequestBody TodoDTO todoDTO, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            TodoDTO updatedTodo = todoService.updateTodo(todoId, todoDTO, userEmail);
            return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //to do 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            todoService.deleteTodo(id, userEmail);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
