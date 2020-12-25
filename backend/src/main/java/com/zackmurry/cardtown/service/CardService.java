package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.card.CardDao;
import com.zackmurry.cardtown.dao.user.UserDao;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CardService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CardDao cardDao;

    public ResponseEntity<ResponseCard> getResponseCardById(String id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UUID uuid = UUIDUtils.decompress(id);
        Optional<CardEntity> optionalCard = cardDao.getCardById(uuid);
        if (optionalCard.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        CardEntity cardEntity = optionalCard.get();

        String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UUID> optionalUserId = userDao.getIdByEmail(principalEmail);
        if (optionalUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        UUID userId = optionalUserId.get();
        if (!userId.equals(cardEntity.getOwnerId())) {
            // todo probably the biggest thing that will be missing from the beta is sharing.
            // would like to test the core application before i have to deal with that
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // todo probably switch owner_id to the owner name (and eventually profile picture and such)
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
            request.getBody() == null ||
            request.getCite() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getTag() == null) {
            request.setTag("");
        }
        if (request.getCiteInformation() == null) {
            request.setCiteInformation("");
        }
        Optional<UUID> optionalUserId = userDao.getIdByEmail(request.getOwnerEmail());
        if (optionalUserId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
        Optional<UUID> optionalCardId = cardDao.createCard(request.toCardEntity(optionalUserId.get()));
        if (optionalCardId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String shortenedCardId = UUIDUtils.compress(optionalCardId.get());
        return new ResponseEntity<>(shortenedCardId, HttpStatus.OK);
    }
}
