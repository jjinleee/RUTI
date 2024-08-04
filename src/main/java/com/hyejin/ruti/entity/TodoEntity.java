package com.hyejin.ruti.entity;

import com.hyejin.ruti.dto.TodoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="todo_table")
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String todoContent;

    @Column
    private String date;

    @Column
    private boolean completed;

    @Column
    private String userEmail;

    @ManyToOne
    @JoinColumn(name="category_id")
    private CategoryEntity categoryId;

    public static TodoEntity toTodoEntity(TodoDTO todoDTO, CategoryEntity categoryEntity, String userEmail) {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setId(todoDTO.getId());
        todoEntity.setTodoContent(todoDTO.getTodoContent());
        todoEntity.setDate(todoDTO.getDate());
        todoEntity.setCompleted(todoDTO.isCompleted());
        todoEntity.setUserEmail(userEmail);
        todoEntity.setCategoryId(categoryEntity);
        return todoEntity;
    }
}