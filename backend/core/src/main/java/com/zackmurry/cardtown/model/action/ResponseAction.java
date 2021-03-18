package com.zackmurry.cardtown.model.action;

import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.util.UUIDCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAction {

    private ActionType actionType;
    private ResponseUserDetails subject;
    private long time;
    private ResponseUserDetails user;
    private ActionCardPreview card;
    private ActionArgumentPreview argument;

    public static ResponseAction of(@NonNull ActionEntity actionEntity,
                                    @NonNull ResponseUserDetails subject,
                                    @NonNull ResponseUserDetails user,
                                    @NonNull ActionCardPreview card,
                                    @NonNull ActionArgumentPreview argument) {
        return new ResponseAction(
                actionEntity.getActionType(),
                subject,
                actionEntity.getTime(),
                user,
                card,
                argument
        );
    }

}
