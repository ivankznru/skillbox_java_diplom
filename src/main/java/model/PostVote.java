package model;

import java.util.Date;

public class PostVote {
    private int id; //id лайка/дизлайка
    private int userId; //тот, кто поставил лайк / дизлайк
    private int postId; //пост, которому поставлен лайк / дизлайк
    private Date time; //дата и время лайка / дизлайка
    private int value; //лайк или дизлайк: 1 или -1

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
