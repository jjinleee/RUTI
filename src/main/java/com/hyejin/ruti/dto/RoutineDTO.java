package com.hyejin.ruti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoutineDTO {
    private String title;
    private String startDate;
    private String endDate;
    private String time;
    private String activeDays;
    private String state;
}