package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.auth.AuthenticationResponse;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserRole;
import com.zackmurry.cardtown.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public AuthenticationResponse createUserAccount(@RequestBody User user) {
        // reserved for unit tests
        if (user.getFirstName().equals("__TEST__") && user.getLastName().equals("__USER__")) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
        user.setRoles(List.of(UserRole.USER));
        return userService.createUserAccount(user);
    }

    @GetMapping("/test")
    public String test() {
        return "this is a test";
    }

}
