package com.zackmurry.cardtown.model.arg.card;

import com.zackmurry.cardtown.model.arg.EncryptedArgument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentCardJoinEntity extends EncryptedArgument {

    private UUID id;
    private UUID ownerId;
    private UUID cardId;
    private short indexInArgument;

    public ArgumentCardJoinEntity(UUID id, UUID ownerId, String name, UUID cardId, short indexInArgument) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.cardId = cardId;
        this.indexInArgument = indexInArgument;
    }

}
