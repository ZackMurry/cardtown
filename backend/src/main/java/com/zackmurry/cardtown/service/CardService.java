package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.card.CardDao;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.HtmlUtils;
import com.zackmurry.cardtown.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private UserService userService;

    @Autowired
    private CardDao cardDao;

    public ResponseEntity<ResponseCard> getResponseCardById(String id) {
        if (id == null) {
            System.out.println("null id");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UUID uuid;
        try {
            uuid = UUIDUtils.decompress(id);
        } catch (BufferUnderflowException e) {
            // this happens if there's an invalid UUID (too short)
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }

        Optional<CardEntity> optionalCard = cardDao.getCardById(uuid);
        if (optionalCard.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CardEntity cardEntity = optionalCard.get();

        String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UUID> optionalUserId = userService.getIdByEmail(principalEmail);
        if (optionalUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        UUID userId = optionalUserId.get();
        if (!userId.equals(cardEntity.getOwnerId())) {
            // todo sharing
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // todo probably switch owner_id to the owner name (and eventually profile picture and such)

        try {
            final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
            cardEntity.decryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ResponseCard responseCard = ResponseCard.fromCard(cardEntity, id);
        return new ResponseEntity<>(responseCard, HttpStatus.OK);
    }

    /**
     * creates a card in the database and returns its shortened id
     * @param request info of card to create
     * @return new card's shortened id or a fail value
     */
    public ResponseEntity<String> createCard(CardCreateRequest request) {
        if (request.getOwnerEmail() == null ||
            request.getBodyHtml() == null ||
            request.getBodyDraft() == null ||
            request.getCite() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getTag() == null) {
            request.setTag("");
        }

        if (request.getCiteInformation() == null) {
            request.setCiteInformation("");
        }

        if (request.getTag().length() > 256 || request.getCite().length() > 128 || request.getCiteInformation().length() > 2048) {
            return new ResponseEntity<>(HttpStatus.LENGTH_REQUIRED);
        }

        // sanitizing script tags
        request.setBodyHtml(HtmlUtils.removeScriptTags(request.getBodyHtml()));

        // todo check lengths
        Optional<UUID> optionalUserId = userService.getIdByEmail(request.getOwnerEmail());
        if (optionalUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        // todo probably switch secret key storage in model to bytes
        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        final CardEntity cardEntity = request.toCardEntity(optionalUserId.get());
        try {
            cardEntity.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<UUID> optionalCardId = cardDao.createCard(cardEntity);
        if (optionalCardId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String shortenedCardId = UUIDUtils.compress(optionalCardId.get());
        return new ResponseEntity<>(shortenedCardId, HttpStatus.OK);
    }

    public ResponseEntity<List<ResponseCard>> getAllCardsByUser() {
        final String email = SecurityContextHolder.getContext().getAuthentication().getName();
        final UUID id = userService.getIdByEmail(email).orElse(null);
        if (id == null) {
            // probably won't happen, since the user has to be in the database to be authenticated
            return new ResponseEntity<>(HttpStatus.GONE);
        }
        List<CardEntity> rawCards = cardDao.getCardsByUser(id);
        if (rawCards == null) {
            // will happen if there's an SQLException
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();

        try {
            for (CardEntity c : rawCards) {
                c.decryptFields(secretKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // not sure if this would be the server's or user's fault tbh
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final List<ResponseCard> responseCards = rawCards.stream().map(ResponseCard::fromCard).collect(Collectors.toList());

        return new ResponseEntity<>(responseCards, HttpStatus.OK);
    }
}
