package com.example.woochat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.woochat.Chat;
import com.example.woochat.ChatAdapter;
import com.example.woochat.ChatroomActivity;
import com.example.woochat.Message;
import com.example.woochat.OnItemClickListener;
import com.example.woochat.R;
import com.example.woochat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    public static final String EXTRA_USER_ID = "com.example.woochat.fragments.USER_ID";
    public static final String EXTRA_FRIEND_ID = "com.example.woochat.fragments.FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "com.example.woochat.fragments.FRIEND_NAME";
    public static final String EXTRA_FRIEND_IMAGE = "com.example.woochat.fragments.FRIEND_IMAGE";

    RecyclerView chatView;

    DatabaseReference messageReference, userReference;
    FirebaseUser firebaseUser;

    String currentUserId;
    String currentUserEmail;
    List<Chat> chatList;
    List<Message> messagesList;
    String lastMessage;
    String friendId;


    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatList = new ArrayList<>();
        messagesList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        currentUserEmail = firebaseUser.getEmail();
    }

    private void getCurrentUserId() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    if (user.email.equals(currentUserEmail)) {
                        currentUserId = user.userId;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatView = view.findViewById(R.id.rv_chatList);

        messageReference = FirebaseDatabase.getInstance().getReference().child("messages");
        userReference = FirebaseDatabase.getInstance().getReference().child("user");

//
//        chatView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getCurrentUserId();

        messageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);

                    assert message != null;

                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                                // get a friend from database
                                User friend = chatSnapshot.getValue(User.class);
                                assert friend != null;

                                if (!friend.userId.equals(currentUserId)) {
                                    // assign attributes to variables
                                    String friendId = friend.userId;
                                    String friendImageUrl = friend.imageUrl;
                                    String friendName = friend.name;

                                    // instantiate Chat object
                                    Chat chat = new Chat(friendName, friendImageUrl, friendId, currentUserId, message.messageContent);

                                    if (message.receiverId.equals(friendId) || message.senderId.equals(friendId)) {
                                        if (!chatList.contains(chat)) {
                                            if (!chat.friendId.equals(currentUserId)) {
                                                chatList.add(chat);
                                            }
                                        } else {
                                            for (int i = 0; i < chatList.size(); i++) {
                                                if (chatList.get(i).equals(chat)) {
                                                    chatList.get(i).messageContent = chat.messageContent;
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            ChatAdapter chatAdapter = new ChatAdapter(chatList, new OnItemClickListener() {

                                @Override
                                public void setOnItemClickListener(User user) {

                                }

                                @Override
                                public void setOnChatItemClickListener(Chat chat) {
                                    Intent intent = new Intent(getContext(), ChatroomActivity.class);
                                    intent.putExtra(EXTRA_USER_ID, currentUserId);
                                    intent.putExtra(EXTRA_FRIEND_ID, chat.friendId);
                                    intent.putExtra(EXTRA_FRIEND_NAME, chat.friendName);
                                    intent.putExtra(EXTRA_FRIEND_IMAGE, chat.imageUrl);
                                    startActivity(intent);
                                }
                            });
                            chatView.setAdapter(chatAdapter);
                            chatView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}