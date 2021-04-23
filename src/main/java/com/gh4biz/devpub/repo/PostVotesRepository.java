package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.PostVote;
import com.gh4biz.devpub.model.VoteCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {
    int countAllByPostIdAndValue(int id, int value);

    @Query("SELECT new com.gh4biz.devpub.model.VoteCount(p.post.id, COUNT(p.post.id)) "
            + "FROM PostVote AS p WHERE p.post.isActive = 1 and value = ?1 GROUP BY p.post.id ORDER BY COUNT(p.post.id) DESC")
    Slice<VoteCount> countTotalVote(int value, Pageable pageable);
}
