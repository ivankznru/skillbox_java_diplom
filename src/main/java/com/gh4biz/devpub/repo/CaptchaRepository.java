package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.CaptchaCode;
import org.springframework.data.repository.CrudRepository;

public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {
    CaptchaCode findBySecretCode(String secret);
}
