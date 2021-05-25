package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.MyStatistics;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import com.gh4biz.devpub.repo.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiStatisticsController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostVotesRepository postVotesRepository;


    public ApiStatisticsController(PostRepository postRepository,
                                   UserRepository userRepository,
                                   PostVotesRepository postVotesRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postVotesRepository = postVotesRepository;
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public MyStatistics myStatistics(Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        MyStatistics myStatistics = new MyStatistics();
        if (optionalUser.isPresent()) {
            User user = userRepository.findByEmail(principal.getName()).get();
            myStatistics.setPostsCount(postRepository.countByIsActiveAndUser(1, user));
            myStatistics.setLikesCount(postVotesRepository.countByUserAndValue(user, 1));
            myStatistics.setDislikesCount(postVotesRepository.countByUserAndValue(user, -1));
            myStatistics.setViewsCount(postRepository.countViews(user));
            myStatistics.setFirstPublication(postRepository.findFirstByUserOrderByTime(user).getTime().getTime() / 1000);

            return myStatistics;
        }
        return new MyStatistics();
    }

    @GetMapping("/statistics/all")
    public MyStatistics allStatistics(Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        MyStatistics myStatistics = new MyStatistics();
        if (optionalUser.isPresent()) {
            User user = userRepository.findByEmail(principal.getName()).get();
            myStatistics.setPostsCount(postRepository.countByIsActiveAndUser(1, user));
            myStatistics.setLikesCount(postVotesRepository.countByUserAndValue(user, 1));
            myStatistics.setDislikesCount(postVotesRepository.countByUserAndValue(user, -1));
            myStatistics.setViewsCount(postRepository.countViews(user));
            myStatistics.setFirstPublication(postRepository.findFirstByUserOrderByTime(user).getTime().getTime() / 1000);

            return myStatistics;
        }
        return new MyStatistics();
    }
}
