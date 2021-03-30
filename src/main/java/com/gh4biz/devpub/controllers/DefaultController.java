package com.gh4biz.devpub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String greeting(Model model) {
        //model.addAttribute("name", "name");
        return "index";
    }

}
