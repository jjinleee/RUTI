package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    //회원가입
    @GetMapping("/join")
    public String createForm() {
        return "create";
    } //생략가능

    @PostMapping("/join")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        UserEntity userEntity = userService.saveUser(userDTO);
        if (userEntity == null) {
            if (userService.isEmailTaken(userDTO.getUserEmail())) {
                return new ResponseEntity<>("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT);
            } else if (userService.isNicknameTaken(userDTO.getNickname())) {
                return new ResponseEntity<>("이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(userEntity, HttpStatus.CREATED);
    }
    //이메일중복, 닉네임중복확인
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam("email") String email) {
        return userService.isEmailTaken(email);
    }

    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam("nickname") String nickname) {
        return userService.isNicknameTaken(nickname);
    }

    //이메일로 회원찾기
    @GetMapping("/{email}")
    public UserDTO getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDTO userDTO, HttpSession session){
        UserDTO loginResult=userService.login(userDTO);
        if(loginResult!=null){
            session.setAttribute("loginEmail",loginResult.getUserEmail());
            return ResponseEntity.status(HttpStatus.OK).build();
        }else {
            return ResponseEntity.status(401).body("이메일 또는 비밀번호가 올바르지 않습니다.");        }
    }

//구글로그인
//    @PostMapping("/oauth2/callback")
//    public ResponseEntity<Object> googleLogin(@RequestBody Map<String, String> requestBody, HttpSession session) {
//        String idTokenString = requestBody.get("token");
//        OidcUser oidcUser = userService.verifyGoogleToken(idTokenString);
//
//        if (oidcUser != null) {
//            String email = oidcUser.getEmail();
//            String name = oidcUser.getFullName();
//            UserDTO user = userService.processOAuthPostLogin(email, name);
//            session.setAttribute("loginEmail", user.getUserEmail());
//            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("success", true));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("success", false));
//        }
//    }
}
