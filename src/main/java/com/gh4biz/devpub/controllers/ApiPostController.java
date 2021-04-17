package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostResponse;
import com.gh4biz.devpub.model.*;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;
    private final int ANNOUNCE_TEXT_LIMIT = 150;
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
    private ResponseEntity<PostResponse> postResponse(HttpServletRequest request) {
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        String mode = request.getParameter("mode");
        Iterable<Post> postIterable = postRepository.findAll();
        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(getPosts(postIterable, offset, limit, mode));

        postsCount = getPostsCount(postIterable);
        postResponse.setCount(postsCount);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostResponse> searchResponse(HttpServletRequest request) {
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        String query = request.getParameter("query").replaceAll(regex, "");
        Iterable<Post> postIterable = postRepository.findAll();
        if (offset == 0)
            searchIndex(postIterable);

        PostResponse searchResponse = new PostResponse();
        ArrayList<Post4Response> posts = getSearch(query);
        searchResponse.setCount(posts.size());
        ArrayList<Post4Response> responses = new ArrayList<>();
        for (int i = offset; i <Math.min(limit, posts.size()); i++){
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
            int likeCount = getVoteCount(post, "like");
            int dislikeCount = getVoteCount(post, "dislike");
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

    private ArrayList<Post4Response> getPosts(Iterable<Post> postIterable, int offset, int limit, String mode) {
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
            int likeCount = getVoteCount(post, "like");
            int dislikeCount = getVoteCount(post, "dislike");
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
            //int ANNOUNCE_TEXT_LIMIT = 150;
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

    private int getPostsCount(Iterable<Post> postIterable) {
        int activePostsCount = 0;
        for (Post post : postIterable) {
            int STATUS_ACTIVE = 1;
            if (post.getIsActive() == STATUS_ACTIVE) {
                activePostsCount++;
            }
        }
        return activePostsCount;
    }

    private int getVoteCount(Post post, String vote) {
        Iterable<PostVote> postVoteIterable = postVotesRepository.findAll();
        int count = 0;
        for (PostVote postVote : postVoteIterable) {
            if (!postVote.getPost().equals(post)) continue;
            if ((vote.equals("like")) & (postVote.getValue() > 0)) {
                count++;
            }
            if ((vote.equals("dislike")) & (postVote.getValue() < 0)) {
                count++;
            }
        }
        return count;
    }

    private int getCommentsCount(Post post) {
        Iterable<PostComment> postCommentIterable = postCommentsRepository.findAll();
        ArrayList<PostComment> postCommentList = new ArrayList<>();
        for (PostComment comment : postCommentIterable) {
            if (comment.getPost().getId() == post.getId()) {
                postCommentList.add(comment);
            }
        }
        return postCommentList.size();
    }
}
