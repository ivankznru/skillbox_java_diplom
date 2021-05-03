package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.entity.PostComment;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import com.gh4biz.devpub.repo.Tag2PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final PostVotesRepository postVotesRepository;
    private final Tag2PostRepository tag2PostRepository;

    private final int ANNOUNCE_TEXT_LIMIT = 150;
    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;
    private final int ACTIVE_POST_VALUE = 1;

    @Autowired
    private PostService(PostRepository postRepository,
                        PostCommentsRepository postCommentsRepository,
                        PostVotesRepository postVotesRepository,
                        Tag2PostRepository tag2PostRepository) {
        this.postRepository = postRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.postVotesRepository = postVotesRepository;
        this.tag2PostRepository = tag2PostRepository;
    }

    public int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    public Post findPostById(int id) {
        return postRepository.findPostsById(id);
    }

    public ArrayList<PostComment> getPostCommentsByPostId(int id) {
        return postCommentsRepository.findAllByPostId(id);
    }

    public ResponseEntity<PostsResponse> getPosts(int offset, int limit, String mode) {
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

    private PostsResponse popularPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();

        Slice<CommentCount> postCommentSlice =
                postCommentsRepository
                        .countTotalComments(
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

    public PostsResponse getSearch(String query, int offset, int limit) {
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

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
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
        return new PostsResponse(postAnnotationResponseList.size(), postAnnotationResponseList);
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
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
        return new PostsResponse(count, postAnnotationResponseList);
    }

}
