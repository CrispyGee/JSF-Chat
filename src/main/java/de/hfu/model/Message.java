package de.hfu.model;

import java.text.SimpleDateFormat;

/**
 * Created by IMTT on 12.04.2017.
 */
public class Message {

    private String content;
    private User sender;
    private MessageState messageState;
    private long timestamp;
    
    public Message(){
    	this.timestamp = System.currentTimeMillis();
    	this.messageState = MessageState.sent;
    }
    
    public Message(User sender, String content){
    	this.timestamp = System.currentTimeMillis();
    	this.messageState = MessageState.sent;
    	this.sender = sender;
    	this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
}
