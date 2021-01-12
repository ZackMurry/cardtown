package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.arg.ArgumentDao;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.ResponseArgument;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArgumentService {

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
        return UUIDCompressor.compress(argId);
    }

    public ResponseArgument getResponseArgumentById(@NonNull String id) {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        final byte[] secretKey = principal.getSecretKey();

        final UUID uuidId = UUIDCompressor.decompress(id);
        final ArgumentEntity argumentEntity = argumentDao.getArgument(uuidId).orElse(null);
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
        argumentDao.addCardToArgument(cardId, argumentId);
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

}
