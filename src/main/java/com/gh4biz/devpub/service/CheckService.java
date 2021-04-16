package com.gh4biz.devpub.service;

import com.gh4biz.devpub.api.response.CheckResponse;
import com.gh4biz.devpub.model.User;
import org.springframework.stereotype.Service;

@Service
public class CheckService {
    public CheckResponse check(){
        CheckResponse checkResponse = new CheckResponse();
        return checkResponse;
    }
}