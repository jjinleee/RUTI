package com.hyejin.ruti.entity;

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

    @ManyToOne
    @JoinColumn(name="category_id")
    private CategoryEntity categoryId;
}