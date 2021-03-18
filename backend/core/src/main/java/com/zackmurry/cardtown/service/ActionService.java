package com.zackmurry.cardtown.service;

import com.zackmurry.cardtown.dao.action.ActionDao;
import com.zackmurry.cardtown.model.action.ActionArgumentPreview;
import com.zackmurry.cardtown.model.action.ActionCardPreview;
import com.zackmurry.cardtown.model.action.ActionEntity;
import com.zackmurry.cardtown.model.action.ResponseAction;
import com.zackmurry.cardtown.model.arg.ArgumentEntity;
import com.zackmurry.cardtown.model.auth.ResponseUserDetails;
import com.zackmurry.cardtown.model.auth.UserModel;
import com.zackmurry.cardtown.model.card.CardPreview;
import com.zackmurry.cardtown.model.team.TeamEntity;
import com.zackmurry.cardtown.util.UUIDCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.zackmurry.cardtown.exception.InternalServerException;

import java.util.*;

@Service
public class ActionService {

    private static final int ACTIONS_PER_PAGE = 10;

    @Autowired
    private ActionDao actionDao;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private ArgumentService argumentService;

    /**
     * Creates an action in the database
     *
     * @param actionEntity Details of action to create
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public void createAction(@NonNull ActionEntity actionEntity) {
        actionDao.createAction(actionEntity);
    }

    /**
     * Gets all actions that should be visible to the principal
     *
     * @return Actions that are visible to the user
     * @throws InternalServerException If a <code>SQLException</code> occurs in the DAO layer
     */
    public List<ActionEntity> getAllActionsVisibleToUser() {
        final Optional<TeamEntity> optionalTeamEntity = teamService.getTeamOfUser();
        if (optionalTeamEntity.isPresent()) {
            return actionDao.getAllActionsByTeam(optionalTeamEntity.get().getId());
        }
        final UUID userId = ((UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return actionDao.getAllActionsByUser(userId);
    }

    private List<ResponseAction> convertActionEntitiesIntoResponseActions(@NonNull List<ActionEntity> actionEntities) {
        // Putting all data into a map so that there aren't too many database queries
        final Map<UUID, ResponseUserDetails> responseUserDetailsMap = new HashMap<>();
        final Map<UUID, ActionCardPreview> actionCardPreviewMap = new HashMap<>();
        final Map<UUID, ActionArgumentPreview> actionArgumentPreviewMap = new HashMap<>();
        for (ActionEntity actionEntity : actionEntities) {
            if (!responseUserDetailsMap.containsKey(actionEntity.getSubjectId())) {
                responseUserDetailsMap.put(actionEntity.getSubjectId(), userService.getResponseUserDetailsById(actionEntity.getSubjectId()).orElseThrow(InternalServerException::new));
            }
            if (actionEntity.getUserId() != null && !responseUserDetailsMap.containsKey(actionEntity.getUserId())) {
                responseUserDetailsMap.put(actionEntity.getUserId(), userService.getResponseUserDetailsById(actionEntity.getUserId()).orElseThrow(InternalServerException::new));
            }
            if (actionEntity.getCardId() != null && !actionCardPreviewMap.containsKey(actionEntity.getCardId())) {
                final CardPreview cardPreview = cardService.getCardPreviewByIdIncludingDeleted(actionEntity.getCardId()).orElseThrow(InternalServerException::new);
                final int numAppearancesInArgs = argumentService.getNumRelatedArgumentsByCardId(actionEntity.getCardId());
                actionCardPreviewMap.put(actionEntity.getCardId(), ActionCardPreview.of(cardPreview, numAppearancesInArgs));
            }
            if (actionEntity.getArgumentId() != null && !actionArgumentPreviewMap.containsKey(actionEntity.getArgumentId())) {
                final ArgumentEntity argumentEntity = argumentService.getArgumentEntityById(actionEntity.getArgumentId(), true).orElseThrow(InternalServerException::new);
                final int numCardsInArgument = argumentService.getNumCardsInArgument(actionEntity.getArgumentId());
                final ActionArgumentPreview actionArgumentPreview = new ActionArgumentPreview(UUIDCompressor.compress(actionEntity.getArgumentId()), argumentEntity.getName(), numCardsInArgument);
                actionArgumentPreviewMap.put(actionEntity.getArgumentId(), actionArgumentPreview);
            }
        }

        final List<ResponseAction> responseActions = new ArrayList<>();
        for (ActionEntity actionEntity : actionEntities) {
            responseActions.add(
                    ResponseAction.of(
                            actionEntity,
                            responseUserDetailsMap.get(actionEntity.getSubjectId()),
                            responseUserDetailsMap.get(actionEntity.getUserId()),
                            actionCardPreviewMap.get(actionEntity.getCardId()),
                            actionArgumentPreviewMap.get(actionEntity.getArgumentId())
                    )
            );
        }
        return responseActions;
    }

    public List<ResponseAction> getRecentActions(int page) {
        final UserModel principal = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<ActionEntity> actionEntities;
        if (principal.getTeamId().isPresent()) {
            // If user is in a team
            actionEntities = actionDao.getRecentActionsByTeam(principal.getTeamId().get(), ACTIONS_PER_PAGE, page * ACTIONS_PER_PAGE);
        } else {
            actionEntities = actionDao.getRecentActionsByUser(principal.getId(), ACTIONS_PER_PAGE, page * ACTIONS_PER_PAGE);
        }
        return convertActionEntitiesIntoResponseActions(actionEntities);
    }

}
