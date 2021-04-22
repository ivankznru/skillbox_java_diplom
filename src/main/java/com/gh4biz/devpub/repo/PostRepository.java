package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface PostRepository extends CrudRepository<Post, Integer> {

    int countAllByIsActive(int value);

    Page<Post> findAll (Pageable pageable);

    Post findById(int id);

    int countAllByIsActiveAndTimeBetween(
            int isActive,
            Date publicationTimeStart,
            Date publicationTimeEnd);

//    @Query("SELECT new com.gh4biz.devpub.model.PostsCountPerDay(p.post.id, COUNT(p.post.id)) "
//            + "FROM PostComment AS p WHERE p.post.isActive = 1 GROUP BY p.post.id ORDER BY COUNT(p.post.id) DESC")
//    Slice<PostsCountPerDay> countPostsPerDay(Pageable pageable);
}
