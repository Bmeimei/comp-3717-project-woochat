package com.example.woochat;

import java.util.Date;

public class Message {
    public String messageId;
    public String senderId;
    public String receiverId;
    public String messageContent;
    public String time;

    public Message(String messageId,
                   String senderId,
                   String receiverId,
                   String messageContent,
                   String time) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.time = time;
    }

    public Message() {}
}
