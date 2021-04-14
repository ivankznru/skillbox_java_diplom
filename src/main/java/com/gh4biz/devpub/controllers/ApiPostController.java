package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostResponse;
import com.gh4biz.devpub.model.Post;
import com.gh4biz.devpub.model.Post4Response;
import com.gh4biz.devpub.model.PostVote;
import com.gh4biz.devpub.model.User4Response;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private final int ANNOUNCE_TEXT_LIMIT = 150;

    public ApiPostController(PostRepository postRepository, PostVotesRepository postVotesRepository) {
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
    }

    @GetMapping("/post")
    private ResponseEntity<PostResponse> postResponse(HttpServletRequest request) {
        Integer offset = Integer.parseInt(request.getParameter("offset"));
        Integer limit = Integer.parseInt(request.getParameter("limit"));
        String mode = request.getParameter("mode");

        PostResponse postResponse = new PostResponse();
        postResponse.setCount(limit);

        postResponse.setPosts(getPosts(offset, limit, mode));
        return ResponseEntity.ok(postResponse);
    }

    private List<Post4Response> getPosts(int offset, int limit, String mode) {
        Iterable<Post> postIterable = postRepository.findAll();
        ArrayList<Post> activePostList = new ArrayList<>();

        for (Post post : postIterable) {
            if (post.getIsActive() == 1) {
                activePostList.add(post);
            }
        }

        postIterable.forEach(activePostList::add);

        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        int activePostsCount = activePostList.size();

        int i = offset;
        while (post4ResponseList.size() < Math.min(offset + limit, activePostsCount)) {
            Post post = activePostList.get(i);

            int id = post.getId();
            Date date = post.getTime();
            long timestamp = date.getTime() / 1000;
            User4Response user = new User4Response(
                    post.getUser().getId(),
                    post.getUser().getName()
            );
            String title = post.getTitle();
            String announce = post.getText().substring
                    (0, Math.min(ANNOUNCE_TEXT_LIMIT, post.getText().length()))
                    .concat("...");

            int likeCount = getVoteCount(post)[0];
            int dislikeCount = getVoteCount(post)[1];
            int commentCount = 0;
            int viewCount = post.getViewCount();

            post4ResponseList.add(new Post4Response(id, timestamp, user, title, announce, likeCount, dislikeCount, commentCount, viewCount));
            i++;
        }

        return post4ResponseList;
    }

    private int[] getVoteCount(Post post) {
        Iterable<PostVote> postVoteIterable = postVotesRepository.findAll();
        ArrayList<PostVote> postVoteList = new ArrayList<>();
        postVoteIterable.forEach(postVoteList::add);

        int likeCount = 0;
        int dislikeCount = 0;
        for (PostVote postVote : postVoteList) {
            if (postVote.getPost().getId() != post.getId())
                continue;
            if (postVote.getValue() > 0) {
                likeCount++;
            }
            if (postVote.getValue() < 0) {
                dislikeCount++;
            }
        }
        return new int[]{likeCount, dislikeCount};
    }
}
