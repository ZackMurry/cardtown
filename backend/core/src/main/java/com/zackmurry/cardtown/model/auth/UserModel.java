package com.zackmurry.cardtown.model.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModel extends User {

    private byte[] secretKey;

    public UserModel(User u, byte[] secretKey) {
        this.setId(u.getId());
        this.setEmail(u.getEmail());
        this.setFirstName(u.getFirstName());
        this.setLastName(u.getLastName());
        this.setPassword(u.getPassword());
        this.secretKey = secretKey;
        this.setRoles(u.getRoles());
    }

}
