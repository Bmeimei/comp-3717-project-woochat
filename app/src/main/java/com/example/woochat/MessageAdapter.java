package com.example.woochat;

import android.print.PageRange;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final ArrayList<Message> messages;
    private final String userID;

    public MessageAdapter(ArrayList<Message> messages, String userID) {
        this.messages = messages;
        this.userID = userID;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View messagesView = inflater.inflate(R.layout.recycler_chat_receiver, parent, false);

        MessageViewHolder viewHolder = new MessageViewHolder(messagesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.END;
        params.weight = 1.0f;
        params.setMargins(0, 0, 10, 15);
        TextView tvTime = holder.tvTime;
        TextView tvContent = holder.tvContent;

        Message message = messages.get(position);
        tvTime.setText(message.time);
        tvContent.setText(message.messageContent);
        if (message.senderId.equals(userID)) {
            tvTime.setLayoutParams(params);
            tvContent.setLayoutParams(params);
            tvContent.setBackground(ContextCompat.getDrawable(tvContent.getContext() , R.drawable.rounded_corner));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTime;
        public TextView tvContent;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.recycler_message_time);
            tvContent = itemView.findViewById(R.id.recycler_message_content);
        }
    }
}
