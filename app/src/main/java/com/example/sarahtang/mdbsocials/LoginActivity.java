package com.example.sarahtang.mdbsocials;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Login Activity: Activity for initial screen that calls users to
 * login or sign up accordingly.
 */

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText email;
    private EditText password;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button signInButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Doing this and additional (super), overriding = redefine in subclass
         */
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        mPasswordView = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(this);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        /**
         * Initialize FirebaseAuth instance and AuthStateListener method to
         * track when user signs in and signs out
         */

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(intent);
                    Log.d("status", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("status", "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    public void onClick(View view) {
        if (view.getId() == R.id.email_sign_in_button) {
            Utils.attemptLogin(email, password, mAuth, LoginActivity.this);
        }
        if (view.getId() == R.id.register_button) {
            Utils.attemptSignUp(mEmailView, mPasswordView, mAuth, LoginActivity.this);
        }
    }

    /**
     * Allows for automatic go to list activity if already signed in
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
