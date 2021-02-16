package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.card.CardDao;
import com.zackmurry.cardtown.exception.*;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.User;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardCreateRequest;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.CardPreview;
import com.zackmurry.cardtown.model.card.ResponseCard;
import com.zackmurry.cardtown.util.HtmlSanitizer;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ArgumentService argumentService;

    @Autowired
    private CardDao cardDao;

    /**
     * Returns a <code>ResponseCard</code> by its id.
     * Checks if the principal has permission to access it.
     * @param id Id of card
     * @return A <code>ResponseCard</code> representing the card's data
     * @throws CardNotFoundException If the card could not be found
     * @throws ForbiddenException If the user doesn't have access to the requested card
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     * @throws InternalServerException If the owner of the card is not found in the users table
     */
    public ResponseCard getResponseCardById(UUID id) {
        final CardEntity cardEntity = cardDao.getCardById(id).orElse(null);
        if (cardEntity == null) {
            throw new CardNotFoundException();
        }

        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final UUID userId = principal.getId();
        if (!userId.equals(cardEntity.getOwnerId())) {
            // todo sharing
            throw new ForbiddenException();
        }

        try {
            cardEntity.decryptFields(principal.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        final User owner = userService.getUserById(userId).orElse(null);
        if (owner == null) {
            logger.warn("Owner of card not found in users database. Owner id: {}, card id: {}", userId, cardEntity.getId());
            throw new InternalServerException();
        }

        return ResponseCard.fromCard(cardEntity, ResponseUserDetails.fromUser(owner));
    }

    /**
     * A helper method for converting a Base64 card id to a UUID and then finding the associated <code>ResponseCard</code>.
     * This method also checks the principal's permission to the card before retrieving it
     * @see CardService#getResponseCardById(UUID) For the method that is called after converting the Base64 to a UUID
     * @param id Id of card in Base64
     * @return A <code>ResponseCard</code> representing the card's data
     * @throws CardNotFoundException If the card could not be found
     * @throws ForbiddenException If the user doesn't have access to the requested card
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     * @throws InternalServerException If the owner of the card is not found in the users table
     * @throws BadRequestException If the card has an invalid id
     */
    public ResponseCard getResponseCardById(@NonNull String id) {
        try {
            return getResponseCardById(UUIDCompressor.decompress(id));
        } catch (BufferUnderflowException e) {
            // This happens if there's an invalid UUID (too short)
            throw new BadRequestException();
        }
    }

    /**
     * Creates a card in the database and returns its id in Base64.
     * If the card's tag or cite information are null, this will replace them with an empty <code>String</code>.
     * This method also sanitizes the HTML found in the request's bodyHtml
     * @see HtmlSanitizer#sanitizeHtml(String) For details about sanitization
     * @param request Info of the card to create
     * @return New card's id in Base64
     * @throws BadRequestException If any of the following are null: ownerEmail, bodyHtml, bodyDraft, bodyText, cite
     * @throws ResponseStatusException (Length required) If tag is more than 256 chars, cite is more than 128 chars, or cite information is more than 2048 chars
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public String createCard(@NonNull CardCreateRequest request) {
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

        // todo: impose some limit on length of body fields
        if (request.getTag().length() > 256 || request.getCite().length() > 128 || request.getCiteInformation().length() > 2048) {
            throw new LengthRequiredException();
        }

        // whitelisting html tags to prevent XSS
        request.setBodyHtml(HtmlSanitizer.sanitizeHtml(request.getBodyHtml()));

        final Optional<UUID> optionalUserId = userService.getIdByEmail(request.getOwnerEmail());
        if (optionalUserId.isEmpty()) {
            throw new InternalServerException();
        }

        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();
        final CardEntity cardEntity = request.toCardEntity(optionalUserId.get());
        try {
            cardEntity.encryptFields(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        final UUID cardId = cardDao.createCard(cardEntity);
        return UUIDCompressor.compress(cardId);
    }

    /**
     * Gets a list of (decrypted) <code>ResponseCard</code>s representing the principal's cards.
     * @return The principal's cards
     * @throws InternalServerException If there is an error decrypting the cards
     * @throws InternalServerException If a card's owner cannot be found in the users table
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public List<ResponseCard> getAllCardsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final List<CardEntity> rawCards = cardDao.getCardsByUser(id);
        final byte[] secretKey = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecretKey();

        try {
            for (CardEntity c : rawCards) {
                c.decryptFields(secretKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Returns the amount of cards that the principal has access to. Does not check if the user exists
     * @return The number of cards the principal has access to
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public int getNumberOfCardsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return cardDao.getNumberOfCardsByUser(id);
    }

    /**
     * Deletes a card by its id. Before deleting, it checks if the principal has permission to delete the card.
     * Before deleting, it checks for appearances in arguments and removes it from them, first
     * @param id Id of card in Base64
     * @throws CardNotFoundException If the card could not be found
     * @throws ForbiddenException If the principal doesn't have permission to delete the card
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void deleteCardById(String id) {
        // todo probably make this internally call another method that takes a UUID param
        final UUID principalId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final UUID cardId = UUIDCompressor.decompress(id);
        final UUID ownerId = cardDao.getOwnerIdByCardId(cardId).orElse(null);
        if (ownerId == null) {
            throw new CardNotFoundException();
        }

        if (!principalId.equals(ownerId)) {
            throw new ForbiddenException();
        }
        argumentService.removeCardFromAllArguments(cardId);
        cardDao.deleteCardById(cardId);
    }

    /**
     * Updates a card by its id. First checks if the principal has permission to edit the card. The <code>request</code> parameter must
     * contain the full new card -- not just a partial card (except for tag and citeInformation, which will be replaced with an empty string
     * if null). This method will also sanitize the incoming HTML
     * @see HtmlSanitizer#sanitizeHtml(String) For the HTML sanitization method
     * @param id The card's id in Base64
     * @param request New information for card to have
     * @throws BadRequestException If any of the following fields of <code>request</code> are null:
     * bodyHtml, bodyText, bodyDraft, cite
     * @throws ResponseStatusException (Length required) If tag is more than 256 chars, cite is more than 128 chars, or cite information is more than 2048 chars
     * @throws CardNotFoundException If the card could not be found
     * @throws ForbiddenException If the principal doesn't have permission to edit the requested card
     * @throws InternalServerException If there is an error while encrypting the new card data
     */
    public void updateCardById(String id, CardCreateRequest request) {
        // todo extract this stuff into a validation method
        if (request.getBodyHtml() == null ||
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
            throw new LengthRequiredException();
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

    /**
     * Finds the response cards of a list of ids. Does not preserve order. Does not check if the principal has access to them
     * @param ids Ids of cards to find
     * @return A list of <code>ResponseCard</code>s that have the requested ids
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public List<ResponseCard> getResponseCardsByIds(List<UUID> ids) {
        final List<ResponseCard> responseCards = new ArrayList<>();
        for (UUID id : ids) {
            responseCards.add(getResponseCardById(id));
        }
        return responseCards;
    }

    /**
     * Gets the (decrypted) <code>CardPreview</code>s of all of the cards that the user has access to
     * @return The <code>CardPreview</code>s that the user has access to
     * @throws InternalServerException If an error occurs while decrypting the cards
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
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

    /**
     * Gets a <code>CardEntity</code> by its id (in UUID form)
     * @param cardId Id of card to find
     * @return If card is found: an <code>Optional</code> containing the request card; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public Optional<CardEntity> getCardEntityById(@NonNull UUID cardId) {
        return cardDao.getCardById(cardId);
    }

}
