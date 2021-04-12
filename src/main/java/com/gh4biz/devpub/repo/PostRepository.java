package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {
}
