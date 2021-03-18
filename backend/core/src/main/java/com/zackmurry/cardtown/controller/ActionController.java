package com.zackmurry.cardtown.controller;

import com.zackmurry.cardtown.exception.BadRequestException;
import com.zackmurry.cardtown.model.action.ResponseAction;
import com.zackmurry.cardtown.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/actions")
@RestController
public class ActionController {

    @Autowired
    private ActionService actionService;

    @GetMapping("/recent")
    public List<ResponseAction> getRecentActions(@RequestParam(required = false) String page) {
        // page is 0-based
        int pageNum = 0;
        if (page != null) {
            try {
                pageNum = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                throw new BadRequestException("Page parameter is invalid");
            }
        }
        return actionService.getRecentActions(pageNum);
    }

}
