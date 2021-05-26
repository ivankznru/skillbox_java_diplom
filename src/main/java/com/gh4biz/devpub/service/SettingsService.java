package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.entity.GlobalSettings;
import com.gh4biz.devpub.model.request.SettingsForm;
import com.gh4biz.devpub.model.response.SettingsResponse;
import com.gh4biz.devpub.repo.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings() {
        boolean multiuserMode =
                globalSettingsRepository
                        .findByCode("MULTIUSER_MODE")
                        .getValue()
                        .equals("YES");
        boolean postPremoderation =
                globalSettingsRepository
                        .findByCode("POST_PREMODERATION")
                        .getValue()
                        .equals("YES");
        boolean statisticsIsPublic =
                globalSettingsRepository
                        .findByCode("STATISTICS_IS_PUBLIC")
                        .getValue()
                        .equals("YES");
        return new SettingsResponse(multiuserMode, postPremoderation, statisticsIsPublic);
    }

    public void setGlobalSettings(SettingsForm form) {
        String isMultiuserMode = form.isMultiuserMode() ? "YES" : "NO";
        String isPremoderation = form.isPremoderation() ? "YES" : "NO";
        String isStatisticIsPublic = form.isStatisticIsPublic() ? "YES" : "NO";
        changeSettings(isMultiuserMode, isPremoderation, isStatisticIsPublic);
    }

    private void changeSettings(String isMultiuserMode, String isPremoderation, String isStatisticIsPublic){
        GlobalSettings globalSettings = globalSettingsRepository.findByCode("MULTIUSER_MODE");
        globalSettings.setValue(isMultiuserMode);
        globalSettingsRepository.save(globalSettings);

        globalSettings = globalSettingsRepository.findByCode("POST_PREMODERATION");
        globalSettings.setValue(isPremoderation);
        globalSettingsRepository.save(globalSettings);

        globalSettings = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        globalSettings.setValue(isStatisticIsPublic);
        globalSettingsRepository.save(globalSettings);
    }

}
