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
    private String time;

    @Column
    private String duration;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private String userEmail;

    public static RoutineEntity toRoutineEntity(RoutineDTO routineDTO, String userEmail) {
        RoutineEntity routineEntity = new RoutineEntity();
        routineEntity.setTitle(routineDTO.getTitle());
        routineEntity.setTime(routineDTO.getTime());
        routineEntity.setDuration(routineDTO.getDuration());
        routineEntity.setStatus(routineDTO.getStatus());
        routineEntity.setFrequency(routineDTO.getFrequency());
        routineEntity.setUserEmail(userEmail);
        return routineEntity;
    }}
