package com.gh4biz.devpub.model.response;

import com.gh4biz.devpub.model.entity.User;
import lombok.Data;

@Data
public class CheckResponse {
    private boolean result;
    private User user;
}
