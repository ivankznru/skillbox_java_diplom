package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostResponse;
import com.gh4biz.devpub.model.*;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;
    private final int ANNOUNCE_TEXT_LIMIT = 150;
    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;
    private final int ACTIVE_POST_VALUE = 1;
    int postsCount;
    private final String regex = "[\\.,\\s!;?:\"']+";
    ArrayList<SearchTree> searchTreeArrayList = new ArrayList<>();

    public ApiPostController(PostRepository postRepository,
                             PostVotesRepository postVotesRepository,
                             PostCommentsRepository postCommentsRepository) {
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
    }

    @GetMapping("/post")
    private ResponseEntity<PostResponse> postResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {

        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        PostResponse postResponse = new PostResponse();

        if (mode.equals("popular")) {
            Slice<CommentCount> postCommentSlice =
                    postCommentsRepository.countTotalComments(
                            PageRequest.of(offset % limit, limit));

            for (CommentCount comment : postCommentSlice) {
                Post post = postRepository.findById(comment.getId());
                post4ResponseList.add(new Post4Response(post.getId(),
                        post.getTime().getTime() / 1000,
                        new User4Response(
                                post.getUser().getId(),
                                post.getUser().getName()),
                        post.getTitle(),
                        post.getText().substring // анонс
                                (0, Math.min(ANNOUNCE_TEXT_LIMIT, post.getText().length()))
                                .concat("..."),
                        getVoteCount(post, LIKE_VALUE),
                        getVoteCount(post, DISLIKE_VALUE),
                        getCommentsCount(post),
                        post.getViewCount(), getCommentsCount(post)));

            }
            postResponse.setCount(postCommentSlice.getContent().size());
            postResponse.setPosts(post4ResponseList);

            return ResponseEntity.ok(postResponse);
        }

        postsCount = getActivePostsCount();
        postResponse.setCount(postsCount);
        Iterable<Post> postIterable = postRepository.findAll();
        postResponse.setPosts(getPosts(postIterable, offset, limit, mode));

        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostResponse> searchResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String query
    ) {
        Iterable<Post> postIterable = postRepository.findAll();
        if (offset == 0)
            searchIndex(postIterable);

        PostResponse searchResponse = new PostResponse();
        ArrayList<Post4Response> posts = getSearch(query);
        searchResponse.setCount(posts.size());
        ArrayList<Post4Response> responses = new ArrayList<>();
        for (int i = offset; i < Math.min(limit, posts.size()); i++) {
            responses.add(posts.get(i));
        }
        searchResponse.setPosts(responses);
        return ResponseEntity.ok(searchResponse);
    }

    private ArrayList<Post4Response> getSearch(String query) {
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        for (SearchTree searchTree : searchTreeArrayList) {
            Post post = searchTree.getPost();
            if (!searchTree.getText().contains(query.toLowerCase())) {
                continue;
            }
            int likeCount = getVoteCount(post, LIKE_VALUE);
            int dislikeCount = getVoteCount(post, DISLIKE_VALUE);
            int commentCount = getCommentsCount(post);
            long sort = post.getTime().getTime();

            post4ResponseList.add(new Post4Response(post.getId(),
                    post.getTime().getTime() / 1000,
                    new User4Response(
                            post.getUser().getId(),
                            post.getUser().getName()),
                    post.getTitle(),
                    post.getText().substring // анонс
                            (0, Math.min(ANNOUNCE_TEXT_LIMIT, post.getText().length()))
                            .concat("..."),
                    likeCount,
                    dislikeCount,
                    commentCount,
                    post.getViewCount(), sort));
        }
        return post4ResponseList;
    }

    private void searchIndex(Iterable<Post> postIterable) {
        searchTreeArrayList.clear();
        for (Post post : postIterable) {
            String text =
                    post.getTitle().concat(" ").concat(post.getText());
            String[] fragments = text.split(regex);
            TreeSet<String> tree = new TreeSet<>();
            for (String fragment : fragments) {
                tree.add(fragment.toLowerCase());
            }
            searchTreeArrayList.add(new SearchTree(post, tree));
        }
    }

    ArrayList<Post4Response> getPosts(Iterable<Post> postIterable, int offset, int limit, String mode) {
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        int iterationCounter = 0;


        for (Post post : postIterable) {
            if (iterationCounter < offset) {
                iterationCounter++;
                continue;
            }
            if (post4ResponseList.size() >= limit) {
                break;
            }
            int likeCount = getVoteCount(post, LIKE_VALUE);
            int dislikeCount = getVoteCount(post, DISLIKE_VALUE);
            int commentCount = getCommentsCount(post);

            long sort = 0;
            switch (mode) {
                case "best": {
                    if (likeCount > 0) {
                        sort = likeCount;
                    } else {
                        continue;
                    }
                    break;
                }
                case "popular": {
                    if (commentCount > 0) {
                        sort = commentCount;
                    } else
                        continue;
                    break;
                }
                case "recent": {
                    sort = post.getTime().getTime();
                }
            }
            post4ResponseList.add(new Post4Response(post.getId(),
                    post.getTime().getTime() / 1000,
                    new User4Response(
                            post.getUser().getId(),
                            post.getUser().getName()),
                    post.getTitle(),
                    post.getText().substring // анонс
                            (0, Math.min(ANNOUNCE_TEXT_LIMIT, post.getText().length()))
                            .concat("..."),
                    likeCount,
                    dislikeCount,
                    getCommentsCount(post),
                    post.getViewCount(), sort));
            iterationCounter++;
        }
        return post4ResponseList;
    }

    private int getActivePostsCount() {
        return postRepository.countAllByIsActive(ACTIVE_POST_VALUE);
    }

    private int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }
}
