package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.EncryptedCard;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardDao {

    /**
     * creates a card in the database with the details of the <code>CardEntity</code>
     * @param card details of the new card
     * @return the id of the new card
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is a <code>SQLException</code>
     */
    UUID createCard(CardEntity card);

    /**
     * gets a card from the database with the specified id
     * @param id id of card to get
     * @return if found: an optional containing the card; if not: <code>Optional.empty()</code>
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is a <code>SQLException</code>
     * @throws org.springframework.web.server.ResponseStatusException if there is a failed expectation within the server
     */
    Optional<CardEntity> getCardById(UUID id);

    /**
     * gets all of the cards that are by the user with the specified id
     * @param id id of user
     * @return a list of cards that the user owns
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is a <code>SQLException</code>
     */
    List<CardEntity> getCardsByUser(UUID id);

    /**
     * gets the number of cards that the user owns
     * @param id id of the user
     * @return the number of cards that the user owns
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is a <code>SQLException</code>
     */
    int getNumberOfCardsByUser(UUID id);

    /**
     * gets the id of the owner of a card
     * @param cardId id of the card
     * @return if found: an optional containing the id; if not found: <code>Optional.empty()</code>
     * @throws com.zackmurry.cardtown.exception.InternalServerException if a <code>SQLException</code> occurs
     */
    Optional<UUID> getOwnerIdByCardId(UUID cardId);

    /**
     * deletes a card with the given id
     * @param id id of card to delete
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is an <code>SQLException</code>
     * @throws com.zackmurry.cardtown.exception.CardNotFoundException if the card is not found
     */
    void deleteCardById(UUID id);

    /**
     * updates a card with the given id and details
     * @param id id of card to update
     * @param request new information for the card to contain
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is an <code>SQLException</code>
     */
    void updateCardById(UUID id, EncryptedCard request);

}
