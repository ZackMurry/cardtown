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
public class ArgumentCreateRequest extends EncryptedArgument {

    private UUID ownerId;
    // todo name should be unique in the scope of the user

}
