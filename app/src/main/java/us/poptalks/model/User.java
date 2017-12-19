package us.poptalks.model;
/**
 * Created by schmaedech on 30/06/17.
 */
public class User {

    private String username;
    private String email;
    private String profilePicLocation;
    private String phoneNumber;


    public User() {

    }

    public User(String name, String email) {
        this.username = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

}
