package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
}
