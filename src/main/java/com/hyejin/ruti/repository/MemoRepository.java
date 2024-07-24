package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.MemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<MemoEntity, Long> {
    List<MemoEntity> findByMemoWriter(String memoWriter);
    List<MemoEntity> findByMemoContentContainingAndMemoWriter(String keyword, String memoWriter);
}
