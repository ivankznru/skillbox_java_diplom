package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.PostComment;
import org.springframework.data.repository.CrudRepository;

public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {
}
