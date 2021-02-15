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
public class ArgumentCreateRequest extends EncryptedArgument {

    private UUID ownerId;
    private List<String> cardIds;
    // todo name should be unique in the scope of the user

    public ArgumentCreateRequest(List<String> cardIds, String name) {
        this.cardIds = cardIds;
        this.name = name;
    }

    public ArgumentCreateRequest(String name) {
        this.name = name;
    }

}
