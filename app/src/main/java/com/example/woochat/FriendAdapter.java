package com.example.woochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

    private final List<User> friendList;
    private final OnItemClickListener onItemClickListener;

    public FriendAdapter(List<User> friendList, OnItemClickListener onItemClickListener) {
        this.friendList = friendList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View friendView = layoutInflater.inflate(R.layout.recycler_friends, parent, false);

        return new FriendHolder(friendView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        ImageView imageView = holder.friendIcon;
        TextView textView = holder.friendName;
        ProgressBar progressBar = holder.progressBar;

        User user = friendList.get(position);
        String imageUrl = user.imageUrl;

        new DownloadImageFromUrl(imageView, progressBar).execute(imageUrl);
        textView.setText(user.name);

        holder.onClick(user, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendHolder extends RecyclerView.ViewHolder {

        public ImageView friendIcon;
        public TextView friendName;
        public ProgressBar progressBar;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            friendIcon = itemView.findViewById(R.id.imageView_chat);
            friendName = itemView.findViewById(R.id.textView_chat_friendName);
            progressBar = itemView.findViewById(R.id.friend_progressBar);
        }

        /**
         * Set an onclick function for this holder.
         */
        public void onClick(User user, OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(v -> onItemClickListener.setOnItemClickListener(user));
        };
    }
}
