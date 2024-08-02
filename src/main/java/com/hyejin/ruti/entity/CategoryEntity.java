package com.hyejin.ruti.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="category_table")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String category;

    @Column
    private String categoryColor;

    @OneToMany(mappedBy="categoryId")
    private List<TodoEntity> todos;
}