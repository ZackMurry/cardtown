package com.zackmurry.cardtown.model.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionArgumentPreview {

    private String id;
    private String name;
    private int numCards;
    // todo number of speeches this appears in

}
