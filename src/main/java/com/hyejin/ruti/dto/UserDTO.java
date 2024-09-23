package com.hyejin.ruti.dto;

import com.hyejin.ruti.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    @NotBlank(message = "닉네임을 입력하세요")
    private String nickname;

    @Email(message = "이메일을 입력하세요")
    @NotBlank(message = "이메일을 입력하세요")
    private String userEmail;

    @NotBlank(message = "이메일을 입력하세요")
    private String userPW;

    private int completedTodoCount;  // 추가된 필드
    private String badgeLevel;  // 추

    // Entity를 DTO로 변환하는 메서드
    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setNickname(userEntity.getNickname());
        userDTO.setUserEmail(userEntity.getUserEmail());
        userDTO.setUserPW(userEntity.getUserPW());
        userDTO.setCompletedTodoCount(userEntity.getCompletedTodoCount());  // 완료된 Todo 개수 필드 추가
        userDTO.setBadgeLevel(userEntity.getBadgeLevel());
        return userDTO;
    }
}
