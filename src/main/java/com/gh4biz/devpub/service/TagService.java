package com.gh4biz.devpub.service;

import com.gh4biz.devpub.model.TagWeight;
import com.gh4biz.devpub.model.entity.Tag;
import com.gh4biz.devpub.model.entity.Tag2Post;
import com.gh4biz.devpub.model.response.TagResponse;
import com.gh4biz.devpub.repo.PostRepository;
import com.gh4biz.devpub.repo.Tag2PostRepository;
import com.gh4biz.devpub.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final Tag2PostRepository tag2PostRepository;

    @Autowired
    public TagService(TagRepository tagRepository,
                      PostRepository postRepository,
                      Tag2PostRepository tag2PostRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.tag2PostRepository = tag2PostRepository;
    }

    public TagResponse getTags() {
        TagResponse tagResponse = new TagResponse();
        int postsCount = postRepository.countAllByIsActive(1);
        ArrayList<TagWeight> tagWeights = new ArrayList<>();
        ArrayList<Integer> orderedTagsIdByTagsCount = tag2PostRepository.getOrderedTags();
        int idMax = orderedTagsIdByTagsCount.get(0);
        double dWeightMax = (double) tag2PostRepository.countPostsByTagId(idMax) / postsCount;
        for (Integer tagId : orderedTagsIdByTagsCount) {
            int count = tag2PostRepository.countPostsByTagId(tagId);
            double dWeight = (double) count / postsCount;
            double kNormal = 1 / dWeightMax;
            double weight = dWeight * kNormal;
            tagWeights.add(new TagWeight(tagRepository.findById(tagId).get().getName(), weight));
        }
        tagResponse.setTagsList(tagWeights);
        return tagResponse;
    }

    public ArrayList<Tag2Post> getTagsByPostId(int postId){
       return tag2PostRepository.findAllByPostId(postId);
    }
}
