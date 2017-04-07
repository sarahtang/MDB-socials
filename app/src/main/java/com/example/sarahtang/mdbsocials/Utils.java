package com.example.sarahtang.mdbsocials;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sarahtang on 2/28/17.
 * Static class to hold general methods for code.
 */

public class Utils extends AppCompatActivity{

    public static final String NAME_KEY = "name";
    public static final String HOST_KEY = "host";
    public static final String DESCRIPTION_KEY = "description";
    public static final String PEOPLE_INTERESTED = "peopleInterested";
    public static final String NUMBER_INTERESTED = "numberInterested";
    public static final String FIREBASE_URL = "firebaseURL";
    public static final String FIREBASE_KEY = "firebaseKey";

    /**
     * Attempt to sign up/create user in Firebase
     */


    public static void attemptSignUp (AutoCompleteTextView mEmailView, EditText mPasswordView, FirebaseAuth mAuth, final LoginActivity loginActivity) {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("status", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("status", "signInWithEmail", task.getException());
                                Toast.makeText(loginActivity, R.string.toastFailSignUp,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.d("status", "signInSuccessful");
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                                final String key = currUser.getUid();
                                ref.child("users").child(key).child("email").setValue(currUser.getEmail());
                            }
                        }
                    });
        }
        else {
            Toast.makeText(loginActivity, R.string.missingInfo, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Attempt to log in user through Firebase
     */

    public static void attemptLogin(EditText emailU, EditText passwordU, FirebaseAuth mAuth, final LoginActivity loginActivity) {
        final String email = emailU.getText().toString();
        String password = passwordU.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("status", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("status", "signInWithEmail:failed", task.getException());
                                Toast.makeText(loginActivity, R.string.failLogIn,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(loginActivity, R.string.missingInfo, Toast.LENGTH_SHORT).show();
        }
    }

}
