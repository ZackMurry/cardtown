package com.zackmurry.cardtown.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirstLastName {

    @NotNull
    private String first;
    @NotNull
    private String last;

}
