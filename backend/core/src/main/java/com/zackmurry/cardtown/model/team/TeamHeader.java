package com.zackmurry.cardtown.model.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamHeader {

    private String name;
    private int memberCount;

    public static TeamHeader of(TeamEntity teamEntity, int memberCount) {
        return new TeamHeader(
                teamEntity.getName(),
                memberCount
        );
    }

}
