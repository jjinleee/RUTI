package com.hyejin.ruti.dto;

import com.hyejin.ruti.entity.CategoryEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String category;
    private String categoryColor;

    public static CategoryDTO toCategoryDTO(CategoryEntity categoryEntity) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryEntity.getId());
        categoryDTO.setCategory(categoryEntity.getCategory());
        categoryDTO.setCategoryColor(categoryEntity.getCategoryColor());
        return categoryDTO;
    }
}
