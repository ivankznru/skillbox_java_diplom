package com.gh4biz.devpub.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsForm {
    @JsonProperty("MULTIUSER_MODE")
    boolean multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    boolean premoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    boolean statisticIsPublic;
}
