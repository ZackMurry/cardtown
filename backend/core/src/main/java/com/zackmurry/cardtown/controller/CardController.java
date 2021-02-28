package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.model.CountResponse;
import com.zackmurry.cardtown.model.arg.card.ArgumentWithCardModel;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.CardPreview;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.service.ArgumentService;
import com.zackmurry.cardtown.service.CardService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequestMapping("/api/v1/cards")
@RestController
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private ArgumentService argumentService;

    // todo switch a lot of controller methods to @Valid for validation
    @GetMapping("/auth-test")
    public String authTest() {
        return Base64.encodeBase64String(((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey());
    }

    @GetMapping("/previews")
    public List<CardPreview> getCardPreviewsByUser() {
        return cardService.getCardPreviewsByUser();
    }

    @GetMapping("/id/**")
    public ResponseCard getCardById(HttpServletRequest request) {
        final String cardId = request.getRequestURI().split("/api/v1/cards/id/")[1];
        if (cardId == null) {
            throw new BadRequestException();
        }
        return cardService.getResponseCardById(cardId);
    }

    /**
     * @return the shortened id of the new card
     */
    @PostMapping("")
    public String createCard(@RequestBody CardCreateRequest request) {
        request.setOwnerEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return cardService.createCard(request);
    }

    @GetMapping("")
    public List<ResponseCard> getAllCardsByUser() {
        return cardService.getAllCardsByUser();
    }

    @GetMapping("/count")
    public CountResponse getNumberOfCardsByUser() {
        final int c = cardService.getNumberOfCardsByUser();
        return new CountResponse(c);
    }

    @DeleteMapping("/id/**")
    public void deleteCardById(HttpServletRequest request) {
        final String cardId = request.getRequestURI().split("/api/v1/cards/id/")[1];
        if (cardId == null) {
            throw new BadRequestException();
        }
        cardService.deleteCardById(cardId);
    }

    @PutMapping("/id/**")
    public void updateCardById(@NonNull @RequestBody CardCreateRequest cardUpdateRequest, HttpServletRequest servletRequest) {
        final String cardId = servletRequest.getRequestURI().split("/api/v1/cards/id/")[1];
        if (cardId == null) {
            throw new BadRequestException();
        }
        cardService.updateCardById(cardId, cardUpdateRequest);
    }

    @GetMapping("/id/**/arguments")
    public List<ArgumentWithCardModel> getArgumentPreviewsByCardId(HttpServletRequest servletRequest) {
        final String cardId = servletRequest.getRequestURI().split("/api/v1/cards/id/")[1].split("/arguments")[0];
        if (cardId == null) {
            throw new BadRequestException();
        }
        return argumentService.getArgumentPreviewsByCardId(cardId);
    }

}
