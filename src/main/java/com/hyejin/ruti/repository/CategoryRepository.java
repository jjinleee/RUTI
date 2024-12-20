package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByUserEmail(String userEmail);

    void deleteByUserEmail(String userEmail);
}
