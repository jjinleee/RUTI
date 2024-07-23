package com.hyejin.ruti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    @NotBlank(message = "닉네임을 입력하세요")
    private String nickname;

    @Email(message = "이메일을 입력하세요")
    @NotBlank(message = "이메일을 입력하세요")
    private String userEmail;

    @NotBlank(message = "이메일을 입력하세요")
    private String userPW;
}
