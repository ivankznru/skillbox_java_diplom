package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Optional<Image> getImageByImageName(String name);
}
