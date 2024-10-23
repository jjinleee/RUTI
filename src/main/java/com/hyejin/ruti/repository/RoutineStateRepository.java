package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.RoutineStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineStateRepository extends JpaRepository<RoutineStateEntity, Long> {
    @Query("SELECT rse FROM RoutineStateEntity rse WHERE rse.routine = :routine AND rse.date = :date")
    Optional<RoutineStateEntity> findByRoutineAndDate(@Param("routine") RoutineEntity routine, @Param("date") String date);
    List<RoutineStateEntity> findByRoutine(RoutineEntity routine);

    @Modifying
    @Query("DELETE FROM RoutineStateEntity r WHERE r.routine = :routine AND (r.date < :startDate OR r.date > :endDate OR r.date NOT IN (:activeDays))")
    void deleteByRoutineAndOutsideDateRange(
            @Param("routine") RoutineEntity routine,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("activeDays") List<String> activeDays);
}