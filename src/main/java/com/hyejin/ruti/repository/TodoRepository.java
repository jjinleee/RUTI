package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    List<TodoEntity> findByDate(String date);

    @Transactional
    @Modifying
    @Query("DELETE FROM TodoEntity t WHERE t.categoryId.id = ?1")
    void deleteByCategoryId(Long categoryId);
}
