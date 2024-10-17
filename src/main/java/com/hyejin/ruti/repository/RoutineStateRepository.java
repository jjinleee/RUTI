package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.RoutineStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoutineStateRepository extends JpaRepository<RoutineStateEntity, Long> {
    Optional<RoutineStateEntity> findByRoutineAndDate(RoutineEntity routine, String date);
}