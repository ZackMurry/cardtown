package com.zackmurry.cardtown.model.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamJoinRequest {

    @NotNull
    private String teamId;
    @NotNull
    private String teamSecretKey;

}
