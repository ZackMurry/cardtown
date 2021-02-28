package com.zackmurry.cardtown.model.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity extends EncryptedTeam {

    private UUID id;
    private String secretKeyHash;

    public TeamEntity(String name) {
        this.name = name;
    }

    public TeamEntity(String name, String secretKeyHash) {
        this.name = name;
        this.secretKeyHash = secretKeyHash;
    }

    public TeamEntity(String name, UUID id, String secretKeyHash) {
        this.name = name;
        this.id = id;
        this.secretKeyHash = secretKeyHash;
    }

}
