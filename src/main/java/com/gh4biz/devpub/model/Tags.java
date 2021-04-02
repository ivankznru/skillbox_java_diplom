package com.gh4biz.devpub.model;

import java.util.List;

public class Tags {
    private List<Tag> tags;
    private double weiht;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public double getWeiht() {
        return weiht;
    }

    public void setWeiht(double weiht) {
        this.weiht = weiht;
    }
}
