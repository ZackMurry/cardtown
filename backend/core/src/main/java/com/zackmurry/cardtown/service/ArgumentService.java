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
import com.zackmurry.cardtown.util.EncryptionUtils;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public String createArgument(@NonNull ArgumentCreateRequest request) {
        request.setOwnerId(((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());

        if (request.getName() == null) {
            throw new BadRequestException();
        }

        if (request.getName().length() > 128 || request.getName().length() < 1) {
            throw new LengthRequiredException("An argument's name must be between 1 and 128 characters long.");
        }

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        try {
            request.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        final UUID argId = argumentDao.createArgument(request);
        if (argId == null) {
            throw new InternalServerException();
        }

        if (request.getCardIds() != null && request.getCardIds().size() != 0) {
            final List<UUID> cardIds = request.getCardIds().stream().map(UUIDCompressor::decompress).collect(Collectors.toList());
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
            throw new ArgumentNotFoundException();
        }
        if (!argumentEntity.getOwnerId().equals(userId)) {
            // todo sharing
            throw new ForbiddenException();
        }
        try {
            argumentEntity.decryptFields(secretKey);
        } catch (Exception e) {
            // this happens if the encoding is invalid for some reason
            throw new InternalServerException();
        }
        final User owner = userService.getUserById(argumentEntity.getOwnerId()).orElse(null);
        if (owner == null) {
            throw new InternalServerException();
        }
        final List<ArgumentCardEntity> argumentCardEntities = argumentDao.getCardsByArgumentId(argumentEntity.getId());
        final List<ResponseCard> responseCards = cardService.getResponseCardsByIds(
                argumentCardEntities.stream()
                        .sorted(Comparator.comparingInt(ace -> (int) ace.getIndexInArgument())) // sort by index
                        .map(ArgumentCardEntity::getCardId) // get card  id
                        .collect(Collectors.toList())
        );
        return ResponseArgument.fromArgumentEntity(argumentEntity, ResponseUserDetails.fromUser(owner), responseCards);
    }

    public void addCardToArgument(UUID cardId, UUID argumentId) {
        final short index = argumentDao.getFirstOpenIndexInArgument(argumentId);
        final CardEntity cardEntity = cardService.getCardEntityById(cardId).orElse(null);
        if (cardEntity == null) {
            throw new CardNotFoundException();
        }
        final UUID principalId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!cardEntity.getOwnerId().equals(principalId)) {
            throw new ForbiddenException();
        }
        argumentDao.addCardToArgument(cardId, argumentId, index);
    }

    public void addCardToArgument(@NonNull String cardId, @NonNull String argumentId) {
        // todo check access to card
        final UUID decompressedCardId = UUIDCompressor.decompress(cardId);
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(decompressedArgId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UUID principalId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!argumentEntity.getOwnerId().equals(principalId)) {
            throw new ForbiddenException();
        }
        addCardToArgument(decompressedCardId, decompressedArgId);
    }

    public List<ArgumentPreview> listArgumentsByUser() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        final byte[] secretKey = principal.getSecretKey();
        final List<ArgumentEntity> argumentEntities = argumentDao.getArgumentsByUser(userId);
        final List<ArgumentPreview> argumentPreviews = new ArrayList<>();
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

    public void removeCardFromArgument(@NonNull UUID argumentId, @NonNull UUID cardId, short index) {
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(argumentId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UUID userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!argumentEntity.getOwnerId().equals(userId)) {
            throw new ForbiddenException();
        }
        argumentDao.removeCardFromArgument(argumentId, cardId, index);
    }

    public void removeCardFromArgument(@NonNull String argumentId, @NonNull String cardId, @NonNull short index) {
        // todo make it so that you can remove a card at a specific index instead of removing all appearances of that card in the arg
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final UUID decompressedCardId = UUIDCompressor.decompress(cardId);
        removeCardFromArgument(decompressedArgId, decompressedCardId, index);
    }

    /**
     * Deletes an argument (including the argument_cards data)
     * @param argumentId Id of argument to delete (in Base64)
     * @throws ArgumentNotFoundException If the argument could not be found
     * @throws ForbiddenException If the user does not have permission to delete the argument
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    public void deleteArgument(@NonNull String argumentId) {
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(decompressedArgId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UUID userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!argumentEntity.getOwnerId().equals(userId)) {
            throw new ForbiddenException();
        }
        argumentDao.deleteArgument(decompressedArgId);
    }

    /**
     * Renames an argument
     * @param argumentId Id of the argument to rename (in Base64)
     * @param newName New name of argument (in plain text)
     * @throws ArgumentNotFoundException If the argument could not be found
     * @throws ForbiddenException If the user does not have permission to rename the argument
     * @throws InternalServerException If an error occurs while encrypting the name
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     * @throws BadRequestException If the name is longer than 128 characters
     */
    public void renameArgument(@NonNull String argumentId, @NonNull String newName) {
        if (newName.length() > 128 || newName.length() < 1) {
            throw new BadRequestException();
        }
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(decompressedArgId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!argumentEntity.getOwnerId().equals(principal.getId())) {
            throw new ForbiddenException();
        }
        String encryptedName;
        try {
            encryptedName = EncryptionUtils.encryptStringAES(newName, principal.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        argumentDao.renameArgument(decompressedArgId, encryptedName);
    }

    /**
     * Reorders the cards in an argument according to a new list of ids
     * @param argumentId Id of argument to modify
     * @param newPositions List of new card ids, in order, with ids in Base64
     * @throws ArgumentNotFoundException If the argument could not be found
     * @throws ForbiddenException If the user does not have access to the requested argument
     * @throws BadRequestException If the new position list does not have the same number of cards as the argument currently does
     * @throws BadRequestException If the new position list contains either a card that is not in the argument or one card more than it appears in the argument
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void updateCardPositions(@NonNull String argumentId, @NonNull List<String> newPositions) {
        // todo unit test
        final UUID decompressedArgId = UUIDCompressor.decompress(argumentId);
        final ArgumentEntity argumentEntity = argumentDao.getArgumentEntity(decompressedArgId).orElse(null);
        if (argumentEntity == null) {
            throw new ArgumentNotFoundException();
        }
        final UUID userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!argumentEntity.getOwnerId().equals(userId)) {
            throw new ForbiddenException();
        }

        // validating that the cards are the same
        final List<ArgumentCardEntity> argumentCardEntities = argumentDao.getCardsByArgumentId(decompressedArgId);
        if (newPositions.size() != argumentCardEntities.size()) {
            throw new BadRequestException();
        }

        final List<UUID> remainingCardIds = argumentCardEntities.stream().map(ArgumentCardEntity::getCardId).collect(Collectors.toList());
        final List<UUID> decompressedNewPositions = newPositions.stream().map(UUIDCompressor::decompress).collect(Collectors.toList());

        for (UUID cardId : decompressedNewPositions) {
            if (!remainingCardIds.remove(cardId)) {
                throw new BadRequestException();
            }
        }

        // Finding the card indices that are different
        argumentCardEntities.sort(Comparator.comparingInt(ArgumentCardEntity::getIndexInArgument));
        for (int i = 0; i < decompressedNewPositions.size(); i++) {
            if (!decompressedNewPositions.get(i).equals(argumentCardEntities.get(i).getCardId())) {
                argumentDao.setCardIndexInArgumentUnchecked(decompressedArgId, decompressedNewPositions.get(i), (short) i);
            }
        }
    }

    /**
     * Removes all appearances of cards from arguments.
     * Checks authorization for accessing argument, but not the card
     * @param cardId Id of card to remove
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void removeCardFromAllArguments(@NonNull UUID cardId) {
        // todo this needs unit tests
        final List<ArgumentCardEntity> argumentCardEntities = argumentDao.getCardEntitiesByCardId(cardId);
        for (ArgumentCardEntity argumentCardEntity : argumentCardEntities) {
            removeCardFromArgument(argumentCardEntity.getArgumentId(), cardId, argumentCardEntity.getIndexInArgument());
        }
    }

}
