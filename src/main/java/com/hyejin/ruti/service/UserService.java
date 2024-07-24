package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity saveUser(UserDTO userDTO) {
        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        return userRepository.save(userEntity);
    }

    public UserEntity getUserByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

}
