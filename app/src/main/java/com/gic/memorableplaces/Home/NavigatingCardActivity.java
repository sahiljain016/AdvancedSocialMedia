package com.gic.memorableplaces.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.SignUp.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NavigatingCardActivity extends AppCompatActivity {
    private static final String TAG = "NavigatingActivity";
    private Context mContext;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isVerified;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = NavigatingCardActivity.this;
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();


        setupFirebaseAuth();
        //DecideNavigation();
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountSettings userAccountSettings = snapshot.getValue(UserAccountSettings.class);
                Intent intent = new Intent(NavigatingCardActivity.this, SignUpActivity.class);
                intent.putExtra("status", "not_done");
                intent.putExtra(mContext.getString(R.string.field_username), userAccountSettings.getUsername());
                intent.putExtra(mContext.getString(R.string.field_course), userAccountSettings.getCourse());
                intent.putExtra(mContext.getString(R.string.field_description), userAccountSettings.getCard_bio());
                intent.putExtra(mContext.getString(R.string.field_display_name), userAccountSettings.getDisplay_name());
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void DecideNavigation(){
        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (!user.getIs_email_verified().equals("true")) {
                    Log.d(TAG, "onDataChange: 1");
                    Intent intent = new Intent(NavigatingCardActivity.this, LogInActivity.class);
                    Toast.makeText(mContext, "Your email is not verified. Please, click the link that you have received via email from Hansraj Social Club to verify your email.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    Log.d(TAG, "onDataChange: false email verify ");
                    if (!user.getPage_number().equals(mContext.getString(R.string.card_completed))) {
                        Log.d(TAG, "onDataChange: navigating to sign up");
                        Intent intent = new Intent(NavigatingCardActivity.this, SignUpActivity.class);
                        intent.putExtra("status", "not_done");
                        intent.putExtra(mContext.getString(R.string.field_username), user.getUsername());
                        startActivity(intent);
                    }
                    else{
                        Log.d(TAG, "onDataChange: 3");
                        Intent intent = new Intent(NavigatingCardActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null) {
            Intent intent = new Intent(mContext, LogInActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
