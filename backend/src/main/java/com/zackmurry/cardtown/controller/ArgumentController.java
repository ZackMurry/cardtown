package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.model.CountResponse;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentPreview;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.CardIdHolder;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.service.ArgumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    public String createArgument(@RequestBody ArgumentCreateRequest request) {
       final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
       request.setOwnerId(userId);
       return argumentService.createArgument(request);
    }

    @GetMapping("/id/**")
    public ResponseArgument getArgumentById(HttpServletRequest request) {
        final String encodedId = request.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (encodedId == null) {
            throw new BadRequestException();
        }
        final String decodedId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8);
        return argumentService.getResponseArgumentById(decodedId);
    }

    @PostMapping("/id/**/cards")
    public void addCardToArgument(@NonNull @RequestBody CardIdHolder addRequest, HttpServletRequest servletRequest) {
        final String relevantPath = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        final String encodedArgId = relevantPath.split("/")[0];
        final String argId = URLDecoder.decode(encodedArgId, StandardCharsets.UTF_8);
        argumentService.addCardToArgument(addRequest.getCardId(), argId);
    }

    @GetMapping("")
    public List<ArgumentPreview> getArgumentsByUser() {
        return argumentService.listArgumentsByUser();
    }

    @GetMapping("/count")
    public CountResponse getNumberOfArgsByUser() {
        final int c = argumentService.getNumberOfArgsByUser();
        return new CountResponse(c);
    }

    @DeleteMapping("/id/**/cards/**")
    public void removeCardFromArgument(HttpServletRequest servletRequest) {
        final String relevantPath = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        final String[] parts = relevantPath.split("/cards/");
        final String argId = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
        final String cardId = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        argumentService.removeCardFromArgument(argId, cardId);
    }

}
