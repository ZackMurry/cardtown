package com.zackmurry.cardtown.dao.card;

import com.zackmurry.cardtown.model.card.CardEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardDao {

    Optional<UUID> createCard(CardEntity card);

    Optional<CardEntity> getCardById(UUID id);

    List<CardEntity> getCardsByUser(UUID id);

    ResponseEntity<Integer> getNumberOfCardsByUser(UUID id);

}
