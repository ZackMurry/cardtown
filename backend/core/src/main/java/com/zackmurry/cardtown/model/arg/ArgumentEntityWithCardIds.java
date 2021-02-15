package com.zackmurry.cardtown.model.arg;

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
public class ArgumentEntityWithCardIds extends EncryptedArgument {

    private UUID id;
    private UUID ownerId;
    private List<UUID> cardIds;

    public ArgumentEntityWithCardIds(UUID id, UUID ownerId, String name, List<UUID> cardIds) {
        super(name);
        this.id = id;
        this.ownerId = ownerId;
        this.cardIds = cardIds;
    }

}
