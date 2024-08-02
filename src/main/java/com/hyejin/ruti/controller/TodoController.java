package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.TodoDTO;
import com.hyejin.ruti.service.TodoService;
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
    public ResponseEntity<List<TodoDTO>> getTodosByDate(@PathVariable String date) {
        List<TodoDTO> todos = todoService.getTodosByDate(date);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    //카테고리별 to do 추가
    @PostMapping("/{categoryId}")
    public ResponseEntity<TodoDTO> addTodo(@PathVariable String categoryId, @RequestBody TodoDTO todoDTO) {
        try {
            todoDTO.setCategoryId(Long.valueOf(categoryId));
            TodoDTO savedTodo = todoService.saveTodo(todoDTO);
            return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //to do 업데이트 - completed 업데이트 시 사용
    @PutMapping("/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long todoId, @RequestBody TodoDTO todoDTO) {
        try {
            TodoDTO updatedTodo = todoService.updateTodo(todoId, todoDTO);
            return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //to do 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        try {
            todoService.deleteTodo(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
