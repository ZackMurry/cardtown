package com.zackmurry.cardtown.model.auth;

import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * this is sent back with requests for cards and stuff.
 * it contains information about a user (normally like an author or something).
 * this contains / (will contain) their name, user id (in base64), profile picture, etc.
 * probably not email tho
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDetails {

    private FirstLastName name;
    private String id;

    public ResponseUserDetails(FirstLastName name, UUID id) {
        this.name = name;
        this.id = UUIDCompressor.compress(id);
    }

    public static ResponseUserDetails fromUser(User u) {
        return new ResponseUserDetails(
                new FirstLastName(
                        u.getFirstName(),
                        u.getLastName()
                ),
                UUIDCompressor.compress(u.getId())
        );
    }

}
