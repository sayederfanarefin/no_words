package us.poptalks.model;

import java.util.List;

/**
 * Created by erfanarefin on 28/07/2017.
 */

public class ChatHeaderGroup {
    String lastMessage;
    String time;
    List<String> imageLocation;
    String chatName;
    String seen;

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    String chatId;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(List<String> imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
