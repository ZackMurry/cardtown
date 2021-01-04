package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.service.CardService;
import com.zackmurry.cardtown.util.EncryptionUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/api/v1/cards")
@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/auth-test")
    public String authTest() {
        return Base64.encodeBase64String(((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey());
    }

    @GetMapping("/**")
    public ResponseEntity<ResponseCard> getCardById(HttpServletRequest request) {
        String encodedId = request.getRequestURI().split("/api/v1/cards/")[1];
        if (encodedId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String id = URLDecoder.decode(encodedId, StandardCharsets.UTF_8);
        return cardService.getResponseCardById(id);
    }

    /**
     * @return the shortened id of the new card
     */
    @PostMapping("")
    public ResponseEntity<String> createCard(@RequestBody CardCreateRequest request) {
        request.setOwnerEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return cardService.createCard(request);
    }

    @GetMapping("")
    public ResponseEntity<List<ResponseCard>> getAllCardsByUser() {
        return cardService.getAllCardsByUser();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getNumberOfCardsByUser() {
        return cardService.getNumberOfCardsByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @DeleteMapping("/**")
    public ResponseEntity<Void> deleteCardById(HttpServletRequest request) {
        String compressedId = request.getRequestURI().split("/api/v1/cards/")[1];
        if (compressedId == null) {
            System.out.println("encoded is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return cardService.deleteCardById(compressedId);
    }

}
