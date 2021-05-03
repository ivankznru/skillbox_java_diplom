package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;

    @Autowired
    public ApiPostController(PostService postService
    ) {
        this.postService = postService;
    }

    @GetMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    private ResponseEntity<PostsResponse> listOfPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "recent") String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    @PreAuthorize("hasAuthority('user:moderate')")
    private ResponseEntity<PostsResponse> searchResponse(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(postService.getSearch(query, offset, limit));
    }

    @GetMapping("/post/byDate")
    private ResponseEntity<PostsResponse> getPostsByDate(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    private ResponseEntity<PostsResponse> getPostsByTag(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/post/{id}")
    private ResponseEntity<PostResponse> getPostById(
            @PathVariable int id) {
        Post post = postService.findPostById(id);
        PostResponse postResponse = new PostResponse(post);
        return ResponseEntity.ok(postResponse);
    }
}
