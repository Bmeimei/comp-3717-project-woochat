package com.example.woochat;

import java.util.Date;

public class Message {
    final String messageId;
    final String senderId;
    final String receiverId;
    final String messageContent;
    final Date time;

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
