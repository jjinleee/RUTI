package com.hyejin.ruti.repository;

import com.hyejin.ruti.entity.RoutineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    List<RoutineEntity> findByUserEmail(String userEmail);
    Optional<RoutineEntity> findByIdAndUserEmail(Long id, String userEmail);

    void deleteByUserEmail(String userEmail);
    List<RoutineEntity> findAllByUserEmailAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String userEmail, String startDate, String endDate);

}
