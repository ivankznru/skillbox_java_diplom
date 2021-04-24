package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.Tag2Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface Tag2PostRepository extends CrudRepository<Tag2Post ,Integer> {

    @Query("select post.id from Tag2Post where tag.name like ?1 and post.isActive = 1 order by post.time desc")
    ArrayList<Integer> getPosts(String query, Pageable pageable);

    @Query("SELECT count(*) FROM Tag2Post WHERE post.isActive = 1 and tag.name like ?1")
    int countTagPosts(String query);
}
