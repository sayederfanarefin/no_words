package info.sayederfanarefin.location_sharing.model;

/**
 * Created by erfanarefin on 28/07/2017.
 */

public class Message_2 {
    private String content;
    private String message_seen;
    private String receiver_uid;
    private String recipient_mood;
    private String sender_image_location;
    private String sender_name;
    private String sender_uid;
    private String timestamp;
    private String type;
    private String id;

    public Message_2(){
        message_seen = "un_seen";
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getContent_location() {
        return content_location;
    }

    public void setContent_location(String content_location) {
        this.content_location = content_location;
    }

    private String content_location;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage_seen() {
        return message_seen;
    }

    public void setMessage_seen(String message_seen) {
        this.message_seen = message_seen;
    }

    public String getReceiver_uid() {
        return receiver_uid;
    }

    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }

    public String getRecipient_mood() {
        return recipient_mood;
    }

    public void setRecipient_mood(String recipient_mood) {
        this.recipient_mood = recipient_mood;
    }

    public String getSender_image_location() {
        return sender_image_location;
    }

    public void setSender_image_location(String sender_image_location) {
        this.sender_image_location = sender_image_location;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_uid() {
        return sender_uid;
    }

    public void setSender_uid(String sender_uid) {
        this.sender_uid = sender_uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
