package com.dev.hari.trackme.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.hari.trackme.R;
import com.dev.hari.trackme.modes.Post;
import com.dev.hari.trackme.modes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hariom.gupta on 5/17/2018.
 */

public class LoginActivity extends BaseActivity {
    private static String TAG = LoginActivity.class.getSimpleName();
    private static final java.lang.CharSequence REQUIRED = "Required!";
    private DatabaseReference mDatabase;
    private Activity activity;
    private EditText loginId;
    private Button submitButton;
//    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_login);
        loginId = findViewById(R.id.LoginId);
        loginId.setText("8510887828");
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String loginIdString = loginId.getText().toString();
                if (TextUtils.isEmpty(loginIdString)) {
                    loginId.setError(REQUIRED);
                    return;
                }
                authenticate(db.getString(PREF_EMAIL), db.getString(PREF_PASSWORD));
            }
        });
    }
    private void authenticate(String email, String password) {
        showProgressDialog("Authenticating...");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        hideProgressDialog();
                        Log.i(TAG, "authenticate: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            String path = "2018-05-19/";
                            mDatabase = FirebaseDatabase.getInstance().getReference(path);
                            fetchData();
                        } else {
                            Toast.makeText(activity, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchData() {
        try {
            showProgressDialog("Getting user data...");
            final String mobileNumber = "8510887828";
            mDatabase.child(mobileNumber).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            hideProgressDialog();
                            if(dataSnapshot != null){
                                String data = dataSnapshot.getChildren().toString();
                            }
                            User user = dataSnapshot.getValue(User.class);

                            if (user == null) {
                                Toast.makeText(activity, "Error: could not fetch user.", Toast.LENGTH_SHORT).show();
                            } else {
                                final String loginIdString = loginId.getText().toString();
                                writeNewPost(mobileNumber, user.userId, loginIdString);
                            }
                            //                                setEditingEnabled(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            //                                setEditingEnabled(true);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeNewPost(String userId, String userId1, String loginIdString) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, userId1, loginIdString);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
