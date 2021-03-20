package com.example.woochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.woochat.fragments.ChatFragment;
import com.example.woochat.fragments.FriendsFragment;
import com.example.woochat.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        tvCurrentUser.setText("hello");
//        if (user != null) {
//            String name = user.getDisplayName();
//        }


        Fragment friendsFragment = new FriendsFragment();
        Fragment chatFragment = new ChatFragment();
        Fragment settingsFragment = new SettingsFragment();

        makeCurrentFragment(friendsFragment);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.ic_friends:
                        makeCurrentFragment(friendsFragment);
                        break;
                    case R.id.ic_chat:
                        makeCurrentFragment(chatFragment);
                        break;
                    case R.id.ic_settings:
                        makeCurrentFragment(settingsFragment);
                        break;
                }
                return true;
            }
        });
    }

    private void makeCurrentFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_wrapper, fragment);
        transaction.commit();
    }

}