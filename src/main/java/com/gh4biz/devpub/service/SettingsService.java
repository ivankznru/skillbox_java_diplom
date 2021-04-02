package com.gh4biz.devpub.service;

import com.gh4biz.devpub.api.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(true);
        settingsResponse.setStatisticsIsPublic(true);
        return settingsResponse;
    }
}
