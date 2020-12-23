package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.AuthenticationResponse;
import com.zackmurry.cardtown.model.User;
import com.zackmurry.cardtown.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<AuthenticationResponse> createUserAccount(@RequestBody User user) {
        return userService.createUserAccount(user);
    }

    @GetMapping("/test")
    public String test() {
        return "this is a test";
    }

}
