package info.sayederfanarefin.location_sharing.model;

/**
 * Created by erfan on 8/28/17.
 */

public class post {

    private String post_type;
    private String link;
    private String status_text;
    private String image_link;
    private String post_time;
    private String post_id;
    private String image_audio_link;
    private String user;
    private String userName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }




//    private List<String> likes;
//    private List<comments> comments;

//    public List<String> getLikes() {
//        return likes;
//    }
//
//    public void setLikes(List<String> likes) {
//        this.likes = likes;
//    }
//
//    public void addLike(String uid){
//        likes.add(uid);
//    }
//
//    public List<comments> getComments() {
//        return comments;
//    }
//
//    public void setComments(List<comments> comments) {
//        this.comments = comments;
//    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }




    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getImage_audio_link() {
        return image_audio_link;
    }

    public void setImage_audio_link(String image_audio_link) {
        this.image_audio_link = image_audio_link;
    }
}
