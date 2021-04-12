package com.gh4biz.devpub.service;

import com.gh4biz.devpub.api.response.SettingsResponse;
import com.gh4biz.devpub.model.GlobalSettings;
import com.gh4biz.devpub.repo.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SettingsService {
    private GlobalSettingsRepository globalSettingsRepository;

    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        Iterable<GlobalSettings> globalSettings = globalSettingsRepository.findAll();
        ArrayList<GlobalSettings> globalSettingsArrayList = new ArrayList<>();
        globalSettings.forEach(globalSettingsArrayList::add);

        for (GlobalSettings set : globalSettingsArrayList){
            if (set.getCode().equals("MULTIUSER_MODE")) {
                if (set.getValue().equals("YES")){
                    settingsResponse.setMultiuserMode(true);
                }
                if (set.equals("NO")){
                    settingsResponse.setMultiuserMode(false);
                }
            }
            if (set.getCode().equals("POST_PREMODERATION")) {
                if (set.getValue().equals("YES")){
                    settingsResponse.setPostPremoderation(true);
                }
                if (set.equals("NO")){
                    settingsResponse.setPostPremoderation(false);
                }
            }
            if (set.getCode().equals("STATISTICS_IS_PUBLIC")) {
                if (set.getValue().equals("YES")){
                    settingsResponse.setStatisticsIsPublic(true);
                }
                if (set.equals("NO")){
                    settingsResponse.setStatisticsIsPublic(false);
                }
            }
        }
        return settingsResponse;
    }
}
