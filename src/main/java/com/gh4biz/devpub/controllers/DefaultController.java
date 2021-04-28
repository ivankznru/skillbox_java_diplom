package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.InitResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    private final InitResponse initResponse;

    public DefaultController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }

    @GetMapping("/posts/*")
    private String getPosts(){
        return "index";
    }

    @GetMapping("/tag/*")
    private String getTag(){
        return "index";
    }

    @GetMapping("/calendar/*")
    private String getCalendar(){
        return "index";
    }

}