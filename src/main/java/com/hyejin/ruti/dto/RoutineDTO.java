package com.hyejin.ruti.dto;

import com.hyejin.ruti.entity.RoutineEntity;
import com.hyejin.ruti.entity.RoutineStateEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class RoutineDTO {
    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private String time;
    private String activeDays;
    private String userEmail;
    private Map<String, String> statesByDate; // 날짜별 상태를 저장하는 필드 추가

    // RoutineEntity로부터 DTO를 생성하는 메서드
    public static RoutineDTO fromEntity(RoutineEntity routine) {
        RoutineDTO dto = new RoutineDTO();
        dto.setId(routine.getId());
        dto.setTitle(routine.getTitle());
        dto.setStartDate(routine.getStartDate());
        dto.setEndDate(routine.getEndDate());
        dto.setTime(routine.getTime());
        dto.setActiveDays(routine.getActiveDays());
        dto.setUserEmail(routine.getUserEmail());

        // 상태 정보를 날짜별로 매핑
        Map<String, String> statesByDate = routine.getRoutineStateEntities().stream()
                .collect(Collectors.toMap(RoutineStateEntity::getDate, RoutineStateEntity::getState));
        dto.setStatesByDate(statesByDate);

        return dto;
    }
}