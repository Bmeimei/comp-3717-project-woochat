package com.example.woochat;

import java.util.Objects;

public class Chat {
    public String messageId;
    public String friendName;
    public String imageUrl;
    public String friendId;
    public String userId;
    public String messageContent;

    public Chat(String messageId, String friendName, String imageUrl, String friendId, String userId, String messageContent) {
        this.messageId = messageId;
        this.friendName = friendName;
        this.imageUrl = imageUrl;
        this.friendId = friendId;
        this.userId = userId;
        this.messageContent = messageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return friendId.equals(chat.friendId) &&
                userId.equals(chat.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendId, userId);
    }
}
