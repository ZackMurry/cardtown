package com.zackmurry.cardtown.model.team;


import com.zackmurry.cardtown.exception.LengthRequiredException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequest extends EncryptedTeam {

    /**
     * Checks if the fields are valid
     * @throws LengthRequiredException If the name is greater than 128 chars or less than one
     */
    public void validateFields() {
        if (name.length() > 128 || name.isEmpty()) {
            throw new LengthRequiredException();
        }
    }

}
