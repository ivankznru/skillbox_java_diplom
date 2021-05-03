package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.response.CheckResponse;
import org.springframework.stereotype.Service;

@Service
public class CheckService {
    public CheckResponse check(){
        return new CheckResponse();
    }
}