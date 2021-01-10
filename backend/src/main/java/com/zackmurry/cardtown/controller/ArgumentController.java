package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.ArgumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/**")
    public ResponseEntity<ResponseArgument> getArgumentById(HttpServletRequest request) {
        String encodedId = request.getRequestURI().split("/api/v1/arguments/")[1];
        if (encodedId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String decodedId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8);
        return argumentService.getResponseArgumentById(decodedId);
    }

}
