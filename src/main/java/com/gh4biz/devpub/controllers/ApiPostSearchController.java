package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostSearchResponse;
import com.gh4biz.devpub.repo.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiPostSearchController {
    private PostRepository postRepository;

    public ApiPostSearchController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private ResponseEntity<PostSearchResponse> searchResponse (HttpServletRequest request){
        String offset = request.getParameter("offset");
        String limit = request.getParameter("limit");
        String count = request.getParameter("query");




        PostSearchResponse searchResponse = new PostSearchResponse();
        return ResponseEntity.ok(searchResponse);
    }
}
