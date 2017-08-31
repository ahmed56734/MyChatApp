package com.example.ahmed.mychatapp;

/**
 * Created by ahmed on 8/23/17.
 */

public class Message {

    private String sender;
    private String message;
    private Long date ;

    public Message(){

    }

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.date = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
