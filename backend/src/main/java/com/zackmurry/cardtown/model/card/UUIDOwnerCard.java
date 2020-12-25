package com.zackmurry.cardtown.model.card;

import java.util.UUID;

/**
 * an interface for abstracting the id type of the card away
 */
public interface UUIDOwnerCard extends BasicCard {

    UUID getOwnerId();

}
