package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.Tag;
import com.gh4biz.devpub.model.entity.Tag2Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface Tag2PostRepository extends CrudRepository<Tag2Post ,Integer> {

    @Query("SELECT post.id FROM Tag2Post WHERE tag.name LIKE ?1 AND post.isActive = 1 ORDER BY post.time DESC")
    ArrayList<Integer> getPostsByQuery(String query, Pageable pageable);

    @Query("SELECT post.id FROM Tag2Post WHERE post.isActive = 1 GROUP BY post.id ORDER BY post.time DESC")
    ArrayList<Integer> getPosts();

    @Query("SELECT count(*) FROM Tag2Post WHERE post.isActive = 1 and tag.id like ?1")
    int countPostsByTagId(Integer id);

    @Query("SELECT count(*) FROM Tag2Post WHERE post.isActive = 1 and tag.name like ?1")
    int countPostsByTagName(String tagName);

    @Query("SELECT tag.id FROM Tag2Post WHERE post.isActive = 1 GROUP BY tag.id ORDER BY count(*) DESC")
    ArrayList<Integer> getOrderedTags();

    ArrayList<Tag2Post> findAllByPostId(int postId);

    //Optional<Tag> getT
}
