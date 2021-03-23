package com.example.woochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Landing extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    Button loginButton;
    Button signUpButton;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_landing);

        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> goToLoginPage());
        signUpButton.setOnClickListener(v -> goToSignUpPage());

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            email = currentUser.getEmail();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (user.email.equals(email)) {
                            Toast.makeText(Landing.this,
                                    "Successfully Login!",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Landing.this, MainActivity.class);
                            intent.putExtra("id", user.userId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void goToLoginPage() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void goToSignUpPage() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}