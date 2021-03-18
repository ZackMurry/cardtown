package com.zackmurry.cardtown.model.action;

import com.zackmurry.cardtown.model.card.CardPreview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionCardPreview {

    private String id;
    private String tag;
    private String cite;
    private String bodyText; // todo: just store word count in database for efficiency?
    private int numRelatedArguments;

    public static ActionCardPreview of(@NonNull CardPreview cardPreview, int numRelatedArguments) {
        return new ActionCardPreview(
            cardPreview.getId(),
            cardPreview.getTag(),
            cardPreview.getCite(),
            cardPreview.getBodyText(),
            numRelatedArguments
        );
    }

}
