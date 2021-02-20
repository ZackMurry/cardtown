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
import com.zackmurry.cardtown.util.UUIDCompressor;
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

        if (addRequest.getId() == null || argId == null) {
            throw new BadRequestException();
        }

        argumentService.addCardToArgument(argId, addRequest.getId());
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
    public void removeCardFromArgument(@RequestParam short index, HttpServletRequest servletRequest) {
        final String relevantPath = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        final String[] parts = relevantPath.split("/cards/");
        final String argId = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
        final String cardId = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        argumentService.removeCardFromArgument(argId, cardId, index);
    }

    @PutMapping("/id/**")
    public void renameArgument(@NonNull @RequestBody ArgumentRenameRequest renameRequest, HttpServletRequest servletRequest) {
        final String encodedId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (encodedId == null || encodedId.isEmpty() || renameRequest.getName() == null) {
            throw new BadRequestException();
        }
        final String decodedId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8);
        argumentService.renameArgument(decodedId, renameRequest.getName());
    }

    @DeleteMapping("/id/**")
    public void deleteArgument(HttpServletRequest servletRequest) {
        final String encodedId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1];
        if (encodedId == null || encodedId.isEmpty()) {
            throw new BadRequestException();
        }
        final String decodedId = URLDecoder.decode(encodedId, StandardCharsets.UTF_8);
        argumentService.deleteArgument(decodedId);
    }

    @PatchMapping("/id/**/cards")
    public void updateArgumentCardPositions(@NonNull @RequestBody ReorderCardsInArgumentRequest reorderRequest, HttpServletRequest servletRequest) {
        if (reorderRequest.getOldIndex() == null || reorderRequest.getNewIndex() == null) {
            throw new BadRequestException();
        }
        final String encodedArgId = servletRequest.getRequestURI().split("/api/v1/arguments/id/")[1].split("/cards")[0];
        final String decodedArgId = URLDecoder.decode(encodedArgId, StandardCharsets.UTF_8);
        argumentService.updateCardPositions(decodedArgId, reorderRequest.getNewIndex(), reorderRequest.getOldIndex());
    }

}
