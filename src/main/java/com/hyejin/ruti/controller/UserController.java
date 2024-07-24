package com.hyejin.ruti.controller;

import com.hyejin.ruti.dto.UserDTO;
import com.hyejin.ruti.entity.UserEntity;
import com.hyejin.ruti.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public UserEntity createUser(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    //이메일중복, 닉네임중복확인


    //이메일로 회원찾기
    @GetMapping("/{email}")
    public UserEntity getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

}
