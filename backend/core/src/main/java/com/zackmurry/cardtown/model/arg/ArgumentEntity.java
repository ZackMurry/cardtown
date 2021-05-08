package com.zackmurry.cardtown.model.arg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentEntity extends EncryptedArgument {

    private UUID id;
    private UUID ownerId;
    private boolean deleted;

    public ArgumentEntity(UUID id, UUID ownerId, String name, boolean deleted) {
        super(name);
        this.id = id;
        this.ownerId = ownerId;
        this.deleted = deleted;
    }

}
