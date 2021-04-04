package com.example.woochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private final List<Chat> chatList;
    private final OnItemClickListener onItemClickListener;

    public ChatAdapter(List<Chat> chatList, OnItemClickListener onItemClickListener) {
        this.chatList = chatList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View chatView = layoutInflater.inflate(R.layout.recycler_chat, parent, false);

        return new ChatHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ImageView imageView = holder.friendIcon;
        TextView textView = holder.friendName;
        TextView messageView = holder.message;

        Chat chat = chatList.get(position);
        String imageUrl = chat.imageUrl;

        Glide
                .with(imageView.getContext())
                .load(imageUrl)
                .thumbnail(Glide.with(imageView.getContext()).load(R.drawable.loading))
                .into(imageView);
        textView.setText(chat.friendName);
        messageView.setText(chat.messageContent);

        holder.onClick(chat, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {

        public ImageView friendIcon;
        public TextView friendName;
        public TextView message;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            friendIcon = itemView.findViewById(R.id.imageView_friend);
            friendName = itemView.findViewById(R.id.textView_friend_friendName);
            message = itemView.findViewById(R.id.textView_chatDetail);

        }

        public void onClick(Chat chat, OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(v -> onItemClickListener.setOnChatItemClickListener(chat));
        };
    }
}
