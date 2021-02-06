package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.arg.ArgumentDao;
import com.zackmurry.cardtown.exception.*;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.ArgumentPreview;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.CardHeader;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArgumentService {

    private static final Logger logger = LoggerFactory.getLogger(ArgumentService.class);

    @Autowired
    private ArgumentDao argumentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    public String createArgument(ArgumentCreateRequest request) {
        if (request == null || request.getName() == null || request.getOwnerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (request.getName().length() > 128) {
            throw new ResponseStatusException(HttpStatus.LENGTH_REQUIRED, "An argument's name must be <= 128 characters.");
        }

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        try {
            request.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        final UUID argId = argumentDao.createArgument(request).orElse(null);
        if (argId == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (request.getCardIds() != null) {
            List<UUID> cardIds = request.getCardIds().stream().map(UUIDCompressor::decompress).collect(Collectors.toList());
            for (UUID cardId : cardIds) {
                addCardToArgument(cardId, argId);
            }
        }
        return UUIDCompressor.compress(argId);
    }

    public ResponseArgument getResponseArgumentById(@NonNull String id) {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        final byte[] secretKey = principal.getSecretKey();

        final UUID uuidId = UUIDCompressor.decompress(id);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(uuidId).orElse(null);
        if (argumentEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!argumentEntity.getOwnerId().equals(userId)) {
            // todo sharing
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        try {
            argumentEntity.decryptFields(secretKey);
        } catch (Exception e) {
            // this happens if the encoding is invalid for some reason
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final User owner = userService.getUserById(argumentEntity.getOwnerId()).orElse(null);
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.GONE);
        }
        final List<ArgumentCardEntity> argumentCardEntities = argumentDao.getCardsByArgumentId(argumentEntity.getId());
        List<ResponseCard> responseCards = cardService.getResponseCardsByIds(argumentCardEntities.stream().map(ArgumentCardEntity::getCardId).collect(Collectors.toList()));
        return ResponseArgument.fromArgumentEntity(argumentEntity, ResponseUserDetails.fromUser(owner), responseCards);
    }

    public void addCardToArgument(UUID cardId, UUID argumentId) {
        short index = argumentDao.getFirstOpenIndexInArgument(argumentId);
        addCardToArgument(cardId, argumentId, index);
    }

    public void addCardToArgument(UUID cardId, UUID argumentId, short indexInArgument) {
        argumentDao.addCardToArgument(cardId, argumentId, indexInArgument);
    }

    public void addCardToArgument(String cardId, String argumentId) {
        // todo check access to card
        final UUID decompressedCardId = UUIDCompressor.decompress(cardId);
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        addCardToArgument(decompressedCardId, decompressedArgId);
    }

    public List<ArgumentPreview> listArgumentsByUser() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        final byte[] secretKey = principal.getSecretKey();
        List<ArgumentEntity> argumentEntities = argumentDao.getArgumentsByUser(userId);
        List<ArgumentPreview> argumentPreviews = new ArrayList<>();
        for (ArgumentEntity argumentEntity : argumentEntities) {
            try {
                argumentEntity.decryptFields(secretKey);
            } catch (Exception e) {
                // probably something wrong with the card
                e.printStackTrace();
                throw new BadRequestException();
            }

            final List<CardEntity> cardEntities = getCardsInArgument(argumentEntity.getId());
            final List<CardHeader> cardHeaders = new ArrayList<>();
            for (CardEntity cardEntity : cardEntities) {
                // todo greedily fetch ResponseUserDetails when sharing is implemented
                final ResponseUserDetails responseUserDetails = userService.getResponseUserDetailsById(cardEntity.getOwnerId()).orElse(null);
                if (responseUserDetails == null) {
                    throw new BadRequestException();
                }
                final ResponseCard responseCard = ResponseCard.fromCard(cardEntity, responseUserDetails);

                final CardHeader cardHeader = CardHeader.of(responseCard);
                try {
                    cardHeader.decryptFields(secretKey);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new InternalServerException();
                }
                cardHeaders.add(cardHeader);
            }

            final ResponseUserDetails responseUserDetails = userService.getResponseUserDetailsById(argumentEntity.getOwnerId()).orElse(null);
            if (responseUserDetails == null) {
                throw new BadRequestException();
            }
            final ArgumentPreview argumentPreview = ArgumentPreview.of(argumentEntity, responseUserDetails, cardHeaders);
            argumentPreviews.add(argumentPreview);
        }
        return argumentPreviews;
    }

    public int getNumberOfArgsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return argumentDao.getNumberOfArgumentsByUser(id);
    }

    /**
     * does not validate credentials!
     * @param argumentId id of argument to search
     * @return list of cards in argument
     */
    public List<CardEntity> getCardsInArgument(UUID argumentId) {
        final List<ArgumentCardEntity> argumentCardEntities = argumentDao.getCardsByArgumentId(argumentId);
        final List<CardEntity> cardEntities = new ArrayList<>();
        for (ArgumentCardEntity argumentCardEntity : argumentCardEntities) {
            final Optional<CardEntity> optionalCardEntity = cardService.getCardEntityById(argumentCardEntity.getCardId());
            if (optionalCardEntity.isEmpty()) {
                logger.warn("Card found in argument_cards but not found in cards");
                continue;
            }
            cardEntities.add(optionalCardEntity.get());
        }
        return cardEntities;
    }

    public void removeCardFromArgument(@NonNull String argumentId, @NonNull String cardId) {
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(decompressedArgId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UUID userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!argumentEntity.getOwnerId().equals(userId)) {
            throw new ForbiddenException();
        }
        final UUID decompressedCardId = UUIDCompressor.decompress(cardId);
        argumentDao.removeCardFromArgument(decompressedArgId, decompressedCardId);
    }
}
