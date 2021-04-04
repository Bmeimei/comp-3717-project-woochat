package com.example.woochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.woochat.fragments.ChatFragment;
import com.example.woochat.fragments.FriendsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ChatroomActivity extends AppCompatActivity {

    String userId;
    String friendId;
    String friendName;
    String imageUrl;
    String messageId;
    EditText editTextMessage;
    Button sendMessageButton;
    ImageView friendImage;

    RecyclerView rvMessages;
    ArrayList<Message> messageList;

    DatabaseReference dbMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Objects.requireNonNull(getSupportActionBar()).hide();

        rvMessages = findViewById(R.id.chatroom_rv_messages);
        messageList = new ArrayList<Message>();

        editTextMessage = findViewById(R.id.chatroom_editText_chat);

//        userId = getIntent().getStringExtra(FriendsFragment.EXTRA_USER_ID);
//        friendId = getIntent().getStringExtra(FriendsFragment.EXTRA_FRIEND_ID);
//        imageUrl = getIntent().getStringExtra(FriendsFragment.EXTRA_FRIEND_IMAGE);
//        friendName = getIntent().getStringExtra(FriendsFragment.EXTRA_FRIEND_NAME);

//        if (TextUtils.isEmpty(userId)) {
            userId = getIntent().getStringExtra(ChatFragment.EXTRA_USER_ID);
            friendId = getIntent().getStringExtra(ChatFragment.EXTRA_FRIEND_ID);
            imageUrl = getIntent().getStringExtra(ChatFragment.EXTRA_FRIEND_IMAGE);
            friendName = getIntent().getStringExtra(ChatFragment.EXTRA_FRIEND_NAME);
//        }
        TextView tvFriendName = findViewById(R.id.textView_chatroom_friend_id);
        tvFriendName.setText(friendName);

        friendImage = findViewById(R.id.imageView_chatroom);




        Glide
                .with(this)
                .load(imageUrl)
                .thumbnail(Glide.with(this).load(R.drawable.loading))
                .into(friendImage);

        sendMessageButton = findViewById(R.id.chatroom_button_send);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        dbMessages = FirebaseDatabase.getInstance().getReference("messages");

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot: snapshot.getChildren()) {
                    Message newMessage = messageSnapshot.getValue(Message.class);
                    assert newMessage != null;
                    if (newMessage.senderId.equals(userId) || newMessage.senderId.equals(friendId)) {
                        if (newMessage.receiverId.equals(userId) || newMessage.receiverId.equals(friendId)) {
                            messageList.add(newMessage);
                        }
                    }
                }

                MessageAdapter adapter = new MessageAdapter(messageList, userId);
                rvMessages.setAdapter(adapter);
                rvMessages.setLayoutManager(new LinearLayoutManager(ChatroomActivity.this));
                if (messageList.size() > 0) {
                    rvMessages.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "EMPTY MESSAGE", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = dbMessages.push().getKey();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-7"));
        String formattedDate = formatter.format(date);
        Message newMessage = new Message(id, userId, friendId, content, formattedDate);

        Task<Void> setValueTask = dbMessages.child(id).setValue(newMessage);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(ChatroomActivity.this, "Succeed", Toast.LENGTH_SHORT).show();
                editTextMessage.setText("");
            }
        });

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatroomActivity.this, "Somthing went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}