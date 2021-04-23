package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostsResponse;
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
    private ResponseEntity<PostsResponse> postResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {

        if (mode.equals("popular")) {
            return ResponseEntity.ok(popularPosts(offset, limit));
        }

        if (mode.equals("best")) {
            return ResponseEntity.ok(bestPosts(offset,limit));
        }

        if (mode.equals("recent")) {
            return ResponseEntity.ok(recentPosts(offset,limit));
        }

        if (mode.equals("early")) {
            return ResponseEntity.ok(earlyPosts(offset,limit));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostsResponse> searchResponse(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String query
    ) {
        Iterable<Post> postIterable = postRepository.findAll();
        if (offset == 0)
            searchIndex(postIterable);

        PostsResponse searchResponse = new PostsResponse();
        ArrayList<Post4Response> posts = getSearch(query);
        searchResponse.setCount(posts.size());
        ArrayList<Post4Response> responses = new ArrayList<>();
        for (int i = offset; i < Math.min(limit, posts.size()); i++) {
            responses.add(posts.get(i));
        }
        searchResponse.setPosts(responses);
        return ResponseEntity.ok(searchResponse);
    }

    private PostsResponse popularPosts(int offset, int limit) {
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();

        Slice<CommentCount> postCommentSlice =
                postCommentsRepository.countTotalComments(
                        PageRequest.of(offset % limit, limit));

        for (CommentCount comment : postCommentSlice) {
            Post post = postRepository.findPostsById(comment.getId());
            long sort = postCommentsRepository.countAllByPostId(post.getId());
            post4ResponseList.add(convert2Post4Response(post, sort));
        }

        return new PostsResponse(postCommentSlice.getContent().size(),
                post4ResponseList);
    }

    private PostsResponse bestPosts(int offset, int limit){
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        Slice<VoteCount> voteCounts = postVotesRepository.countTotalVote(
                LIKE_VALUE,
                PageRequest.of(offset % limit, limit)
        );
        for (VoteCount vote : voteCounts) {
            Post post = postRepository.findPostsById(vote.getId());
            long sort = vote.getTotal();
            post4ResponseList.add(convert2Post4Response(post, sort));
        }
        return new PostsResponse(voteCounts.getSize(), post4ResponseList);
    }

    private PostsResponse recentPosts(int offset, int limit){
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeDesc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset % limit, limit)
        );
        for (Post post : posts){
            post4ResponseList.add(convert2Post4Response(post, 0));
        }
        return new PostsResponse(postRepository.countAllByIsActive(ACTIVE_POST_VALUE), post4ResponseList);
    }

    private PostsResponse earlyPosts(int offset, int limit){
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeAsc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset % limit, limit)
        );
        for (Post post : posts){
            post4ResponseList.add(convert2Post4Response(post, 0));
        }
        return new PostsResponse(postRepository.countAllByIsActive(ACTIVE_POST_VALUE), post4ResponseList);
    }

    private ArrayList<Post4Response> getSearch(String query) {
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        for (SearchTree searchTree : searchTreeArrayList) {
            Post post = searchTree.getPost();
            if (!searchTree.getText().contains(query.toLowerCase())) {
                continue;
            }
            long sort = post.getTime().getTime();
            post4ResponseList.add(convert2Post4Response(post, sort));
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

    private Post4Response convert2Post4Response(Post post, long sort) {
        return new Post4Response(post.getId(),
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
                post.getViewCount(), sort);
    }

    private int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }
}
