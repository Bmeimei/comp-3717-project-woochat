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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailText;
    private EditText passwordText;

    public Button loginButton;
    public Button signUpButton;

    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        signUpButton = findViewById(R.id.loginPageSignUpButton);
        signUpButton.setOnClickListener(v -> goToSignUpPage());

        loginButton = findViewById(R.id.loginPageLoginButton);
        loginButton.setOnClickListener(v -> login());

        emailText = findViewById(R.id.emailInputBox);
        passwordText = findViewById(R.id.passwordInputBox);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void backToLandingPage(View view) {
        Intent intent = new Intent(this, Landing.class);
        startActivity(intent);
    }

    private void login() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (!(checkIfStringEmpty(email, "Email") && checkIfStringEmpty(password, "Password"))) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this,
                                    "Successfully Login!",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this,
                                    "Invalid Email Or Password!",
                                    Toast.LENGTH_LONG).show();
                            passwordText.setText("");
                        }
                    });
        }
    }

    public void goToSignUpPage() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    private boolean checkIfStringEmpty(String item, String label) {
        if (TextUtils.isEmpty(item)) {
            Toast.makeText(this, label + " can not be empty", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}