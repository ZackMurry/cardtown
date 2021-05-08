package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.model.CountResponse;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentPreview;
import com.zackmurry.cardtown.model.arg.ArgumentRenameRequest;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.CardIdHolder;
import com.zackmurry.cardtown.model.arg.card.ReorderCardsInArgumentRequest;
import com.zackmurry.cardtown.service.ArgumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        return argumentService.createArgument(request);
    }

    @GetMapping("/id/**")
    public ResponseArgument getArgumentById(HttpServletRequest request) {
        final String cardId = request.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (cardId == null) {
            throw new BadRequestException();
        }
        return argumentService.getResponseArgumentById(cardId);
    }

    @PostMapping("/id/**/cards")
    public void addCardToArgument(@NonNull @RequestBody CardIdHolder addRequest, HttpServletRequest servletRequest) {
        final String relevantPath = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        final String argId = relevantPath.split("/")[0];

        if (addRequest.getId() == null || argId == null) {
            throw new BadRequestException();
        }

        argumentService.addCardToArgument(argId, addRequest.getId());
    }

    @GetMapping("")
    public List<ArgumentPreview> getArgumentsByUser(@RequestParam(required = false) boolean showDeleted) {
        return argumentService.listArgumentsByUser(showDeleted);
    }

    @GetMapping("/count")
    public CountResponse getNumberOfArgsByUser() {
        final int c = argumentService.getNumberOfArgumentsByUser();
        return new CountResponse(c);
    }

    @DeleteMapping("/id/**/cards/**")
    public void removeCardFromArgument(@RequestParam short index, HttpServletRequest servletRequest) {
        final String relevantPath = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        final String[] parts = relevantPath.split("/cards/");
        argumentService.removeCardFromArgument(parts[0], parts[1], index);
    }

    @PutMapping("/id/**")
    public void renameArgument(@NonNull @RequestBody ArgumentRenameRequest renameRequest, HttpServletRequest servletRequest) {
        final String argId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (argId == null || argId.isEmpty() || renameRequest.getName() == null) {
            throw new BadRequestException();
        }
        argumentService.renameArgument(argId, renameRequest.getName());
    }

    @DeleteMapping("/id/**")
    public void deleteArgument(HttpServletRequest servletRequest) {
        final String argId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (argId == null) {
            throw new BadRequestException();
        }
        final String decodedId = URLDecoder.decode(argId, StandardCharsets.UTF_8);
        argumentService.deleteArgument(decodedId);
    }

    @PatchMapping("/id/**/cards")
    public void updateArgumentCardPositions(@NonNull @RequestBody ReorderCardsInArgumentRequest reorderRequest, HttpServletRequest servletRequest) {
        if (reorderRequest.getOldIndex() == null || reorderRequest.getNewIndex() == null) {
            throw new BadRequestException();
        }
        final String argId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1].split("/cards")[0];
        argumentService.updateCardPositions(argId, reorderRequest.getNewIndex(), reorderRequest.getOldIndex());
    }

}
