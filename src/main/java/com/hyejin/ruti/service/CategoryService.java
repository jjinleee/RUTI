package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.CategoryDTO;
import com.hyejin.ruti.entity.CategoryEntity;
import com.hyejin.ruti.repository.CategoryRepository;
import com.hyejin.ruti.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TodoRepository todoRepository;

    public List<CategoryDTO> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDTO::toCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryDTO.toCategoryDTO(categoryEntity);
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategory(categoryDTO.getCategory());
        categoryEntity.setCategoryColor(categoryDTO.getCategoryColor());
        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);
        return CategoryDTO.toCategoryDTO(savedEntity);
    }

    public void deleteCategory(Long categoryId) {
        todoRepository.deleteByCategoryId(categoryId);
        categoryRepository.deleteById(categoryId);
    }
}
