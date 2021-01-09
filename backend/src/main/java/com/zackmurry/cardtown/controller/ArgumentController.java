package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.ArgumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/arguments")
@RestController
public class ArgumentController {

    @Autowired
    private ArgumentService argumentService;

    /**
     * @return a string containing the id of the new card (compressed in base64)
     */
    @PostMapping("")
    public ResponseEntity<String> createArgument(@RequestBody ArgumentCreateRequest request) {
       final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
       request.setOwnerId(userId);
       return argumentService.createArgument(request);
    }

}
