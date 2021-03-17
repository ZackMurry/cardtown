package com.zackmurry.cardtown.model.action;

import com.zackmurry.cardtown.exception.InternalServerException;
import com.zackmurry.cardtown.model.auth.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionEntity {

    private UUID id;
    private UUID subjectId;
    private ActionType actionType;
    private long time;
    private UUID userId;
    private UUID cardId;
    private UUID argumentId;

    public ActionEntity(UUID subjectId, ActionType actionType, UUID userId, UUID cardId, UUID argumentId) {
        this.subjectId = subjectId;
        this.actionType = actionType;
        this.time = System.currentTimeMillis();
        this.userId = userId;
        this.cardId = cardId;
        this.argumentId = argumentId;
    }

    public static class ActionEntityBuilder {

        private final ActionEntity actionEntity;

        public ActionEntityBuilder() {
            actionEntity = new ActionEntity();
            actionEntity.setTime(System.currentTimeMillis());
        }

        public ActionEntityBuilder id(UUID id) {
            actionEntity.setId(id);
            return this;
        }

        public ActionEntityBuilder subject(UUID id) {
            actionEntity.setSubjectId(id);
            return this;
        }

        public ActionEntityBuilder type(ActionType actionType) {
            actionEntity.setActionType(actionType);
            return this;
        }

        public ActionEntityBuilder time(long time) {
            actionEntity.setTime(time);
            return this;
        }

        public ActionEntityBuilder user(UUID id) {
            actionEntity.setUserId(id);
            return this;
        }

        public ActionEntityBuilder card(UUID id) {
            actionEntity.setCardId(id);
            return this;
        }

        public ActionEntityBuilder argument(UUID id) {
            actionEntity.setArgumentId(id);
            return this;
        }

        public ActionEntityBuilder principal() {
            actionEntity.setSubjectId(((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
            return this;
        }

        public ActionEntity build() {
            if (actionEntity.getActionType() == null || actionEntity.getSubjectId() == null) {
                throw new InternalServerException();
            }
            return actionEntity;
        }

    }

    public static ActionEntityBuilder builder() {
        return new ActionEntityBuilder();
    }

}
