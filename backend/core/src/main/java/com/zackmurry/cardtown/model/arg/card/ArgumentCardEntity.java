package com.zackmurry.cardtown.model.arg.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentCardEntity {

    private UUID argumentId;
    private UUID cardId;
    private short indexInArgument;

}
