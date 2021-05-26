package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.request.SettingsForm;
import com.gh4biz.devpub.model.response.Result;
import com.gh4biz.devpub.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiSettings {
    private final SettingsService settingsService;

    public ApiSettings(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PutMapping("/api/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Result> settingsChange(
            @RequestBody SettingsForm form) {
        settingsService.setGlobalSettings(form);
        return ResponseEntity.ok(new Result(true));
    }
}
