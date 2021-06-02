package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.entity.PostVote;
import com.gh4biz.devpub.model.entity.User;
import com.gh4biz.devpub.model.response.VoteCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {
    int countAllByPostIdAndValue(int id, int value);

    @Query("SELECT new com.gh4biz.devpub.model.response.VoteCount(p.post.id, COUNT(p.post.id)) "
            + "FROM PostVote AS p WHERE p.post.isActive = 1 and p.post.status = 'ACCEPTED' GROUP BY p.post.id ORDER BY COUNT(p.post.id) DESC")
    Slice<VoteCount> countTotalVote(Pageable pageable);


    @Query("SELECT new com.gh4biz.devpub.model.response.VoteCount(pv.post.id, SUM(pv.value)) "
            + "FROM PostVote AS pv WHERE pv.post.isActive = 1 and pv.post.status = 'ACCEPTED' GROUP BY pv.post.id ORDER BY SUM(pv.value) DESC")
    Slice<VoteCount> postsOrderByVoteSum (Pageable pageable);

    int countByUserAndValue(User user, int value);

    int countByValue(int value);

    Optional<PostVote> findByValueAndUserAndPost(int value, User user, Post post);

    Optional<PostVote> findByUserAndPost(User user, Post post);
}
