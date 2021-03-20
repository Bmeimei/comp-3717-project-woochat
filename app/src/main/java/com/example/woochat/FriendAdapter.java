package com.example.woochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

    private final List<User> friendList;

    public FriendAdapter(List<User> friendList) {
        this.friendList = friendList;
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

        User user = friendList.get(position);

        // TODO   Replace ImageView with User Icon
        imageView.setImageResource(R.drawable.friend_image);
        textView.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendHolder extends RecyclerView.ViewHolder {

        public ImageView friendIcon;
        public TextView friendName;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            friendIcon = itemView.findViewById(R.id.imageView_chat);
            friendName = itemView.findViewById(R.id.textView_chat_friendName);
        }
    }
}
