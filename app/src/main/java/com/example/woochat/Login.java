package com.example.woochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText emailText;
    private EditText passwordText;

    public Button loginButton;
    public Button signUpButton;
    public Button googleLoginButton;

    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

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

        googleLoginButton = findViewById(R.id.loginPageGoogleLoginButton);
        googleLoginButton.setOnClickListener(v -> loginByGoogleAccount());

        emailText = findViewById(R.id.emailInputBox);
        passwordText = findViewById(R.id.passwordInputBox);
        firebaseAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
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
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        assert user != null;
                                        if (user.email.equals(email)) {
                                            Toast.makeText(Login.this,
                                                    "Successfully Login!",
                                                    Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            intent.putExtra("id", user.userId);
                                            startActivity(intent);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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

    private void loginByGoogleAccount() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            if (result.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                assert googleSignInAccount != null;
                String name = googleSignInAccount.getDisplayName();
                String email = googleSignInAccount.getEmail();
                System.out.println("Email:" + email);
                String id = googleSignInAccount.getId();
                User user = new User(id, email, name);
                assert id != null;
                Task<Void> task = databaseReference.child(id).setValue(user);

                try {
                    task.addOnSuccessListener(e -> {
                        firebaseAuthWithGoogle(googleSignInAccount);
                        Log.d("Sign Up success", "Successful Create User.");
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("id", id);
                    });

                    task.addOnFailureListener(e -> Log.d("Failed", "Failed To Create User"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(Login.this,
                                "Failed to Login!",
                                Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(Login.this,
                                "Successfully Login!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        Login.this.finish();
                    }
                });
    }
}