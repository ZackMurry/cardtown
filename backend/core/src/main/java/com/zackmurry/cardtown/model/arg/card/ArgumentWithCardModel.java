package com.zackmurry.cardtown.model.arg.card;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentWithCardModel {

    private String name;
    private String id;
    private ResponseUserDetails owner;
    private short indexInArgument;


}
