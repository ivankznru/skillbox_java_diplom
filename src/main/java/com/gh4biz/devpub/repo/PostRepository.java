package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;

public interface PostRepository extends CrudRepository<Post, Integer> {

    int countAllByIsActive(int value);

    Page<Post> findAll (Pageable pageable);

    Post findPostsById(int id);

    Slice<Post> findAllByIsActiveOrderByTimeDesc(int isActive, Pageable pageable);
    Slice<Post> findAllByIsActiveOrderByTimeAsc(int isActive, Pageable pageable);

    int countAllByIsActiveAndTimeBetween(
            int isActive,
            Date publicationTimeStart,
            Date publicationTimeEnd);

    @Query("SELECT DISTINCT year(time) FROM Post AS year GROUP BY year")
    ArrayList<Integer> getYears();

    @Query("SELECT id FROM Post WHERE isActive = 1 and year(time) = ?1 and month(time) = ?2 and day(time) = ?3 order by time desc")
    ArrayList<Integer> getPostsByDate(int year, int month, int day, Pageable pageable);

    @Query("SELECT id FROM Post WHERE isActive = 1 and (text LIKE ?1 or title LIKE ?1) order by time desc")
    Page<Integer> getPostsBySearch(String search, Pageable pageable);

    @Query("SELECT count(*) FROM Post WHERE isActive = 1 and (text LIKE ?1 or title LIKE ?1) order by time desc")
    int countSearchPosts(String query);
}
