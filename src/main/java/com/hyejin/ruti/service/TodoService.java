package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.TodoDTO;
import com.hyejin.ruti.entity.CategoryEntity;
import com.hyejin.ruti.entity.TodoEntity;
import com.hyejin.ruti.repository.CategoryRepository;
import com.hyejin.ruti.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<TodoDTO> getTodosByDate(String date, String userEmail) {
        List<TodoEntity> todos = todoRepository.findByDateAndUserEmail(date, userEmail);
        return todos.stream()
                .map(TodoDTO::toTodoDTO)
                .collect(Collectors.toList());
    }

    public TodoDTO saveTodo(TodoDTO todoDTO, String userEmail) {
        CategoryEntity categoryEntity = categoryRepository.findById(todoDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        TodoEntity todoEntity = TodoEntity.toTodoEntity(todoDTO, categoryEntity, userEmail);
        TodoEntity savedEntity = todoRepository.save(todoEntity);
        return TodoDTO.toTodoDTO(savedEntity);
    }

    public TodoDTO updateTodo(Long todoId, TodoDTO todoDTO, String userEmail) {
        TodoEntity todoEntity = todoRepository.findById(todoId)
                .filter(todo -> todo.getUserEmail().equals(userEmail))
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoEntity.setCompleted(todoDTO.isCompleted());
        TodoEntity updatedEntity = todoRepository.save(todoEntity);
        return TodoDTO.toTodoDTO(updatedEntity);
    }

    public void deleteTodo(Long todoId, String userEmail) {
        TodoEntity todoEntity = todoRepository.findById(todoId)
                .filter(todo -> todo.getUserEmail().equals(userEmail))
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        todoRepository.delete(todoEntity);
    }

    public Map<String, Object> getTodoStatistics(String userEmail) {
        List<CategoryEntity> categories = categoryRepository.findByUserEmail(userEmail);
        Map<String, Object> stats = new HashMap<>();

        for (CategoryEntity category : categories) {
            List<TodoEntity> todos = todoRepository.findByCategoryId_IdAndUserEmail(category.getId(), userEmail);
            long completedCount = todos.stream().filter(TodoEntity::isCompleted).count();
            long totalCount = todos.size();

            Map<String, Long> categoryStats = new HashMap<>();
            categoryStats.put("completed", completedCount);
            categoryStats.put("total", totalCount);

            stats.put(category.getCategoryName(), categoryStats);
        }

        return stats;
    }

    public Map<String, Object> getMonthlyTodoStatistics(String userEmail, int year, int month) {
        List<CategoryEntity> categories = categoryRepository.findByUserEmail(userEmail);
        Map<String, Object> stats = new HashMap<>();

        for (CategoryEntity category : categories) {
            List<TodoEntity> todos = todoRepository.findByCategoryId_IdAndUserEmail(category.getId(), userEmail)
                    .stream()
                    .filter(todo -> {
                        String[] dateParts = todo.getDate().split("-");
                        int todoYear = Integer.parseInt(dateParts[0]);
                        int todoMonth = Integer.parseInt(dateParts[1]);
                        return todoYear == year && todoMonth == month;
                    })
                    .collect(Collectors.toList());

            long completedCount = todos.stream().filter(TodoEntity::isCompleted).count();
            long totalCount = todos.size();

            Map<String, Long> categoryStats = new HashMap<>();
            categoryStats.put("completed", completedCount);
            categoryStats.put("total", totalCount);

            stats.put(category.getCategoryName(), categoryStats);
        }

        return stats;
    }


}
