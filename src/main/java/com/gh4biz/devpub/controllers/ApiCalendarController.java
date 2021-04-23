package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.PostsResponse;
import com.gh4biz.devpub.api.response.PostsByYearResponse;
import com.gh4biz.devpub.model.Post;
import com.gh4biz.devpub.model.Post4Response;
import com.gh4biz.devpub.model.User4Response;
import com.gh4biz.devpub.repo.PostCommentsRepository;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.PostVotesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiCalendarController {
    private final PostRepository postRepository;
    private PostVotesRepository postVotesRepository;
    private PostCommentsRepository postCommentsRepository;
    private final int ANNOUNCE_TEXT_LIMIT = 150;
    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;

    public ApiCalendarController(PostRepository postRepository,
                                 PostVotesRepository postVotesRepository,
                                 PostCommentsRepository postCommentsRepository) {
        this.postRepository = postRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
    }

    @GetMapping("/calendar")
    private ResponseEntity<PostsByYearResponse> postsResponseByYear(
            @RequestParam int year
    ) throws ParseException {
        String strStart = year + "-01-01 00:00:00";  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat sdf4response = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        LocalDate localDateStart = LocalDate.parse(strStart, formatter);
        Date dateStart = convertToDateViaInstant(localDateStart);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(strStart));

        int lengthOfYear = convertToLocalDateViaInstant(dateStart).lengthOfYear();
        PostsByYearResponse postsByYearResponse = new PostsByYearResponse();
        postsByYearResponse.setYears(postRepository.getYears());
        TreeMap<String, Integer> postsCountPerDay = new TreeMap<>();

        for (int i = 0; i < lengthOfYear; i++){
            dateStart = c.getTime();
            c.add(Calendar.DATE, 1);  // number of days to add
            Date dateEnd = c.getTime();

            postsCountPerDay.put(
                    sdf4response.format(dateStart),
                    postRepository.countAllByIsActiveAndTimeBetween(1, dateStart, dateEnd));
        }
        postsByYearResponse.setPosts(postsCountPerDay);
        return ResponseEntity.ok(postsByYearResponse);
    }

    @GetMapping("/post/byDate")
    private ResponseEntity<PostsResponse> postsResponseByDate(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        ArrayList<Integer> posts =
                postRepository.getPostsByDate(year, month, day,
                PageRequest.of(offset % limit, limit));

        ArrayList<Post4Response> post4ResponseList = new ArrayList<>();
        for (Integer postId : posts){
            Post post = postRepository.findPostsById(postId);
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
        return ResponseEntity.ok(new PostsResponse(post4ResponseList.size(), post4ResponseList));
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }
}
