package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.EncryptedCard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardDao {

    /**
     * Creates a card in the database with the details of the <code>CardEntity</code>
     *
     * @param card Details of the new card
     * @return The id of the new card
     * @throws InternalServerException if there is a <code>SQLException</code>
     */
    UUID createCard(CardEntity card);

    /**
     * Gets a card from the database with the specified id. Ignores deleted cards.
     *
     * @param id Id of card to get
     * @return If found: an optional containing the card; if not: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<CardEntity> getCardById(UUID id);

    /**
     * Gets a card from the database with the specified id.
     *
     * @param id Id of card to get
     * @param includeDeleted Whether deleted cards should be included in the results
     * @return If found: an optional containing the card; if not: <code>Optional.empty()</code>
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    Optional<CardEntity> getCardById(UUID id, boolean includeDeleted);

    /**
     * Gets all of the cards that are by the user with the specified id
     *
     * @param id Id of user
     * @return A list of cards that the user owns
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    List<CardEntity> getCardsByUser(UUID id);

    /**
     * Gets the number of cards that the user owns
     *
     * @param id Id of the user
     * @return The number of cards that the user owns
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    int getNumberOfCardsByUser(UUID id);

    /**
     * Gets the id of the owner of a card
     *
     * @param cardId Id of the card
     * @return If found: an optional containing the id; if not found: <code>Optional.empty()</code>
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    Optional<UUID> getOwnerIdByCardId(UUID cardId);

    /**
     * Marks a card as deleted
     * todo add option to permanently delete card and a way to restore it
     *
     * @param cardId Id of card to delete
     * @throws InternalServerException                                If there is an <code>SQLException</code>
     * @throws com.zackmurry.cardtown.exception.CardNotFoundException If the card is not found
     */
    void markCardAsDeleted(UUID cardId);

    /**
     * Updates a card with the given id and details. Also updates last_modified to the current time
     *
     * @param id      Id of card to update
     * @param request New information for the card to contain
     * @throws InternalServerException If there is an <code>SQLException</code>
     */
    void updateCardById(UUID id, EncryptedCard request);

    /**
     * Gets cards with an owner in a team
     *
     * @param teamId Id of team to find cards of
     * @return List of cards associated with team
     * @throws InternalServerException If a <code>SQLException</code> occurs
     */
    List<CardEntity> getCardsByTeamId(UUID teamId);

}
