package com.hyejin.ruti.entity;

import com.hyejin.ruti.dto.CategoryDTO;
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

    @Column
    private String userEmail;

    @OneToMany(mappedBy="categoryId")
    private List<TodoEntity> todos;

    public static CategoryEntity toCategoryEntity(CategoryDTO categoryDTO, String userEmail) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategory(categoryDTO.getCategory());
        categoryEntity.setCategoryColor(categoryDTO.getCategoryColor());
        categoryEntity.setUserEmail(userEmail);
        return categoryEntity;
    }

    public String getCategoryName() {
        return this.category;
    }
}