package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.ModerationStatus;
import com.gh4biz.devpub.model.entity.*;
import com.gh4biz.devpub.model.request.PostEditForm;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.repo.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final PostVotesRepository postVotesRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Value("${blogAnnounceTextLimit}")
    private Integer blogAnnounceTextLimit;

    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;
    private final int ACTIVE_POST_VALUE = 1;

    public int getVoteCount(Post post, int vote) {
        return postVotesRepository.countAllByPostIdAndValue(post.getId(), vote);
    }

    public Post findPostById(int id) {
        return postRepository.findPostsById(id);
    }

    public ArrayList<PostComment> getPostCommentsByPostId(int id) {
        return postCommentsRepository.findAllByPostId(id);
    }

    public ResponseEntity<PostsResponse> getPosts(int offset, int limit, String mode) {
        if (mode.equals("popular")) {
            return ResponseEntity.ok(popularPosts(offset, limit));
        }
        if (mode.equals("best")) {
            return ResponseEntity.ok(bestPosts(offset, limit));
        }
        if (mode.equals("recent")) {
            return ResponseEntity.ok(recentPosts(offset, limit));
        }
        if (mode.equals("early")) {
            return ResponseEntity.ok(earlyPosts(offset, limit));
        }
        return ResponseEntity.noContent().build();
    }

    private PostsResponse popularPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();

        Slice<CommentCount> postCommentSlice =
                postCommentsRepository
                        .countTotalComments(
                                PageRequest.of(offset / limit, limit));

        for (CommentCount comment : postCommentSlice) {
            Post post = postRepository.findPostsById(comment.getId());
            postAnnotationResponseList.add(convert2Post4Response(post));
        }

        return new PostsResponse(postCommentSlice.getContent().size(),
                postAnnotationResponseList);
    }

    private PostsResponse bestPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<VoteCount> voteCounts = postVotesRepository.countTotalVote(
                LIKE_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (VoteCount vote : voteCounts) {
            Post post = postRepository.findPostsById(vote.getId());
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return new PostsResponse(voteCounts.getSize(), postAnnotationResponseList);
    }

    private PostsResponse recentPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeDesc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        int count = postRepository.countAllByIsActive(ACTIVE_POST_VALUE);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    private PostsResponse earlyPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveOrderByTimeAsc(
                ACTIVE_POST_VALUE,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return new PostsResponse(postRepository.countAllByIsActive(ACTIVE_POST_VALUE), postAnnotationResponseList);
    }

    private PostAnnotationResponse convert2Post4Response(Post post) {
        Document doc = Jsoup.parse(post.getText());
        String text = doc.text();
        String announce = text.substring(0, Math.min(blogAnnounceTextLimit, text.length()));
        return new PostAnnotationResponse(post.getId(),
                post.getTime().getTime() / 1000,
                new UserResponse(
                        post.getUser().getId(),
                        post.getUser().getName()),
                post.getTitle(),
                announce.concat("..."),
                getVoteCount(post, LIKE_VALUE),
                getVoteCount(post, DISLIKE_VALUE),
                getCommentsCount(post),
                post.getViewCount());
    }

    public PostsResponse getSearch(String query, int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        String query4sql = "%".concat(query).concat("%");
        Page<Integer> posts = postRepository.getPostsBySearch(
                query4sql,
                PageRequest.of(offset / limit, limit)
        );

        for (Post post : postRepository.findAllById(posts)){
            postAnnotationResponseList.add(convert2Post4Response(post));
        }

        int count = postRepository.countSearchPosts(query4sql);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    private int getCommentsCount(Post post) {
        return postCommentsRepository.countAllByPostId(post.getId());
    }

    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();

        ArrayList<Integer> posts =
                postRepository.getPostsByDate(year, month, day,
                        PageRequest.of(offset / limit, limit));

        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Integer postId : posts) {
            Post post = postRepository.findPostsById(postId);
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return new PostsResponse(postAnnotationResponseList.size(), postAnnotationResponseList);
    }

    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        String tag2resp = "%".concat(tag).concat("%");
        ArrayList<Integer> posts =
                tag2PostRepository.getPostsByQuery(
                        tag2resp,
                        PageRequest.of(offset / limit, limit));

        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();

        for (Post post : postRepository.findAllById(posts)) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }

        int count = tag2PostRepository.countPostsByTagName(tag2resp);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    public PostResponse getPostById(int id) {
        Post post = postRepository.findPostsById(id);
        PostResponse postResponse = new PostResponse();

        postResponse.setId(post.getId());
        postResponse.setTimestamp(post.getTime().getTime() / 1000);
        if (post.getIsActive() == 1) {
            postResponse.setActive(true);
        }
        postResponse.setUser(new UserResponse(
                post.getUser().getId(),
                post.getUser().getName()));
        postResponse.setTitle(post.getTitle());
        postResponse.setText(post.getText());
        postResponse.setLikeCount(getVoteCount(post, 1));
        postResponse.setLikeCount(getVoteCount(post, -1));
        postResponse.setViewCount(post.getViewCount());
        postResponse.setComments(getComments(id));
        return postResponse;
    }

    private ArrayList<CommentResponse> getComments(int postId) {
        ArrayList<CommentResponse> commentResponseArrayList = new ArrayList<>();
        ArrayList<PostComment> commentsIdList = getPostCommentsByPostId(postId);
        for (PostComment postComment : commentsIdList) {
            //PostComment postComment = PostService.getPostCommentsRepository().findPostCommentById(commentId);
            commentResponseArrayList.add(
                    new CommentResponse(
                            postComment.getId(),
                            postComment.getTime().getTime() / 1000,
                            postComment.getText(),
                            new UserWithPhotoResponse(
                                    postComment.getUser().getId(),
                                    postComment.getUser().getName(),
                                    postComment.getUser().getPhoto())));
        }
        return commentResponseArrayList;
    }

    public ResponseEntity<PostsResponse> getModerationPosts(int offset, int limit, String status, Principal principal) {
        User moderator = userRepository.findByEmail(principal.getName()).get();
        int count = postRepository.countByIsActiveAndStatusAndModerator(1, ModerationStatus.NEW, moderator);
        Slice<Post> posts = postRepository.findAllByIsActiveAndStatusAndModerator(
                1,
                ModerationStatus.NEW,
                moderator,
                PageRequest.of(offset / limit, limit));

        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }

        return ResponseEntity.ok(new PostsResponse(count, postAnnotationResponseList));
    }

    public ResponseEntity<PostsResponse> getMyPosts(int offset, int limit, String status, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        int isActive = status.equals("inactive") ? 0 : 1;
        int count = postRepository.countByIsActiveAndStatusAndUser(isActive, ModerationStatus.getByStatus(status), user);
        Slice<Post> posts = postRepository.findAllByIsActiveAndStatusAndUserOrderByTimeDesc(
                isActive,
                ModerationStatus.getByStatus(status),
                user,
                PageRequest.of(offset / limit, limit));
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        return ResponseEntity.ok(new PostsResponse(count, postAnnotationResponseList));
    }

    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postAdd(PostEditForm form, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();

        if (!checkPost(form).isEmpty()){
            return ResponseEntity.ok(new PostUpdateEditUploadErrorsResponse(false, checkPost(form)));
        }

        Post post = new Post(
                form.getActive(),
                ModerationStatus.NEW,
                user,
                new Date(form.getTimestamp() * 1000),
                form.getTitle(),
                form.getText());
        postRepository.save(post);
        for (String tagName : form.getTags()) {
            Optional<Tag> optionalTag = tagRepository.findTagByName(tagName);
            Tag2Post tag2Post = optionalTag.isPresent() ?
                    new Tag2Post(post, optionalTag.get()) :
                    new Tag2Post(post, new Tag(tagName));
            tag2PostRepository.save(tag2Post);
        }
        return ResponseEntity.ok(new PostUpdateEditUploadErrorsResponse(true));
    }

    public ResponseEntity<PostUpdateEditUploadErrorsResponse> postEdit(int id, PostEditForm form, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        if (!checkPost(form).isEmpty()){
            return ResponseEntity.ok(new PostUpdateEditUploadErrorsResponse(false, checkPost(form)));
        }

        Post post = postRepository.findPostsById(id);
        post.setIsActive(form.getActive());
        if (!post.getModerator().equals(user)) {
            post.setStatus(ModerationStatus.NEW);
        }

        post.setTime(new Date(form.getTimestamp() * 1000));
        post.setTitle(form.getTitle());
        post.setText(form.getText());
        postRepository.save(post);

        for (String tagName : form.getTags()) {
            Optional<Tag> optionalTag = tagRepository.findTagByName(tagName);
            Tag2Post tag2Post = optionalTag.isPresent() ?
                    new Tag2Post(post, optionalTag.get()) :
                    new Tag2Post(post, new Tag(tagName));
            tag2PostRepository.save(tag2Post);
        }

        return ResponseEntity.ok(new PostUpdateEditUploadErrorsResponse(true));
    }

    private HashMap<String, String> checkPost (PostEditForm form){
        Calendar calendar = Calendar.getInstance();
        long formTimestamp = form.getTimestamp();
        long currentTimestamp = calendar.toInstant().getEpochSecond();

        HashMap<String, String> errors = new HashMap<>();

        if ((currentTimestamp - formTimestamp) > 600) {
            errors.put("timestamp", "Проверьте дату публикации");
        }
        if ((form.getTitle().isEmpty()) || (form.getTitle().length() < 3)) {
            errors.put("title", "Текст заголовка менее трёх символов!");
        }
        if ((form.getText().isEmpty()) || (form.getText().length() < 50)) {
            errors.put("text", "Текст поста менее 50 символов!");
        }
        return errors;
    }
}
