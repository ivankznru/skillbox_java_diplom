package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.*;
import com.gh4biz.devpub.model.GlobalSettings;
import com.gh4biz.devpub.repo.GlobalSettingsRepository;
import com.gh4biz.devpub.service.CheckService;
import com.gh4biz.devpub.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final CheckService checkService;

    public ApiGeneralController(InitResponse initResponse,
                                SettingsService settingsService,
                                CheckService checkService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.checkService = checkService;
    }

    @GetMapping("/auth/check")
    private ResponseEntity<CheckResponse> checkResponse(){
        return ResponseEntity.ok(checkService.check());
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private ResponseEntity<SettingsResponse> settingsResponse(){
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

//    @GetMapping("/tag")
//    private ResponseEntity<TagResponse> tagResponse(){
//        return ResponseEntity.ok(new TagResponse());
//    }
}