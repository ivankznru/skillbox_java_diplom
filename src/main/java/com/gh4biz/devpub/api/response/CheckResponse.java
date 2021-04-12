package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.User;
import lombok.Data;

@Data
public class CheckResponse {
    private boolean result;
    private User user;
}
