package us.poptalks.model;

/**
 * Created by erfan on 8/28/17.
 */

public class comments {
    private String comment;
    private String comment_user_id;
    private String comment_user_name;
    private String comment_time;
    private String comment_type; //image or text
    private String comment_image_url;

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public String getComment_image_url() {
        return comment_image_url;
    }

    public void setComment_image_url(String comment_image_url) {
        this.comment_image_url = comment_image_url;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getComment_user_name() {
        return comment_user_name;
    }

    public void setComment_user_name(String comment_user_name) {
        this.comment_user_name = comment_user_name;
    }
}
