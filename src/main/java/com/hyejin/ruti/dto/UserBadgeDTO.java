package com.hyejin.ruti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBadgeDTO {
    private String nickname;
    private int completedTodoCount;
    private String badgeLevel;
    private int todosUntilNextLevel;

    public UserBadgeDTO(String nickname, int completedTodoCount, String badgeLevel, int todosUntilNextLevel) {
        this.nickname = nickname;
        this.completedTodoCount = completedTodoCount;
        this.badgeLevel = badgeLevel;
        this.todosUntilNextLevel = todosUntilNextLevel;
    }
}