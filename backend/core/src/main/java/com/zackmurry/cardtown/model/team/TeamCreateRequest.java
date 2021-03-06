package com.zackmurry.cardtown.model.team;


import com.zackmurry.cardtown.exception.LengthRequiredException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamCreateRequest extends EncryptedTeam {

    public TeamCreateRequest(String name) {
        this.name = name;
    }

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
