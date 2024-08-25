package com.hyejin.ruti.entity;

import com.hyejin.ruti.dto.RoutineDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="routine_table")
public class RoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String startDate;

    @Column(nullable = false)
    private String endDate;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String activeDays;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String state = "pending";

    public static RoutineEntity toRoutineEntity(RoutineDTO routineDTO, String userEmail) {
        RoutineEntity routineEntity = new RoutineEntity();
        routineEntity.setTitle(routineDTO.getTitle());
        routineEntity.setStartDate(routineDTO.getStartDate());
        routineEntity.setEndDate(routineDTO.getEndDate());
        routineEntity.setTime(routineDTO.getTime());
        routineEntity.setActiveDays(routineDTO.getActiveDays());
        routineEntity.setUserEmail(userEmail);
        routineEntity.setState(routineDTO.getState() != null ? routineDTO.getState() : "pending");  // state가 전달되지 않으면 기본값 사용
        return routineEntity;
    }
}