package com.gh4biz.devpub.repo;

import com.gh4biz.devpub.model.response.CommentCount;
import com.gh4biz.devpub.model.entity.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {
    int countAllByPostId(int id);
    //Slice<PostComment> findDistinctByPost_IsActive(int isActive, Pageable pageable);

    @Query("SELECT new com.gh4biz.devpub.model.response.CommentCount(p.post.id, COUNT(p.post.id)) "
            + "FROM PostComment AS p WHERE p.post.isActive = 1 GROUP BY p.post.id ORDER BY COUNT(p.post.id) DESC")
    Slice<CommentCount> countTotalComments(Pageable pageable);

    ArrayList<PostComment> findAllByPostId(int id);

    PostComment findPostCommentById(int id);

    Optional<PostComment> findTopByOrderByIdDesc();
}
