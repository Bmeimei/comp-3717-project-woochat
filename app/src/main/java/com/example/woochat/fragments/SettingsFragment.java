package com.example.woochat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.woochat.Landing;
import com.example.woochat.MainActivity;
import com.example.woochat.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    Button logoutButton;

    public SettingsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    public void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), Landing.class);
        Toast.makeText(getContext(), "Successfully Log Out!", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
}