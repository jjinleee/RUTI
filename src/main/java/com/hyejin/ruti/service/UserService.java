package com.hyejin.ruti.service;

import com.hyejin.ruti.dto.UserBadgeDTO;
import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.RowId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TodoRepository todoRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final MemoRepository memoRepository;
    @Autowired
    private final RoutineRepository routineRepository;


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

    public boolean changeNickname(String userEmail, String newNickname) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserEmail(userEmail);
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

    public boolean checkPw(String userEmail, String currentPassword) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserEmail(userEmail);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            return userEntity.getUserPW().equals(currentPassword);
        }
        return false;
    }

    public boolean changePw(String userEmail, String newPassword) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserEmail(userEmail);
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            userEntity.setUserPW(newPassword);
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteUser(String email, String password){
        Optional<UserEntity> userEntityOptional=userRepository.findByUserEmail(email);
        if(userEntityOptional.isPresent()){
            UserEntity userEntity=userEntityOptional.get();
            if(userEntity.getUserPW().equals(password)){
                deleteRelatedData(userEntity.getUserEmail());

                userRepository.delete(userEntity);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void deleteRelatedData(String userEmail) {
        // userEmail을 외래 키로 사용하는 모든 엔티티를 삭제
        memoRepository.deleteByUserEmail(userEmail);
        todoRepository.deleteByUserEmail(userEmail);
        routineRepository.deleteByUserEmail(userEmail);
        categoryRepository.deleteByUserEmail(userEmail);
    }

    //뱃지관리 로직
    public UserBadgeDTO getUserBadgeInfo(Long userId) {
        // 사용자의 완료된 할 일(Todo) 수 가져오기
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 배지레벨
        int completedTodos = user.getCompletedTodoCount();
        String badgeLevel = determineBadgeLevel(completedTodos);

        if (!badgeLevel.equals(user.getBadgeLevel())) {
            user.setBadgeLevel(badgeLevel);
            userRepository.save(user); // 배지 레벨이 변경되면 업데이트
        }
        int todosUntilNextLevel = calculateTodosUntilNextLevel(completedTodos);

        return new UserBadgeDTO(user.getNickname(),completedTodos, badgeLevel, todosUntilNextLevel);
    }

    public void updateBadgeLevel(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        int completedTodos = user.getCompletedTodoCount();
        String badgeLevel = determineBadgeLevel(completedTodos);
        user.setBadgeLevel(badgeLevel);
        userRepository.save(user);
    }

    private String determineBadgeLevel(int completedTodos) {
        if (completedTodos >= 371) return "Diamond 3";
        else if (completedTodos >= 321) return "Diamond 2";
        else if (completedTodos >= 271) return "Diamond 1";
        else if (completedTodos >= 231) return "Gold 3";
        else if (completedTodos >= 191) return "Gold 2";
        else if (completedTodos >= 151) return "Gold 1";
        else if (completedTodos >= 121) return "Silver 3";
        else if (completedTodos >= 91) return "Silver 2";
        else if (completedTodos >= 61) return "Silver 1";
        else if (completedTodos >= 41) return "Bronze 3";
        else if (completedTodos >= 21) return "Bronze 2";
        else return "Bronze 1";
    }

    //다음 레벨까지 남은 todo 계산
    private int calculateTodosUntilNextLevel(int completedTodos) {
        if (completedTodos >= 371) {
            return 0; // 최고 레벨에 도달한 경우
        } else if (completedTodos >= 321) {
            return 371 - completedTodos;
        } else if (completedTodos >= 271) {
            return 321 - completedTodos;
        } else if (completedTodos >= 231) {
            return 271 - completedTodos;
        } else if (completedTodos >= 191) {
            return 231 - completedTodos;
        } else if (completedTodos >= 151) {
            return 191 - completedTodos;
        } else if (completedTodos >= 121) {
            return 151 - completedTodos;
        } else if (completedTodos >= 91) {
            return 121 - completedTodos;
        } else if (completedTodos >= 61) {
            return 91 - completedTodos;
        } else if (completedTodos >= 41) {
            return 61 - completedTodos;
        } else if (completedTodos >= 21) {
            return 41 - completedTodos;
        } else {
            return 21 - completedTodos;
        }
    }

    public void updateFcmToken(String userEmail, String fcmToken) {
        Optional<UserEntity> optionalUser = userRepository.findByUserEmail(userEmail);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setFcmToken(fcmToken); // FCM 토큰 설정
            userRepository.save(user);  // 변경된 엔티티 저장
            System.out.println("FCM 토큰이 업데이트되었습니다: " + fcmToken);
        } else {
            System.out.println("사용자를 찾을 수 없습니다. 이메일: " + userEmail);
        }
    }

    public String getFcmToken(String userEmail) {
        Optional<UserEntity> userOptional = userRepository.findByUserEmail(userEmail);
        return userOptional.map(UserEntity::getFcmToken).orElse(null); // 값이 있으면 FCM 토큰을 반환, 없으면 null 반환
    }
}
