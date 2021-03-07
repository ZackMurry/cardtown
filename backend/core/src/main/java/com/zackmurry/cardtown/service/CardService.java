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
import com.zackmurry.cardtown.model.team.TeamEntity;
import com.zackmurry.cardtown.util.HtmlSanitizer;
import com.zackmurry.cardtown.util.UUIDCompressor;
import com.zackmurry.cardtown.util.UserSecretKeyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ArgumentService argumentService;

    @Autowired
    private CardDao cardDao;

    @Autowired
    private TeamService teamService;

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
        final UUID ownerId = cardEntity.getOwnerId();
        if (!teamService.usersInSameTeam(userId, ownerId)) {
            throw new ForbiddenException();
        }

        try {
            cardEntity.decryptFields(UserSecretKeyHolder.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        final User owner = userService.getUserById(ownerId).orElse(null);
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
        request.validateFields();

        // whitelisting html tags to prevent XSS
        request.setBodyHtml(HtmlSanitizer.sanitizeHtml(request.getBodyHtml()));

        final Optional<UUID> optionalUserId = userService.getIdByEmail(request.getOwnerEmail());
        if (optionalUserId.isEmpty()) {
            throw new InternalServerException();
        }

        final CardEntity cardEntity = request.toCardEntity(optionalUserId.get());
        try {
            cardEntity.encryptFields(UserSecretKeyHolder.getSecretKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
        final UUID cardId = cardDao.createCard(cardEntity);
        return UUIDCompressor.compress(cardId);
    }

    /**
     * Gets cards that the user owns (excludes cards owned by other team members)
     * @return <code>ResponseCard</code>s representing the user's cards
     * @throws InternalServerException If an error occurs while decrypting the cards
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public List<ResponseCard> getCardsOwnedByUser() {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<CardEntity> rawCards = cardDao.getCardsByUser(principal.getId());
        final byte[] secretKey = UserSecretKeyHolder.getSecretKey();
        try {
            for (CardEntity c : rawCards) {
                c.decryptFields(secretKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }
        final ResponseUserDetails resUserDetails = ResponseUserDetails.fromUser(principal);
        return rawCards.stream().map(c -> ResponseCard.fromCard(c, resUserDetails)).collect(Collectors.toList());
    }

    /**
     * Gets a list of <code>ResponseCard</code>s representing the principal's cards (and those of their team)
     * @return The principal's cards
     * @throws InternalServerException If there is an error decrypting the cards
     * @throws InternalServerException If a card's owner cannot be found in the users table
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     * @see CardService#getCardsOwnedByUser() If the user is not in a team, this is called
     */
    public List<ResponseCard> getAllCardsVisibleToUser() {
        final Optional<TeamEntity> userTeam = teamService.getTeamOfUser();
        if (userTeam.isEmpty()) {
            return getCardsOwnedByUser();
        }
        final List<CardEntity> rawCards = cardDao.getCardsByTeamId(userTeam.get().getId());

        final byte[] secretKey = UserSecretKeyHolder.getSecretKey();
        try {
            for (CardEntity c : rawCards) {
                c.decryptFields(secretKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException();
        }

        // Get ResponseUserDetails for each CardEntity and map the cards to ResponseCards
        final List<ResponseCard> responseCards = new ArrayList<>();
        // This map is to reduce the amount of times the database is queried
        // Every owner will be fetched exactly once
        final Map<UUID, ResponseUserDetails> userDetailsMap = new HashMap<>();
        for (CardEntity c : rawCards) {
            final ResponseUserDetails responseUserDetails;
            if (userDetailsMap.containsKey(c.getOwnerId())) {
                responseUserDetails = userDetailsMap.get(c.getOwnerId());
            } else {
                final User ownerEntity = userService.getUserById(c.getOwnerId()).orElse(null);
                if (ownerEntity == null) {
                    logger.warn("Owner of card not found in database -- owner id: {}", c.getOwnerId());
                    throw new InternalServerException();
                }
                responseUserDetails = ResponseUserDetails.fromUser(ownerEntity);
                userDetailsMap.put(c.getOwnerId(), responseUserDetails);
            }
            responseCards.add(ResponseCard.fromCard(c, responseUserDetails));
        }
        return responseCards;
    }

    /**
     * Returns the amount of cards that the principal has access to. Does not check if the user exists
     * @return The number of cards the principal has access to
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public int getNumberOfCardsByUser() {
        final UUID id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return cardDao.getNumberOfCardsByUser(id); // todo include team cards
    }

    /**
     * Deletes a card by its id. Before deleting, it checks if the principal has permission to delete the card and
     * for appearances in arguments and removes it from them
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

        if (!teamService.usersInSameTeam(principalId, ownerId)) {
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
    public void updateCardById(@NonNull String id, @NonNull CardCreateRequest request) {
        request.validateFields();
        final UUID principalId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        final UUID cardId = UUIDCompressor.decompress(id);
        final UUID ownerId = cardDao.getOwnerIdByCardId(cardId).orElse(null);
        if (ownerId == null) {
            throw new CardNotFoundException();
        }

        if (!teamService.usersInSameTeam(principalId, ownerId)) {
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

        try {
            cardEntity.encryptFields(UserSecretKeyHolder.getSecretKey());
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
        final byte[] secretKey = UserSecretKeyHolder.getSecretKey();
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
     * Gets an encrypted <code>CardEntity</code> by its id (in UUID form)
     * @param cardId Id of card to find
     * @return If card is found: an <code>Optional</code> containing the request card; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public Optional<CardEntity> getCardEntityById(@NonNull UUID cardId) {
        return cardDao.getCardById(cardId);
    }

}
