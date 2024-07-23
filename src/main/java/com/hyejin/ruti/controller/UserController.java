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

    @GetMapping("/join")
    public String create() {return "create";}

    @PostMapping("/join")
    public UserEntity createUser(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @GetMapping("/{email}")
    public UserEntity getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}
