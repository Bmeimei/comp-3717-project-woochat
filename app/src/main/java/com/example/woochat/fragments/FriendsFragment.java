package com.example.woochat.fragments;

import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woochat.ChatroomActivity;
import com.example.woochat.FriendAdapter;
import com.example.woochat.OnItemClickListener;
import com.example.woochat.R;
import com.example.woochat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    public static final String EXTRA_USER_ID = "com.example.woochat.fragments.USER_ID";
    public static final String EXTRA_FRIEND_ID = "com.example.woochat.fragments.FRIEND_ID";
    public static final String EXTRA_FRIEND_NAME = "com.example.woochat.fragments.FRIEND_NAME";

    RecyclerView friendView;
    DatabaseReference databaseReference;
    List<User> friendList;
    TextView tvFriends;
    String user_email;
    String user_name;
    String user_id;
    String imageUrl;
    TextView tvCurrentUser;
    FirebaseUser firebaseUser;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment FriendsFragment.
     */
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        friendList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            user_email = firebaseUser.getEmail();
        }
        Bundle bundle = this.getArguments();
        assert bundle != null;
        user_id = bundle.getString("id");
        databaseReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    assert currentUser != null;
                    user_name = currentUser.name;
                    tvCurrentUser.setText(user_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    User user = friendSnapshot.getValue(User.class);
                    assert user != null;

                    if (!user.email.equals(user_email)) {
                        friendList.add(user);
                    }

                }

                String friendsHeader = "Friends (" + friendList.size() + ")";
                tvFriends.setText(friendsHeader);
                FriendAdapter friendAdapter = new FriendAdapter(friendList, new OnItemClickListener() {

                    /**
                     * TODO: Implement the chat function with friend here.
                     */
                    @Override
                    public void setOnItemClickListener(User friend) {
                        Intent intent = new Intent(getContext(), ChatroomActivity.class);
                        intent.putExtra(EXTRA_USER_ID, user_id);
                        intent.putExtra(EXTRA_FRIEND_ID, friend.userId);
                        intent.putExtra(EXTRA_FRIEND_NAME, friend.name);
                        startActivity(intent);
                    }
                });
                friendView.setAdapter(friendAdapter);
                friendView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendView = view.findViewById(R.id.recycler_friends);
        tvFriends = view.findViewById(R.id.textView_friends);
        tvCurrentUser = view.findViewById(R.id.tv_friends_username);
        tvCurrentUser.setText(user_name);

        // Inflate the layout for this fragment
        return view;
    }
}