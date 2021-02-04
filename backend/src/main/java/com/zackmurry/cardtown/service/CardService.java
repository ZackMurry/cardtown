package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.card.CardDao;
import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.exception.CardNotFoundException;
import com.zackmurry.cardtown.exception.ForbiddenException;
import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.*;
import com.zackmurry.cardtown.util.HtmlSanitizer;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.BufferUnderflowException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CardDao cardDao;

    public ResponseCard getResponseCardById(UUID id) {
        CardEntity cardEntity = cardDao.getCardById(id).orElse(null);
        if (cardEntity == null) {
            throw new CardNotFoundException();
        }

        String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UUID> optionalUserId = userService.getIdByEmail(principalEmail);
        if (optionalUserId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
        UUID userId = optionalUserId.get();
        if (!userId.equals(cardEntity.getOwnerId())) {
            // todo sharing
            throw new ForbiddenException();
        }

        try {
            final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
            cardEntity.decryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        User owner = userService.getUserById(userId).orElse(null);
        if (owner == null) {
            logger.warn("Owner of card not found in users database. Owner id: {}, card id: {}", userId, cardEntity.getId());
            throw new InternalServerException();
        }

        return ResponseCard.fromCard(cardEntity, ResponseUserDetails.fromUser(owner));
    }

    public ResponseCard getResponseCardById(@NonNull String id) {
        try {
            return getResponseCardById(UUIDCompressor.decompress(id));
        } catch (BufferUnderflowException e) {
            // this happens if there's an invalid UUID (too short)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * creates a card in the database and returns its shortened id
     * @param request info of card to create
     * @return new card's shortened id or a fail value
     */
    public String createCard(CardCreateRequest request) {
        if (request.getOwnerEmail() == null ||
            request.getBodyHtml() == null ||
            request.getBodyDraft() == null ||
            request.getCite() == null ||
            request.getBodyText() == null) {
            throw new BadRequestException();
        }
        if (request.getTag() == null) {
            request.setTag("");
        }

        if (request.getCiteInformation() == null) {
            request.setCiteInformation("");
        }

        if (request.getTag().length() > 256 || request.getCite().length() > 128 || request.getCiteInformation().length() > 2048) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        }

        // whitelisting html tags to prevent XSS
        request.setBodyHtml(HtmlSanitizer.sanitizeHtml(request.getBodyHtml()));

        Optional<UUID> optionalUserId = userService.getIdByEmail(request.getOwnerEmail());
        if (optionalUserId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        final CardEntity cardEntity = request.toCardEntity(optionalUserId.get());
        try {
            cardEntity.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        UUID cardId = cardDao.createCard(cardEntity);
        return UUIDCompressor.compress(cardId);
    }

    public List<ResponseCard> getAllCardsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<CardEntity> rawCards = cardDao.getCardsByUser(id);
        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();

        try {
            for (CardEntity c : rawCards) {
                c.decryptFields(secretKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // not sure if this would be the server's or user's fault tbh
            throw new InternalServerException();
        }
        // probably use a HashMap<UUID, ResponseUserDetails> when i add sharing to greedily keep track of user details by UUID
        final User userEntity = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userEntity == null) {
            logger.warn("Author of card not found in database -- author id: {}", id);
            throw new InternalServerException();
        }
        final ResponseUserDetails resUserDetails = ResponseUserDetails.fromUser(userEntity);
        return rawCards.stream().map(c -> ResponseCard.fromCard(c, resUserDetails)).collect(Collectors.toList());
    }

    public int getNumberOfCardsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return cardDao.getNumberOfCardsByUser(id);
    }

    public void deleteCardById(String id) {
        final UUID principalId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final UUID cardId = UUIDCompressor.decompress(id);
        final UUID ownerId = cardDao.getOwnerIdByCardId(cardId).orElse(null);
        if (ownerId == null) {
            throw new CardNotFoundException();
        }

        if (!principalId.equals(ownerId)) {
            throw new ForbiddenException();
        }
        cardDao.deleteCardById(cardId);
    }

    public void updateCardById(String id, CardCreateRequest request) {
        if (request.getBodyHtml() == null ||
            request.getBodyDraft() == null ||
            request.getCite() == null ||
            request.getBodyText() == null) {
            throw new BadRequestException();
        }
        if (request.getTag().length() > 256 || request.getCite().length() > 128 || request.getCiteInformation().length() > 2048) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED);
        }

        final UUID principalId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final UUID cardId = UUIDCompressor.decompress(id);
        final UUID ownerId = cardDao.getOwnerIdByCardId(cardId).orElse(null);
        if (ownerId == null) {
            throw new CardNotFoundException();
        }

        if (!ownerId.equals(principalId)) {
            throw new ForbiddenException();
        }
        // whitelisting html tags to prevent XSS
        request.setBodyHtml(HtmlSanitizer.sanitizeHtml(request.getBodyHtml()));

        final CardEntity cardEntity = new CardEntity(
                ownerId,
                request.getTag(),
                request.getCite(),
                request.getCiteInformation(),
                request.getBodyHtml(),
                request.getBodyDraft(),
                request.getBodyText()
        );

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        try {
            cardEntity.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        cardDao.updateCardById(cardId, cardEntity);
    }

    public List<ResponseCard> getResponseCardsByIds(List<UUID> ids) {
        List<ResponseCard> responseCards = new ArrayList<>();
        for (UUID id : ids) {
            responseCards.add(getResponseCardById(id));
        }
        return responseCards;
    }

    public List<CardPreview> getCardPreviewsByUser() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<CardEntity> rawCards = cardDao.getCardsByUser(principal.getId());
        final ResponseUserDetails userDetails = ResponseUserDetails.fromUser(principal);
        final byte[] secretKey = principal.getSecretKey();
        final List<CardPreview> cardPreviews = new ArrayList<>();
        for (CardEntity c : rawCards) {
            final CardPreview cardPreview = CardPreview.of(c, userDetails);
            try {
                cardPreview.decryptFields(secretKey);
            } catch (Exception e) {
                throw new InternalServerException();
            }
            cardPreviews.add(cardPreview);
        }
        return cardPreviews;
    }

    public Optional<CardEntity> getCardEntityById(@NonNull UUID cardId) {
        return cardDao.getCardById(cardId);
    }

}
