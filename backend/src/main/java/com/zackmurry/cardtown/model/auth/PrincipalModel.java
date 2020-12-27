package com.zackmurry.cardtown.model.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrincipalModel extends User {

    private String secretKey;

    public PrincipalModel(User u, String secretKey) {
        this.setId(u.getId());
        this.setEmail(u.getEmail());
        this.setFirstName(u.getFirstName());
        this.setLastName(u.getLastName());
        this.setPassword(u.getPassword());
        this.secretKey = secretKey;
    }

}
