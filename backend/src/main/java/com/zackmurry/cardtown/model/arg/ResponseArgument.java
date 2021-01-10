package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseArgument {

    private String id;
    private ResponseUserDetails owner;
    private String name;

    // todo list of cards

    public static ResponseArgument fromArgumentEntity(ArgumentEntity e, ResponseUserDetails u) {
        return new ResponseArgument(UUIDUtils.compress(e.getId()), u, e.getName());
    }

}
