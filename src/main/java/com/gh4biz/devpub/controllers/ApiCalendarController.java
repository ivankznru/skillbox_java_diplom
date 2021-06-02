package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.model.ModerationStatus;
import com.gh4biz.devpub.model.response.PostsByYearResponse;
import com.gh4biz.devpub.repo.PostRepository;
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

    public ApiCalendarController(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @GetMapping("/calendar")
    public ResponseEntity<PostsByYearResponse> postsResponseByYear(
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
                    postRepository.countAllByIsActiveAndStatusAndTimeBetween(1, ModerationStatus.ACCEPTED, dateStart, dateEnd));
        }
        postsByYearResponse.setPosts(postsCountPerDay);
        return ResponseEntity.ok(postsByYearResponse);
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
}
