package com.zackmurry.cardtown.model.arg.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReorderCardsInArgumentRequest {

    private Short oldIndex;
    private Short newIndex;

}
