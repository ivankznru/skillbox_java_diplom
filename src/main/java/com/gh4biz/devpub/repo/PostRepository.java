package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.ModerationStatus;
import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Integer> {

    int countAllByIsActive(int value);

    Page<Post> findAll (Pageable pageable);

    Post findPostsById(int id);
    Optional<Post> findPostById(int id);

    Slice<Post> findAllByIsActiveAndStatusOrderByTimeDesc(int isActive, ModerationStatus status, Pageable pageable);
    Slice<Post> findAllByIsActiveAndStatusOrderByTimeAsc(int isActive, ModerationStatus status, Pageable pageable);
    Slice<Post> findAllByIsActiveAndStatus(int isActive, ModerationStatus status, Pageable pageable);

    int countAllByIsActiveAndStatusAndTimeBetween(
            int isActive,
            ModerationStatus status,
            Date publicationTimeStart,
            Date publicationTimeEnd);

    @Query("SELECT DISTINCT year(time) FROM Post AS year WHERE isActive = 1 and status = 'ACCEPTED' GROUP BY year")
    ArrayList<Integer> getYears();

    @Query("SELECT id FROM Post WHERE isActive = 1 and year(time) = ?1 and month(time) = ?2 and day(time) = ?3 order by time desc")
    ArrayList<Integer> getPostsByDate(int year, int month, int day, Pageable pageable);

    @Query("SELECT id FROM Post WHERE isActive = 1 and (text LIKE ?1 or title LIKE ?1) order by time desc")
    Page<Integer> getPostsBySearch(String search, Pageable pageable);

    @Query("SELECT count(*) FROM Post WHERE isActive = 1 and (text LIKE ?1 or title LIKE ?1) order by time desc")
    int countSearchPosts(String query);

    int countByIsActiveAndStatusAndModerator(Integer isActive, ModerationStatus status, User moderator);
    Slice<Post> findAllByIsActiveAndStatusAndModerator(int isActive, ModerationStatus status, User moderator, Pageable pageable);

    int countByIsActiveAndStatusAndUser(Integer isActive, ModerationStatus status, User user);
    Slice<Post> findAllByIsActiveAndStatusAndUserOrderByTimeDesc(int isActive, ModerationStatus status, User user, Pageable pageable);

    int countByIsActiveAndUser(int isActive, User user);

    @Query("SELECT SUM(viewCount) FROM Post WHERE isActive = 1 and user LIKE ?1")
    int countViews(User user);

    @Query("SELECT SUM(viewCount) FROM Post WHERE isActive = 1")
    int countAllViews();

    Post findFirstByUserAndIsActiveOrderByTime(User user, int isActive);

    List<Post> findPostByIsActiveOrderByTime(int isActive);

    int countAllByIsActiveAndStatus(int active_post_value, ModerationStatus status);
}
