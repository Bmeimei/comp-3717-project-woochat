package com.example.woochat;

import java.util.Date;

public class Message {
    public final String messageId;
    public final String senderId;
    public final String receiverId;
    public final String messageContent;
    public final Date time;

    public Message(String messageId,
                   String senderId,
                   String receiverId,
                   String messageContent,
                   Date time) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.time = time;
    }
}
