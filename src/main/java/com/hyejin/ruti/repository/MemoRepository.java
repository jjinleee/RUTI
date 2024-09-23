package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.MemoEntity;
import com.hyejin.ruti.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<MemoEntity, Long> {
    List<MemoEntity> findByUser(UserEntity user);
    List<MemoEntity> findByMemoContentContainingAndUser(String keyword, UserEntity user);
}
