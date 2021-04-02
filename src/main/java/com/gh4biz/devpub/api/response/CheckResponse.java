package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.User;

public class CheckResponse {
    private boolean result;
    private User user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
