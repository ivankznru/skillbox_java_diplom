package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostResponse;
import com.gh4biz.devpub.model.Post;
import com.gh4biz.devpub.repo.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private PostRepository postRepository;

    public ApiPostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/post")
    private ResponseEntity<PostResponse> postResponse(HttpServletRequest request){
        String offset = request.getParameter("offset");
        String limit = request.getParameter("limit");
        String mode = request.getParameter("mode");

        Iterable<Post> posts = postRepository.findAll();
        ArrayList<Post> postArrayList = new ArrayList<>();
        posts.forEach(postArrayList::add);

        PostResponse postResponse = new PostResponse();
        postResponse.setCount(postArrayList.size());
        postResponse.setPosts(postArrayList);

        //model.addAttribute("post", postArrayList);
        return ResponseEntity.ok(postResponse);
    }

}
