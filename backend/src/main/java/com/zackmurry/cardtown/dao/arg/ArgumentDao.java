package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArgumentDao {

    /**
     * Creates an argument with the specified details
     * @param request Argument to create
     * @return Uuid of the new argument
     * @throws InternalServerException if no new rows are created in the database
     * @throws InternalServerException if there is a <code>SQLException</code>
     */
    UUID createArgument(ArgumentCreateRequest request);

    /**
     * Gets an argument entity from the database
     * @param id Id of the argument
     * @return An <code>Optional</code> of the requested argument
     * @throws InternalServerException if there is a <code>SQLException</code>
     */
    Optional<ArgumentEntity> getArgumentEntity(UUID id);

    /**
     * Gets cards from an argument with the given id
     * @param argumentId id of the argument
     * @return A list of cards found in the argument
     * @throws InternalServerException if there is a <code>SQLException</code>
     */
    List<ArgumentCardEntity> getCardsByArgumentId(UUID argumentId);

    /**
     * Inserts a card into an argument and the specified index (0-based).
     * If a card already exists there, this pushes it (and other possible cards) to the next index
     * @param cardId Card to add to argument. Must already be in the database
     * @param argumentId Argument to add to. Must already be in the database
     * @param indexInArgument The desired zero-based index to set the card at
     * @throws IllegalArgumentException If the index would create a gap between two cards
     * @throws IllegalArgumentException If the index is negative
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    void addCardToArgument(UUID cardId, UUID argumentId, short indexInArgument);

    /**
     * Gets the first unused position in an argument.
     * Does not check if the argument exists; returns zero if no cards are found (assumes the argument has no cards)
     * @param id Id of argument to search
     * @return A zero-based short representing the index
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    short getFirstOpenIndexInArgument(UUID id);

    /**
     * Gets all arguments that are owned by a user
     * @param id User to find arguments for
     * @return Data about the arguments (not the cards in them)
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    List<ArgumentEntity> getArgumentsByUser(UUID id);

    /**
     * Gets the number of arguments that are owned by a user
     * @param id User to find argument count for
     * @return Number of arguments owned by the user
     * @throws InternalServerException If there is a <code>SQLException</code> or no count is returned from the query
     */
    int getNumberOfArgumentsByUser(UUID id);

    /**
     * Gets the number of cards in an argument
     * @param argumentId Id of argument to find number of cards for
     * @return Number of cards in the argument
     * @throws InternalServerException If there is a <code>SQLException</code> or no count is returned from the query
     */
    int getNumberOfCardsInArgument(UUID argumentId);

    /**
     * Removes a card from an argument and left-shifts other cards' indices
     * @param argumentId Id of the argument that the card belongs to
     * @param cardId Id of the card to remove
     * @throws com.zackmurry.cardtown.exception.CardNotFoundException If the card could not be found in the argument
     * @throws InternalServerException If there is a <code>SQLException</code>
     */
    void removeCardFromArgument(UUID argumentId, UUID cardId);

    /**
     * Gets the position of a card in an argument
     * @param argumentId Id of argument to search in
     * @param cardId Card to find index for
     * @return The (0-based) position of the card in the argument
     * @throws com.zackmurry.cardtown.exception.CardNotFoundException If the card could not be found in the argument
     */
    short getIndexOfCardInArgument(UUID argumentId, UUID cardId);

}
