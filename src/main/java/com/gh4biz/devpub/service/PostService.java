package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.ModerationStatus;
import com.gh4biz.devpub.model.entity.*;
import com.gh4biz.devpub.model.request.VoteForm;
import com.gh4biz.devpub.model.request.PostEditForm;
import com.gh4biz.devpub.model.response.*;
import com.gh4biz.devpub.repo.*;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.sound.midi.Soundbank;
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
    private final GlobalSettingsRepository globalSettingsRepository;

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
            if ((post.getIsActive() == 1) && (post.getStatus().equals(ModerationStatus.ACCEPTED))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
        }
        return new PostsResponse(postCommentSlice.getContent().size(),
                postAnnotationResponseList);
    }

    private PostsResponse bestPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<VoteCount> voteCounts = postVotesRepository.postsOrderByVoteSum(
                PageRequest.of(offset / limit, limit));
        for (VoteCount vote : voteCounts) {
            Post post = postRepository.findPostsById(vote.getId());
            if ((post.getIsActive() == 1) && (post.getStatus().equals(ModerationStatus.ACCEPTED))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
        }
        return new PostsResponse(voteCounts.getSize(), postAnnotationResponseList);
    }

    private PostsResponse recentPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveAndStatusOrderByTimeDesc(
                ACTIVE_POST_VALUE,
                ModerationStatus.ACCEPTED,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            postAnnotationResponseList.add(convert2Post4Response(post));
        }
        int count = postRepository.countAllByIsActiveAndStatus(ACTIVE_POST_VALUE, ModerationStatus.ACCEPTED);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    private PostsResponse earlyPosts(int offset, int limit) {
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        Slice<Post> posts = postRepository.findAllByIsActiveAndStatusOrderByTimeAsc(
                ACTIVE_POST_VALUE,
                ModerationStatus.ACCEPTED,
                PageRequest.of(offset / limit, limit)
        );
        for (Post post : posts) {
            if ((post.getIsActive() == 1) && (post.getStatus().equals(ModerationStatus.ACCEPTED))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
        }
        return new PostsResponse(postRepository.countAllByIsActiveAndStatus(ACTIVE_POST_VALUE, ModerationStatus.ACCEPTED), postAnnotationResponseList);
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

        for (Post post : postRepository.findAllById(posts)) {
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
            if ((post.getIsActive() == 1) && (post.getStatus().equals(ModerationStatus.ACCEPTED))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
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
            if ((post.getIsActive() == 1) && (post.getStatus().equals(ModerationStatus.ACCEPTED))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
        }

        int count = tag2PostRepository.countPostsByTagName(tag2resp);
        return new PostsResponse(count, postAnnotationResponseList);
    }

    public PostResponse getPostById(int postId, Principal principal) {
        Optional<Post> postOptional = postRepository.findPostById(postId);
        if (postOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пост не найден!");
        }
        Post post = postOptional.get();
        Optional<User> optionalUser = Optional.empty();

        if (principal != null) optionalUser = userRepository.findByEmail(principal.getName());
        // увеличиваем просмотры если юзер не авторизован
        if (optionalUser.isEmpty()) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        // увеличиваем просмотры если юзер не модератор и не автор поста
        if (optionalUser.isPresent()
                && (optionalUser.get().getIsModerator() != 1)
                && (!optionalUser.get().getId().equals(post.getUser().getId()))) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
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
        postResponse.setDislikeCount(getVoteCount(post, -1));
        postResponse.setViewCount(post.getViewCount());
        postResponse.setComments(getComments(postId));

        ArrayList<String> tags = new ArrayList<>();
        for (Tag2Post tag2Post : tag2PostRepository.findAllByPostId(postId)) {
            Tag tag = tagRepository.findById(tag2Post.getTag().getId()).get();
            tags.add(tag.getName());
        }
        postResponse.setTags(tags);

        return postResponse;
    }

    private ArrayList<CommentResponse> getComments(int postId) {
        ArrayList<CommentResponse> commentResponseArrayList = new ArrayList<>();
        ArrayList<PostComment> commentsIdList = getPostCommentsByPostId(postId);
        for (PostComment postComment : commentsIdList) {
            if (postComment.getParent() != null) {
                commentResponseArrayList.add(
                        new CommentResponse(
                                postComment.getId(),
                                postComment.getTime().getTime() / 1000,
                                postComment.getParent().getId(),
                                postComment.getText(),
                                new UserWithPhotoResponse(
                                        postComment.getUser().getId(),
                                        postComment.getUser().getName(),
                                        postComment.getUser().getPhoto())));
            } else
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
        ModerationStatus moderationStatus = ModerationStatus.NEW;
        if (status.equals("declined")) {
            moderationStatus = ModerationStatus.DECLINED;
        }
        if (status.equals("accepted")) {
            moderationStatus = ModerationStatus.ACCEPTED;
        }

        int count = postRepository.countByIsActiveAndStatusAndModerator(1, moderationStatus, moderator);
        Slice<Post> posts = postRepository.findAllByIsActiveAndStatus(
                1,
                moderationStatus,
                PageRequest.of(offset / limit, limit));
        ArrayList<PostAnnotationResponse> postAnnotationResponseList = new ArrayList<>();
        for (Post post : posts) {
            if ((post.getIsActive() == 1) && (post.getStatus().equals(moderationStatus))) {
                postAnnotationResponseList.add(convert2Post4Response(post));
            }
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

    public ResponseEntity<PostUpdateEditResponse> postAdd(PostEditForm form, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        if (!checkPost(form).isEmpty()) {
            return ResponseEntity.ok(new PostUpdateEditResponse(false, checkPost(form)));
        }

        Post post = new Post(
                form.getActive(),
                ModerationStatus.NEW,
                user,
                new Date(form.getTimestamp() * 1000),
                form.getTitle(),
                form.getText());

        String premoderationSettings = globalSettingsRepository.findByCode("POST_PREMODERATION").getValue();

        ModerationStatus status = premoderationSettings.equals("YES") ? ModerationStatus.NEW : ModerationStatus.ACCEPTED;
        post.setStatus(status);

        postRepository.save(post);
        //System.out.println(form.getTags().size());
        for (String tagName : form.getTags()) {

            Optional<Tag> optionalTag = tagRepository.getAllByName(tagName);

            System.out.println(optionalTag);

            Tag2Post tag2Post = optionalTag.map(tag -> new Tag2Post(post, tag)).orElseGet(() -> new Tag2Post(post, new Tag(tagName)));
            tag2PostRepository.save(tag2Post);
        }
        return ResponseEntity.ok(new PostUpdateEditResponse(true));
    }

    public ResponseEntity<PostUpdateEditResponse> postEdit(int id, PostEditForm form, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        if (!checkPost(form).isEmpty()) {
            return ResponseEntity.ok(new PostUpdateEditResponse(false, checkPost(form)));
        }

        Post post = postRepository.findPostsById(id);
        post.setIsActive(form.getActive());

        if (user.getIsModerator() == 0) {
            post.setStatus(ModerationStatus.NEW);
        }

        post.setTime(new Date());
        post.setTitle(form.getTitle());
        post.setText(form.getText());
        postRepository.save(post);

        ArrayList<Tag2Post> tag2posts = tag2PostRepository.findAllByPostId(post.getId());

        if (tag2posts.isEmpty()) {
            for (String tagName : form.getTags()) {
                Optional<Tag> optionalTag = tagRepository.getAllByName(tagName);
                Tag tag;
                if (optionalTag.isEmpty()) {
                    tag = new Tag(tagName);
                    tagRepository.save(tag);
                } else {
                    tag = optionalTag.get();
                }
                tag2PostRepository.save(new Tag2Post(post, tag));
            }
        } else {
            for (Tag2Post tag2post : tag2posts) {
                String tagName = tag2post.getTag().getName();
                if (!form.getTags().contains(tagName)) {
                    tag2PostRepository.delete(tag2post);
                }
            }
            for (String item : form.getTags()) {
                Optional<Tag> optionalTag = tagRepository.getAllByName(item);
                Tag tag;
                if (optionalTag.isEmpty()) {
                    tag = new Tag(item);
                    tagRepository.save(tag);
                } else {
                    tag = optionalTag.get();
                }
                addTag2Post(post, tag);
            }
        }
        return ResponseEntity.ok(new PostUpdateEditResponse(true));
    }

    private void addTag2Post(Post post, Tag tag) {
        Optional<Tag2Post> tag2Post = tag2PostRepository.findTag2PostsByPostAndTag(post, tag);
        if (tag2Post.isEmpty()) {
            Tag2Post t2p = new Tag2Post(post, tag);
            tag2PostRepository.save(t2p);
        }
    }

    private HashMap<String, String> checkPost(PostEditForm form) {
        Calendar calendar = Calendar.getInstance();
        long formTimestamp = form.getTimestamp();
        long currentTimestamp = calendar.toInstant().getEpochSecond();

        HashMap<String, String> errors = new HashMap<>();

//        if ((currentTimestamp - formTimestamp) > 600) {
//            errors.put("timestamp", "Проверьте дату публикации");
//        }
        if ((form.getTitle().isEmpty()) || (form.getTitle().length() < 3)) {
            errors.put("title", "Текст заголовка менее трёх символов!");
        }
        if ((form.getText().isEmpty()) || (form.getText().length() < 50)) {
            errors.put("text", "Текст поста менее 50 символов!");
        }
        return errors;
    }

    public ResponseEntity<CommentAddResult> commentAdd(Integer parentId, Integer postId, String text, Principal principal) {
        PostComment parent = null;
        if (parentId != null) {
            parent = postCommentsRepository.findPostCommentById(parentId);
        }

        User user = userRepository.findByEmail(principal.getName()).get();
        PostComment postComment = new PostComment(
                parent,
                postRepository.findPostsById(postId),
                user,
                new Date(),
                text);
        postCommentsRepository.save(postComment);
        Optional<PostComment> commentId = postCommentsRepository.findTopByOrderByIdDesc();
        return ResponseEntity.ok(new CommentAddResult(commentId.get().getId()));
    }

    public boolean moderate(int postId, String decision, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        Post post = postRepository.findPostsById(postId);
        if (user.getIsModerator() != 1) {
            return false;
        }
        post.setModerator(user);
        if (decision.equals("accept")) {
            post.setStatus(ModerationStatus.ACCEPTED);
        }

        if (decision.equals("decline")) {
            post.setStatus(ModerationStatus.DECLINED);
        }

        postRepository.save(post);
        return true;
    }

    public boolean like(VoteForm form, Principal principal) {
        return vote(1, form, principal);
    }

    public boolean dislike(VoteForm form, Principal principal) {
        return vote(-1, form, principal);
    }

    private boolean vote(int vote, VoteForm form, Principal principal) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getName());
        User user = userOptional.get();
        Post post = postRepository.findPostsById(form.getPostId());
        Optional<PostVote> optionalPostVote = postVotesRepository.findByUserAndPost(user, post);
        if (optionalPostVote.isEmpty()) {
            PostVote postVote = new PostVote();
            postVote.setPost(post);
            postVote.setUser(user);
            postVote.setTime(new Date());
            postVote.setValue(vote);
            postVotesRepository.save(postVote);
            return true;
        }
        PostVote postVote = optionalPostVote.get();
        if (postVote.getValue() == vote) {
            return false;
        }
        postVote.setValue(vote);
        postVotesRepository.save(postVote);
        return true;
    }

}
