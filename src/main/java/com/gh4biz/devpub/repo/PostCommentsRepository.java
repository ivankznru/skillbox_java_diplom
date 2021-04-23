package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.CommentCount;
import com.gh4biz.devpub.model.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {
    int countAllByPostId(int id);
    //Slice<PostComment> findDistinctByPost_IsActive(int isActive, Pageable pageable);

    @Query("SELECT new com.gh4biz.devpub.model.CommentCount(p.post.id, COUNT(p.post.id)) "
            + "FROM PostComment AS p WHERE p.post.isActive = 1 GROUP BY p.post.id ORDER BY COUNT(p.post.id) DESC")
    Slice<CommentCount> countTotalComments(Pageable pageable);
}
