package com.zackmurry.cardtown.model.team;

public enum TeamRole {
    MEMBER("MEMBER"),
    OWNER("OWNER");

    private final String name;

    TeamRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
