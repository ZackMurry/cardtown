package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.team.TeamCreateRequest;
import com.zackmurry.cardtown.model.team.TeamCreationResponse;
import com.zackmurry.cardtown.model.team.TeamJoinRequest;
import com.zackmurry.cardtown.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RequestMapping("/api/v1/teams")
@RestController
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("")
    public TeamCreationResponse createTeam(@RequestBody @NotNull TeamCreateRequest teamCreateRequest) {
        return teamService.createTeam(teamCreateRequest);
    }

    @PostMapping("/join")
    public void joinTeam(@RequestBody @NotNull TeamJoinRequest teamJoinRequest) {
        teamService.joinTeam(teamJoinRequest);
    }

}
