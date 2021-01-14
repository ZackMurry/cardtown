package com.zackmurry.cardtown.dao.arg;

import com.zackmurry.cardtown.model.arg.ArgumentCreateRequest;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.arg.ArgumentEntityWithCards;
import com.zackmurry.cardtown.model.arg.card.ArgumentCardEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArgumentDao {

    Optional<UUID> createArgument(ArgumentCreateRequest request);

    Optional<ArgumentEntity> getArgumentEntity(UUID id);

    List<ArgumentCardEntity> getCardsByArgumentId(UUID argumentId);

    /**
     * inserts a card into an argument and the specified index (0-based).
     * if a card already exists there, this pushes it (and other possible cards) to the next index
     * @param cardId card to add to argument. must already be in the database
     * @param argumentId argument to add to. must already be in the database
     * @param indexInArgument the desired zero-based index to set the card at
     * @throws IllegalArgumentException if the index would create a gap between two cards
     * @throws IllegalArgumentException if the index is negative
     * @throws com.zackmurry.cardtown.exception.InternalServerException if there is a <code>SQLException</code>
     */
    void addCardToArgument(UUID cardId, UUID argumentId, short indexInArgument);

    short getFirstOpenIndexInArgument(UUID id);

    List<ArgumentEntity> getArgumentsByUser(UUID id);

}
