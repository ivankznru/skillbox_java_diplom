package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.request.CommentAddForm;
import com.gh4biz.devpub.model.request.PostEditForm;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
    @Value("${blogCommentTextLength}")
    private int blogCommentTextLength;

    @Autowired
    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    public ResponseEntity<PostsResponse> listOfPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "recent") String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostsResponse> searchResponse(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(postService.getSearch(query, offset, limit));
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> listOfModerationPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String status,
            Principal principal) {
        return postService.getModerationPosts(offset, limit, status, principal);
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> listOfMyPosts(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam String status,
            Principal principal) {
        return postService.getMyPosts(offset, limit, status, principal);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postAdd(
            @RequestBody PostEditForm form,
            Principal principal) {
        return postService.postAdd(form, principal);
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postEdit(
            @RequestBody PostEditForm form,
            @PathVariable int id,
            Principal principal) {
        return postService.postEdit(id, form, principal);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentAddResult> commentAdd(
            @RequestBody CommentAddForm form,
            Principal principal) {
        if (form.getText().length() < blogCommentTextLength){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Текст комментария не задан или слишком короткий!");
        }
        return postService.commentAdd(form.getParentId(), form.getPostId(), form.getText(), principal);
    }
}
