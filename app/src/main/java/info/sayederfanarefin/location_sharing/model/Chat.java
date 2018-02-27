package info.sayederfanarefin.location_sharing.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by schmaedech on 30/06/17.
 */
public class Chat {

    private String uid;
    private String chatName;
    //private List<Message> messages;
    //private List<Friend> friends;
    private List<String> friends;

    public Chat() {

    }



    public Chat(String chatName, List<String> added_users1) {

        this.uid = uid;
        this.chatName = chatName;
        //this.messages = new ArrayList<Message>();
        this.friends = new ArrayList<String>();

        for(int i =0; i < added_users1.size(); i++){

            appendFriend(added_users1.get(i));
        }
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    //public List<Message> getMessages() {
    //    return messages;
    //}

    public List<String> getFriends() {
        return friends;
    }

    public boolean appendFriend(String friend) {
        Boolean contFriend = friends.contains(friend);
        if (!contFriend) {
            friends.add(friend);
            return true;
        }
        return false;
    }

    public boolean removeFriend(Friend friend) {
        friends.remove(friend);
        return true;
    }

}
