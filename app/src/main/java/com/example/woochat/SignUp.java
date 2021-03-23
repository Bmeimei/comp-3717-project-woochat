package com.example.woochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private EditText emailText;
    private EditText passwordText;
    private EditText nameText;

    public Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        emailText = findViewById(R.id.signUpEmailInputBox);
        passwordText = findViewById(R.id.signUpPasswordInputBox);
        nameText = findViewById(R.id.nickNameInputBox);

        signUpButton = findViewById(R.id.signUpPageButton);

        signUpButton.setOnClickListener(v -> signUp());
    }

    public void backToLandingPage(View view) {
        Intent intent = new Intent(this, Landing.class);
        startActivity(intent);
    }

    private void signUp() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String name = nameText.getText().toString();
        if (!(checkIfStringEmpty(email, "Email") && checkIfStringEmpty(password, "Password") && checkIfStringEmpty(name, "Name"))) {
            firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this,
                                    "Successfully Sign Up!",
                                    Toast.LENGTH_LONG).show();
                            addUserToDatabase();
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this,
                                    "User already existed!",
                                    Toast.LENGTH_LONG).show();
                            emailText.setText("");
                            nameText.setText("");
                            passwordText.setText("");
                        }
                    });
        }
    }

    private void addUserToDatabase() {
        String id = databaseReference.push().getKey();
        String email = emailText.getText().toString();
        String name = nameText.getText().toString();

        User user = new User(id, email, name);
        assert id != null;
        Task<Void> task = databaseReference.child(id).setValue(user);

        try {
            task.addOnSuccessListener(e -> {
                Log.d("Sign Up success", "Successful Create User.");
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                intent.putExtra("id", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });

            task.addOnFailureListener(e -> Log.d("Failed", "Failed To Create User"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkIfStringEmpty(String item, String label) {
        if (TextUtils.isEmpty(item)) {
            Toast.makeText(this, label + " can not be empty", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}