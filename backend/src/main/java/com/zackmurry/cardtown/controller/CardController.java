package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.auth.PrincipalModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/cards")
@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("/auth-test")
    public String authTest() {
        return ((PrincipalModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCard> getCardById(@PathVariable String id) {
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

}
