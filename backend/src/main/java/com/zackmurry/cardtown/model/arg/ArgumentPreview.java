package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * like a <code>ResponseArgument</code>, but without cards (just number of cards)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentPreview {

    private String id;
    private String name;
    private ResponseUserDetails owner;
    private Integer cardCount;

    public static ArgumentPreview of(ArgumentEntity arg, ResponseUserDetails owner, Integer cardCount) {
        return new ArgumentPreview(
                UUIDCompressor.compress(arg.getId()),
                arg.getName(),
                owner,
                cardCount
        );
    }

}
