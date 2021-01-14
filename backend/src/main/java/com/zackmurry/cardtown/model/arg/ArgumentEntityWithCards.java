package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.card.CardEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentEntityWithCards extends EncryptedArgument {

    private UUID id;
    private UUID ownerId;
    private List<CardEntity> cards;

    public ArgumentEntityWithCards(UUID id, UUID ownerId, String name, List<CardEntity> cards) {
        super(name);
        this.id = id;
        this.ownerId = ownerId;
        this.cards = cards;
    }

    public static ArgumentEntityWithCards fromArgumentEntity(ArgumentEntity entity, List<CardEntity> cards) {
        return new ArgumentEntityWithCards(entity.getId(), entity.getOwnerId(), entity.getName(), cards);
    }

}
