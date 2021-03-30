package model;

import java.util.Date;

public class PostComment {
    private int id; //id комментария
    private int parentId; //комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)
    private int postId; //пост, к которому написан комментарий
    private int userId; //автор комментария
    private Date time; //дата и время комментария
    private String text; //текст комментария

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
