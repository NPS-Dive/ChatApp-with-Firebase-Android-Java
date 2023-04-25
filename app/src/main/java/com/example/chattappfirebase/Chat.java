package com.example.chattappfirebase;

public class Chat {
    private String sender;
    private String receiver;
    private String message;

    //constructors
    public Chat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Chat() {
    }

    //getter & setter
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //toString()
    @Override
    public String toString() {
        return "Chat{" +
                "sender = '" + sender + '\'' +
                ", receiver = '" + receiver + '\'' +
                ", message = '" + message + '\'' +
                '}';
    }
}
