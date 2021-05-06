package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.TagResponse;
import com.gh4biz.devpub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiTagController {
    private final TagService tagService;

    @Autowired
    public ApiTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> tagResponse() {
        return ResponseEntity.ok(tagService.getTags());
    }
}
