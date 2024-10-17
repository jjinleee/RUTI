package com.hyejin.ruti.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hyejin.ruti.dto.RoutineDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@Table(name="routine_table")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    //순환참조문제때문에 EAGER로 로딩변경->성능은 안좋아짐 , 이렇게하거나 엔티티대신 DTO를 직접 반환하여 직렬화문제를 해결할수있음
    @JsonManagedReference
    private List<RoutineStateEntity> routineStateEntities;

    public static RoutineEntity toRoutineEntity(RoutineDTO routineDTO, String userEmail) {
        RoutineEntity routineEntity = new RoutineEntity();
        routineEntity.setTitle(routineDTO.getTitle());
        routineEntity.setStartDate(routineDTO.getStartDate());
        routineEntity.setEndDate(routineDTO.getEndDate());
        routineEntity.setTime(routineDTO.getTime());
        routineEntity.setActiveDays(routineDTO.getActiveDays());
        routineEntity.setUserEmail(userEmail);
        return routineEntity;
    }
}