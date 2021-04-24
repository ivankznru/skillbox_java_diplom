package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private static PostRepository postRepository;
    private static PostCommentsRepository postCommentsRepository;
    private static PostVotesRepository postVotesRepository;
    private static PostService instance;
    private PostService(PostRepository postRepository,
                       PostCommentsRepository postCommentsRepository,
                       PostVotesRepository postVotesRepository) {
        this.postRepository = postRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.postVotesRepository = postVotesRepository;
    }

    public static PostRepository getPostRepository() {
        return postRepository;
    }

    public static PostCommentsRepository getPostCommentsRepository() {
        return postCommentsRepository;
    }

    public static PostVotesRepository getPostVotesRepository() {
        return postVotesRepository;
    }

    public static int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }
}
