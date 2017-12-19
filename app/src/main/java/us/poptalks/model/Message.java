package us.poptalks.model;
/**
 * Created by schmaedech on 30/06/17.
 */
public class Message {

    private String messageSenderImage;
    private String messageSenderName;
    private String messageSenderUid;
    private String message;
    private String dataTime;
    private int messageSeenImage;
    private Boolean multimedia = false;
    private String contentType = "";
    private String contentLocation = "";




    public String getMessageSenderUid() {
        return messageSenderUid;
    }

    public void setMessageSenderUid(String messageSenderUid) {
        this.messageSenderUid = messageSenderUid;
    }

    public Message() {

    }

    public Message(String messageSenderName, String senderUid, String message, String contentType, String contentLocation, String dataTime) {
        this.messageSenderName = messageSenderName;
        this.message = message;
        this.multimedia = true;
        this.contentType = contentType;
        this.dataTime = dataTime;
        this.contentLocation = contentLocation;
        this.messageSenderUid = senderUid;
    }

    public String getMessageSenderImage() {
        return messageSenderImage;
    }

    public void setMessageSenderImage(String messageSenderImage) {
        this.messageSenderImage = messageSenderImage;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public void setMessageSenderName(String messageSenderName) {
        this.messageSenderName = messageSenderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public int getMessageSeenImage() {
        return messageSeenImage;
    }

    public void setMessageSeenImage(int messageSeenImage) {
        this.messageSeenImage = messageSeenImage;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public Boolean getMultimedia() {
        return multimedia;
    }

    public String getContentType() {
        return contentType;
    }

}
