package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.RoutineStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoutineStateRepository extends JpaRepository<RoutineStateEntity, Long> {
    @Query("SELECT rse FROM RoutineStateEntity rse WHERE rse.routine = :routine AND rse.date = :date")
    Optional<RoutineStateEntity> findByRoutineAndDate(@Param("routine") RoutineEntity routine, @Param("date") String date);

}