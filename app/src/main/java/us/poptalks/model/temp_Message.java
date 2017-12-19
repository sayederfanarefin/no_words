package us.poptalks.model;
/**
 * Created by schmaedech on 30/06/17.
 */
public class temp_Message {

    private int messageSenderImage;
    private String messageSenderName;
    private String lastMessage;
    private String dataTime;
    private int messageSeenImage;
    private Boolean multimedia = false;
    private String contentType = "";
    private String contentLocation = "";

    public temp_Message() {

    }

    public temp_Message(int messageSenderImage, String messageSenderName, String lastMessage, String dataTime, int messageSeenImage) {
        this.messageSenderImage = messageSenderImage;
        this.messageSenderName = messageSenderName;
        this.lastMessage = lastMessage;
        this.dataTime = dataTime;
        this.messageSeenImage = messageSeenImage;
    }

    public temp_Message(String messageSenderName, String lastMessage, String contentType, String contentLocation, String dataTime) {
        this.messageSenderName = messageSenderName;
        this.lastMessage = lastMessage;
        this.multimedia = true;
        this.contentType = contentType;
        this.dataTime = dataTime;
        this.contentLocation = contentLocation;
    }

    public int getMessageSenderImage() {
        return messageSenderImage;
    }

    public void setMessageSenderImage(int messageSenderImage) {
        this.messageSenderImage = messageSenderImage;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public void setMessageSenderName(String messageSenderName) {
        this.messageSenderName = messageSenderName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
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
