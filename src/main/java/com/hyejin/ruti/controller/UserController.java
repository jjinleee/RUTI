package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.service.NotificationService;
import com.hyejin.ruti.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final NotificationService notificationService;


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
            notificationService.addLoggedInUser(loginResult.getUserEmail());
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

    //닉네임 변경
    @GetMapping("/current-nickname")
    public ResponseEntity<?> getCurrentNickname(HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        if (userEmail != null) {
            UserDTO userDTO = userService.getUserByEmail(userEmail);
            if (userDTO != null) {
                return ResponseEntity.ok(Collections.singletonMap("nickname", userDTO.getNickname()));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자가 로그인되어 있지 않습니다.");
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<?> changeNickname(@RequestBody Map<String, String> request, HttpSession session) {
        String userEmail = (String) session.getAttribute("loginEmail");
        String newNickname = request.get("newNickname");
        boolean isChanged = userService.changeNickname(userEmail, newNickname);

        if (isChanged) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("success", false));
        }
    }
    //비번 변경
    @PostMapping("/check-pw")
    public ResponseEntity<Boolean> checkPw(@RequestBody Map<String, String> request, HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        String currentPassword = request.get("currentPassword");
        boolean isValid = userService.checkPw(email, currentPassword);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/change-pw")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String newPassword = request.get("newPassword");
        boolean isChanged = userService.changePw(email, newPassword);

        if (isChanged) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("success", false));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session= request.getSession(false);
        if(session!=null){
            String loggedInUserEmail = (String) session.getAttribute("loginEmail");
            session.invalidate();
            if (loggedInUserEmail != null) {
                notificationService.removeLoggedInUser(loggedInUserEmail);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String,String> requset, HttpSession session){
        String email=requset.get("email");
        String password=requset.get("password");

        if(email==null || email.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("messsage","해당 이메일이 없습니다"));
        }
        boolean isDeleted= userService.deleteUser(email,password);

        if(isDeleted){
            session.invalidate();
            return ResponseEntity.ok(Collections.singletonMap("success",true));
        } else{
            String errorMessage=userService.isEmailTaken(email)? "비밀번호가 올바르지 않습니다." : "존재하지 않는 이메일입니다.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", errorMessage));
        }
    }
    @PostMapping("/saveToken")
    public ResponseEntity<String> saveToken(@RequestBody Map<String, String> tokenData, HttpSession session) {
        String token = tokenData.get("token");
        String email = (String) session.getAttribute("loginEmail");

        if (token == null || email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token or email is missing");
        }

        // FCM 토큰을 업데이트하는 서비스 호출
        userService.updateFcmToken(email, token);
        return ResponseEntity.ok("Token received and saved");
    }

    @PostMapping("/updateFcmToken")
    public ResponseEntity<String> updateFcmToken(@RequestBody Map<String, String> tokenData, HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        String token = tokenData.get("token");

        if (email == null || token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token or email is missing");
        }

        userService.updateFcmToken(email, token);
        return ResponseEntity.ok("FCM 토큰이 업데이트되었습니다.");
    }

}
