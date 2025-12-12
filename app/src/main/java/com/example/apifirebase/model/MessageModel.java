package com.example.apifirebase.model;

public class MessageModel {
    private String content;
    private String senderid;

    public MessageModel(String content, String senderid) {
        this.content = content;
        this.senderid = senderid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }
}
