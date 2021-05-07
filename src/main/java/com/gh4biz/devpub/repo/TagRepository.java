package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Integer> {
    Optional<Tag> findTagByName(String name);
}
