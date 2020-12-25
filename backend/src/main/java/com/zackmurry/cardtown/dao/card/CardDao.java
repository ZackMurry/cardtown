package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.model.card.CardEntity;
import com.zackmurry.cardtown.model.card.UUIDOwnerCard;

import java.util.Optional;
import java.util.UUID;

public interface CardDao {

    Optional<UUID> createCard(UUIDOwnerCard card);

    Optional<CardEntity> getCardById(UUID id);

}
