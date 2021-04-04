package com.zackmurry.cardtown.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class UserModel extends User {

    private byte[] secretKey;

    // Used for easy access by the service layer
    private byte[] teamSecretKey;
    private Optional<UUID> teamId;
    private byte[] encryptionKey;

    public UserModel(User u, byte[] secretKey, byte[] teamSecretKey, Optional<UUID> teamId, byte[] encryptionKey) {
        this.setId(u.getId());
        this.setEmail(u.getEmail());
        this.setFirstName(u.getFirstName());
        this.setLastName(u.getLastName());
        this.setPassword(u.getPassword());
        this.secretKey = secretKey;
        this.setRoles(u.getRoles());
        this.setTeamSecretKey(teamSecretKey);
        this.teamId = teamId;
        this.encryptionKey = encryptionKey;
    }

}
