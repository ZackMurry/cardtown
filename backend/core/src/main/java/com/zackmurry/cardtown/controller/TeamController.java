package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.model.team.*;
import com.zackmurry.cardtown.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotNull;
import java.util.Optional;

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
    public void joinTeam(@RequestBody TeamJoinRequest teamJoinRequest) {
        teamService.joinTeam(teamJoinRequest);
    }

    @GetMapping("")
    public TeamHeader getTeamInformation() {
        final Optional<TeamHeader> teamHeader = teamService.getTeamInformation();
        if (teamHeader.isEmpty()) {
            // If user is not in a team
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return teamHeader.get();
    }

    @PostMapping("/leave")
    public void leaveTeam() {
        teamService.leaveTeam();
    }

    @GetMapping("/id/{id}")
    public TeamPublicData getTeamHeaderById(@NonNull @PathVariable String id) {
        return teamService.getTeamHeaderById(id);
    }

}
