package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.response.PostsResponse;
import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.response.CommentCount;
import com.gh4biz.devpub.model.response.PostAnnotationResponse;
import com.gh4biz.devpub.model.response.UserResponse;
import com.gh4biz.devpub.model.response.VoteCount;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import com.gh4biz.devpub.repo.Tag2PostRepository;
import com.gh4biz.devpub.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;
    private Tag2PostRepository tag2PostRepository;
    private final int ANNOUNCE_TEXT_LIMIT = 150;
    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;
    private final int ACTIVE_POST_VALUE = 1;

    public ApiPostController(PostRepository postRepository,
                             PostVotesRepository postVotesRepository,
                             PostCommentsRepository postCommentsRepository,
                             Tag2PostRepository tag2PostRepository) {
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tag2PostRepository = tag2PostRepository;
    }

    @GetMapping("/post")
    private ResponseEntity<PostsResponse> postResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {
        if (mode.equals("popular")) {
            return ResponseEntity.ok(popularPosts(offset, limit));
        }
        if (mode.equals("best")) {
            return ResponseEntity.ok(bestPosts(offset, limit));
        }
        if (mode.equals("recent")) {
            return ResponseEntity.ok(recentPosts(offset, limit));
        }
        if (mode.equals("early")) {
            return ResponseEntity.ok(earlyPosts(offset, limit));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostsResponse> searchResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(getSearch(query, offset, limit));
    }

    @GetMapping("/post/byDate")
    private ResponseEntity<PostsResponse> postsResponseByDate(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        ArrayList<Integer> posts =
                postRepository.getPostsByDate(year, month, day,
                        PageRequest.of(offset / limit, limit));

        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Integer postId : posts) {
            Post post = postRepository.findPostsById(postId);
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return ResponseEntity.ok(new PostsResponse(postAnnotationResponseList.size(), postAnnotationResponseList));
    }

    @GetMapping("/post/byTag")
    private ResponseEntity<PostsResponse> postsResponseByTag(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String tag) {

        String tag2resp = "%".concat(tag).concat("%");
        ArrayList<Integer> posts =
                tag2PostRepository.getPosts(
                        tag2resp,
                        PageRequest.of(offset / limit, limit));

        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Integer postId : posts) {
            Post post = postRepository.findPostsById(postId);
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        int count = tag2PostRepository.countTagPosts(tag2resp);
        return ResponseEntity.ok(new PostsResponse(count, postAnnotationResponseList));
    }

    @GetMapping("/post/{id}")
    private ResponseEntity<PostsResponse> getPostById(
            @PathVariable int id) {
        Post post = PostService.getPostRepository().findPostsById(id);
        System.out.println(post.getText());
        return ResponseEntity.ok(new PostsResponse());
    }

    private PostsResponse popularPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();

        Slice<CommentCount> postCommentSlice =
                postCommentsRepository.countTotalComments(
                        PageRequest.of(offset / limit, limit));

        for (CommentCount comment : postCommentSlice) {
            Post post = postRepository.findPostsById(comment.getId());
            postAnnotationResponseList.add(convert2Post4Response(post));
        }

        return new PostsResponse(postCommentSlice.getContent().size(),
                postAnnotationResponseList);
    }

    private PostsResponse bestPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<VoteCount> voteCounts = postVotesRepository.countTotalVote(
                LIKE_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (VoteCount vote : voteCounts) {
            Post post = postRepository.findPostsById(vote.getId());
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return new PostsResponse(voteCounts.getSize(), postAnnotationResponseList);
    }

    private PostsResponse recentPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeDesc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        int count = postRepository.countAllByIsActive(ACTIVE_POST_VALUE);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    private PostsResponse earlyPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeAsc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return new PostsResponse(postRepository.countAllByIsActive(ACTIVE_POST_VALUE), postAnnotationResponseList);
    }

    private PostsResponse getSearch(String query, int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        String query4sql = "%".concat(query).concat("%");
        Page<Integer> posts = postRepository.getPostsBySearch(
                query4sql,
                PageRequest.of(offset / limit, limit)
        );
        for (Integer postId : posts) {
            Post post = postRepository.findPostsById(postId);
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        int count = postRepository.countSearchPosts(query4sql);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    private PostAnnotationResponse convert2Post4Response(Post post) {
        return new PostAnnotationResponse(post.getId(),
                post.getTime().getTime() / 1000,
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getName()),
                post.getTitle(),
                post.getText().substring // анонс
                        (0, Math.min(ANNOUNCE_TEXT_LIMIT, post.getText().length()))
                        .concat("..."),
                getVoteCount(post, LIKE_VALUE),
                getVoteCount(post, DISLIKE_VALUE),
                getCommentsCount(post),
                post.getViewCount());
    }

    private int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }
}
