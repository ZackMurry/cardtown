package com.zackmurry.cardtown.model.arg;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.card.CardHeader;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<CardHeader> cards;

    public static ArgumentPreview of(ArgumentEntity arg, ResponseUserDetails owner, List<CardHeader> cards) {
        return new ArgumentPreview(
                UUIDCompressor.compress(arg.getId()),
                arg.getName(),
                owner,
                cards
        );
    }

}
