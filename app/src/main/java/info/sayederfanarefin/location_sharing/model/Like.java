package info.sayederfanarefin.location_sharing.model;

/**
 * Created by erfanarefin on 31/08/2017.
 */

public class Like {
    private String userName;
    private String userId;
    private String likeType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikeType() {
        return likeType;
    }

    public void setLikeType(String likeType) {
        this.likeType = likeType;
    }
}
