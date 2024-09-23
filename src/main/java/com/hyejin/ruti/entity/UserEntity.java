package com.hyejin.ruti.entity;

import com.hyejin.ruti.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@Table(name="user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String userEmail;

    @Column
    private String userPW;

    @Column(nullable = false)
    private int completedTodoCount=0;

    @Column(nullable = false)
    private String badgeLevel="Bronze 1";

    // DTO를 Entity로 변환하는 메서드
    public static UserEntity toUserEntity(UserDTO userDTO){
        UserEntity userEntity=new UserEntity();
        userEntity.setId(userDTO.getId());
        userEntity.setUserEmail(userDTO.getUserEmail());
        userEntity.setUserPW(userDTO.getUserPW());
        userEntity.setNickname(userDTO.getNickname());
        userEntity.setBadgeLevel("Bronze 1");
        return userEntity;
    }
}
