package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity saveUser(UserDTO userDTO) {
        if (isEmailTaken(userDTO.getUserEmail())) {
            return null;
        }
        if (isNicknameTaken(userDTO.getNickname())) {
            return null;
        }

        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        return userRepository.save(userEntity);
    }

    public UserDTO getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByUserEmail(email).orElse(null);
        if (userEntity != null) {
            return UserDTO.toUserDTO(userEntity);
        }
        return null;
    }

    //이메일,닉네임 중복처리
    public boolean isEmailTaken(String email){
        Optional<UserEntity> user=userRepository.findByUserEmail(email);
        return user.isPresent();
    }
    public boolean isNicknameTaken(String nickname) {
        Optional<UserEntity> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }


}
