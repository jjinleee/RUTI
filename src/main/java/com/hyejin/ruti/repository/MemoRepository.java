package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.MemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<MemoEntity, Long> {
}
