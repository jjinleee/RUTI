package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
//    @Autowired
//    private ClientRegistrationRepository clientRegistrationRepository;
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

    public UserDTO login(UserDTO userDTO){
        Optional<UserEntity> byemail=userRepository.findByUserEmail(userDTO.getUserEmail());
        if(byemail.isPresent()){
            UserEntity userEntity=byemail.get();
            if(userEntity.getUserPW().equals(userDTO.getUserPW())){
                UserDTO dto=UserDTO.toUserDTO((userEntity));
                return dto;
            } else{
                return null;
            }
        } else return null;
    }
//    public OidcUser verifyGoogleToken(String idTokenString) {
//        OidcIdToken idToken = new OidcIdToken(idTokenString, null, null, Map.of());
//        OidcUserRequest userRequest = new OidcUserRequest(clientRegistrationRepository.findByRegistrationId("google"), null, idToken);
//        OidcUserService oidcUserService = new OidcUserService();
//        return oidcUserService.loadUser(userRequest);
//    }
//
//    public UserDTO processOAuthPostLogin(String email, String name) {
//        Optional<UserEntity> existUser = userRepository.findByUserEmail(email);
//
//        if (existUser.isPresent()) {
//            return UserDTO.toUserDTO(existUser.get());
//        } else {
//            UserEntity newUser = new UserEntity();
//            newUser.setUserEmail(email);
//            newUser.setNickname(name.split("@")[0]);  // email 앞부분을 닉네임으로 설정
//            userRepository.save(newUser);
//            return UserDTO.toUserDTO(newUser);
//        }
//    }

    public boolean changeNickname(String oldNickname, String newNickname) {
        Optional<UserEntity> userEntityOptional = userRepository.findByNickname(oldNickname);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            if (isNicknameTaken(newNickname)) {
                return false; // 닉네임 중복 확인 실패
            }
            userEntity.setNickname(newNickname);
            userRepository.save(userEntity);
            return true; // 닉네임 변경 성공
        } else {
            return false; // 기존 닉네임을 찾지 못함
        }
    }
}
