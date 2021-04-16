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
import java.util.ArrayList;
import java.util.TreeSet;

@RestController
@RequestMapping("/api")
public class ApiSearchController {
    private PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;

    private final String regex = "[\\.,\\s!;?:\"']+";
    ArrayList<SearchTree> searchTreeArrayList = new ArrayList<>();

    public ApiSearchController(PostRepository postRepository,
                               PostVotesRepository postVotesRepository,
                               PostCommentsRepository postCommentsRepository) {
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
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
        ArrayList<Post4Response> posts = getPosts(query);
        searchResponse.setCount(posts.size());
        ArrayList<Post4Response> responses = new ArrayList<>();
        for (int i = offset; i <Math.min(limit, posts.size()); i++){
            responses.add(posts.get(i));
        }
        searchResponse.setPosts(responses);
        return ResponseEntity.ok(searchResponse);
    }

    private ArrayList<Post4Response> getPosts(String query) {
        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        for (SearchTree searchTree : searchTreeArrayList) {
            Post post = searchTree.getPost();
            if (!searchTree.getText().contains(query)) {
                continue;
            }
            int likeCount = getVoteCount(post, "like");
            int dislikeCount = getVoteCount(post, "dislike");
            int commentCount = getCommentsCount(post);
            long sort = post.getTime().getTime();

            int ANNOUNCE_TEXT_LIMIT = 150;
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
            String[] fragments = post.getTitle().split(regex);
            TreeSet<String> tree = new TreeSet<>();
            for (String fragment : fragments) {
                tree.add(fragment);
            }
            fragments = post.getText().split(regex);
            for (String fragment : fragments) {
                tree.add(fragment);
            }
            searchTreeArrayList.add(new SearchTree(post, tree));
        }
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
