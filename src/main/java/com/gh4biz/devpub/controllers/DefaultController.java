package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.InitResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    private final InitResponse initResponse;

    public DefaultController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @RequestMapping("/")
    public String greeting(Model model) {
//        model.addAttribute("name", "name");
//        System.out.println(initResponse.getTitle());
//        System.out.println("-=-=-=-=-=-=-=-");
        return "index";
    }

}