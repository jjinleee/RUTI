package com.hyejin.ruti.dto;

import com.hyejin.ruti.entity.TodoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TodoDTO {
    private Long id;
    private String todoContent;
    private String date;
    private Long categoryId;
    private boolean completed;

    public static TodoDTO toTodoDTO(TodoEntity todoEntity) {
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setId(todoEntity.getId());
        todoDTO.setTodoContent(todoEntity.getTodoContent());
        todoDTO.setDate(todoEntity.getDate());
        todoDTO.setCategoryId(todoEntity.getCategoryId() != null ? todoEntity.getCategoryId().getId() : null);
        todoDTO.setCompleted(todoEntity.isCompleted());  // 추가된 필드
        return todoDTO;
    }
}