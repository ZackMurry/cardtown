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
public class TeamMemberEntity {

    private UUID teamId;
    private UUID userId;
    private String teamSecretKey;
    private TeamRole role;

    public TeamMemberEntity(UUID teamId, UUID userId, String teamSecretKey) {
        this.teamId = teamId;
        this.userId = userId;
        this.teamSecretKey = teamSecretKey;
        this.role = TeamRole.MEMBER;
    }

}
