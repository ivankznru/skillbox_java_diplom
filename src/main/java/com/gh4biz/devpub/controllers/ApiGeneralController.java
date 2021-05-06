package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.service.CheckService;
import com.gh4biz.devpub.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final CheckService checkService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                CheckService checkService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.checkService = checkService;
    }

    @GetMapping("/init")
    public ResponseEntity<InitResponse> init(){
        return ResponseEntity.ok(initResponse);
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settingsResponse(){
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

}