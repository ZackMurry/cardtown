package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.model.CountResponse;
import com.zackmurry.cardtown.model.analytic.AnalyticCreateRequest;
import com.zackmurry.cardtown.model.analytic.AnalyticUpdateRequest;
import com.zackmurry.cardtown.model.analytic.ResponseAnalytic;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentPreview;
import com.zackmurry.cardtown.model.arg.ArgumentRenameRequest;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.IdHolder;
import com.zackmurry.cardtown.model.arg.card.ReorderCardsInArgumentRequest;
import com.zackmurry.cardtown.model.arg.card.ResponseArgumentCard;
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

    @PostMapping("/id/{id}/cards")
    public ResponseArgumentCard addCardToArgument(@PathVariable String id, @NonNull @RequestBody IdHolder addRequest) {
        if (addRequest.getId() == null) {
            throw new BadRequestException();
        }

        return argumentService.addCardToArgument(id, addRequest.getId());
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

    @DeleteMapping("/id/{argId}/cards/id/{cardId}")
    public void removeCardFromArgument(@PathVariable String argId, @PathVariable String cardId, @RequestParam short index) {
        argumentService.removeCardFromArgument(argId, cardId, index);
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

    @PatchMapping("/id/{id}/items")
    public void updateArgumentItemPosition(@PathVariable String id, @NonNull @RequestBody ReorderCardsInArgumentRequest reorderRequest) {
        argumentService.updateItemPosition(id, reorderRequest.getNewIndex(), reorderRequest.getOldIndex());
    }

    @PostMapping("/id/{id}/restore")
    public void restoreArgumentById(@PathVariable String id) {
        argumentService.restoreArgumentById(id);
    }

    @PostMapping("/id/{id}/delete")
    public void permanentlyDeleteArgument(@PathVariable String id) {
        argumentService.permanentlyDeleteArgumentById(id);
    }

    @PostMapping("/id/{id}/analytics")
    public ResponseAnalytic addAnalyticToArgument(@PathVariable String id, @NonNull @RequestBody AnalyticCreateRequest addRequest) {
        if (addRequest.getBody() == null) {
            throw new BadRequestException();
        }

        return argumentService.addAnalyticToArgument(id, addRequest);
    }

    @PutMapping("/id/{argId}/analytics/id/{id}")
    public void updateAnalyticInArgument(@PathVariable String argId, @PathVariable String id, @NonNull @RequestBody AnalyticUpdateRequest updateRequest) {
        if (updateRequest.getBody() == null) {
            throw new BadRequestException();
        }
        argumentService.updateAnalytic(id, updateRequest);
    }

    @DeleteMapping("/id/{argId}/analytics/id/{id}")
    public void deleteAnalyticById(@PathVariable String argId, @PathVariable String id) {
        argumentService.deleteAnalytic(id);
    }

}
