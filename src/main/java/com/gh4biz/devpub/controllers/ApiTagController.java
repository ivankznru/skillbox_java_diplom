package com.gh4biz.devpub.controllers;

import com.gh4biz.devpub.api.response.TagResponse;
import com.gh4biz.devpub.model.Post;
import com.gh4biz.devpub.model.Tag2Post;
import com.gh4biz.devpub.model.TagWeight;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.Tag2PostRepository;
import com.gh4biz.devpub.repo.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class ApiTagController {
    private TagRepository tagRepository;
    private PostRepository postRepository;
    private Tag2PostRepository tag2PostRepository;

    public ApiTagController(TagRepository tagRepository,
                            PostRepository postRepository,
                            Tag2PostRepository tag2PostRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tag2PostRepository = tag2PostRepository;
    }

    @GetMapping("/tag")
    private ResponseEntity<TagResponse> tagResponse(HttpServletRequest request) {
        String query = request.getParameter("query");

        Iterable<Tag2Post> tag2PostIterable = tag2PostRepository.findAll();
        HashMap<String, Integer> tagsHashMap = new HashMap<>();

        Iterable<Post> postIterable = postRepository.findAll();
        ArrayList<Post> posts = new ArrayList<>();
        postIterable.forEach(posts::add);

        int postsCount = posts.size();
        int max = 1;

        for (Tag2Post tag2Post : tag2PostIterable) {
            int count = 0;
//            int tagId = tag2Post.getTag().getId();
            String tagName = tag2Post.getTag().getName();

            if (tagsHashMap.containsKey(tagName)) {
                count = tagsHashMap.get(tagName);
                if (count == max) max++;
                tagsHashMap.replace(tagName, ++count);
                continue;
            }
            tagsHashMap.put(tagName, ++count);
        }
        double dWeightMax = (double) max / (double) postsCount;
        double kNormal = 1 / dWeightMax;
        ArrayList<TagWeight> tagWeights = new ArrayList<>();
        for (String key : tagsHashMap.keySet()) {
            double weight = ((double) tagsHashMap.get(key) / (double) postsCount) * kNormal;
            tagWeights.add(new TagWeight(key, weight));
        }
        TagResponse tagResponse = new TagResponse();
        tagResponse.setTagsList(tagWeights);
        return ResponseEntity.ok(tagResponse);
    }
}
