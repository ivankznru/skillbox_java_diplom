package com.gh4biz.devpub.service;

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
}
