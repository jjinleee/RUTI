package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.TodoDTO;
import com.hyejin.ruti.entity.CategoryEntity;
import com.hyejin.ruti.entity.TodoEntity;
import com.hyejin.ruti.repository.CategoryRepository;
import com.hyejin.ruti.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<TodoDTO> getTodosByDate(String date) {
        List<TodoEntity> todos = todoRepository.findByDate(date);
        return todos.stream()
                .map(TodoDTO::toTodoDTO)
                .collect(Collectors.toList());
    }

    public TodoDTO saveTodo(TodoDTO todoDTO) {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTodoContent(todoDTO.getTodoContent());
        todoEntity.setDate(todoDTO.getDate());
        todoEntity.setCompleted(todoDTO.isCompleted());
        CategoryEntity categoryEntity = categoryRepository.findById(todoDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        todoEntity.setCategoryId(categoryEntity);
        TodoEntity savedEntity = todoRepository.save(todoEntity);
        return TodoDTO.toTodoDTO(savedEntity);
    }

    public TodoDTO updateTodo(Long todoId, TodoDTO todoDTO) {
        TodoEntity todoEntity = todoRepository.findById(todoId).orElseThrow(() -> new RuntimeException("Todo not found"));
        todoEntity.setCompleted(todoDTO.isCompleted());
        TodoEntity updatedEntity = todoRepository.save(todoEntity);
        return TodoDTO.toTodoDTO(updatedEntity);
    }

    public void deleteTodo(Long todoId) {
        if (todoRepository.existsById(todoId)) {
            todoRepository.deleteById(todoId);
        } else {
            throw new RuntimeException("Todo not found");
        }
    }

}
