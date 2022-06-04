package com.gic.memorableplaces.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.UserDetailsDatabase;
import com.gic.memorableplaces.SignUp.SignUpActivity;
import com.gic.memorableplaces.interfaces.UserDetailsDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class NavigatingCardActivity extends AppCompatActivity {
    private static final String TAG = "NavigatingCardActivity";
    private Context mContext;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isVerified;

    private User user;
    private UserDetailsDatabase UD_DETAILS;
    private UserDetailsDao userDetailsDao;
    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = NavigatingCardActivity.this;
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user = new User();
        UD_DETAILS = UserDetailsDatabase.getDatabase(mContext);
        databaseWriteExecutor = UD_DETAILS.databaseWriteExecutor;
        userDetailsDao = UD_DETAILS.userDetailsDao();
        user.SetDefault();

        setupFirebaseAuth();
        Handler handler = new Handler(Looper.getMainLooper());
        databaseWriteExecutor.execute(() -> {
            if (userDetailsDao.GetAllDetails(mAuth.getCurrentUser().getUid()).size() != 0) {
                user = userDetailsDao.GetAllDetails(mAuth.getCurrentUser().getUid()).get(0);
                handler.post(this::DecideNavigation);
            } else {

                Query query = myRef.child(mContext.getString(R.string.dbname_users))
                        .child(mAuth.getCurrentUser().getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            ArrayList<String> ImagesPaths = new ArrayList<>(5);
                            user.setUser_id(String.valueOf(snapshot.child("user_id").getValue()));
                            user.setUsername(String.valueOf(snapshot.child("username").getValue()));
                            user.setAuto_desp(String.valueOf(snapshot.child("auto_desp").getValue()));
                            user.setDisplay_name(String.valueOf(snapshot.child("display_name").getValue()));
                            user.setCourse(String.valueOf(snapshot.child("course").getValue()));
                            user.setLocation(String.valueOf(snapshot.child("location").getValue()));
                            for (DataSnapshot dataSnapshot : snapshot.child("photos_list").getChildren()) {
                                ImagesPaths.add(String.valueOf(dataSnapshot.getValue()));
                            }
                            user.setPhotos_list(ImagesPaths);
                            if (snapshot.hasChild("isAutoDespEnabled"))
                                user.setAutoDespEnabled((Boolean) snapshot.child("isAutoDespEnabled").getValue());
                            user.setProfile_photo(String.valueOf(snapshot.child("profile_photo").getValue()));
                            user.setEmail(String.valueOf(snapshot.child("email").getValue()));
                            user.setGender(String.valueOf(snapshot.child("gender").getValue()));
                            if (snapshot.hasChild("phone_number"))
                                user.setPhone_number((Long) snapshot.child("phone_number").getValue());
                            if (snapshot.hasChild("email_verified"))
                                user.setEmail_verified((Boolean) snapshot.child("email_verified").getValue());
                            if (snapshot.hasChild("sign_up_complete"))
                                user.setSign_up_complete((Boolean) snapshot.child("sign_up_complete").getValue());

                            Log.d(TAG, "onDataChange: user 1: " + snapshot.getValue(User.class));
                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: user: " + user);
                        }

                        databaseWriteExecutor.execute(() -> {
                            userDetailsDao.InsertNewDetail(user);
                            DecideNavigation();
                        });
//                        Intent intent = new Intent(NavigatingCardActivity.this, SignUpActivity.class);
//                        intent.putExtra("status", "not_done");
//                        if (user != null) {
//                            intent.putExtra(mContext.getString(R.string.field_username), user.getUsername());
//                            intent.putExtra(mContext.getString(R.string.field_course), user.getCourse());
//                            intent.putExtra(mContext.getString(R.string.field_description), user.getAuto_desp());
//                            intent.putExtra(mContext.getString(R.string.field_display_name), user.getDisplay_name());
//                        }
//                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //DecideNavigation();


    }

    private void DecideNavigation() {
        if (!user.isEmail_verified()) {
            Intent intent = new Intent(NavigatingCardActivity.this, LogInActivity.class);
            Toast.makeText(mContext, "Your email is not verified. Please, click the link that you have received via email from Hansraj Social Club to verify your email.", Toast.LENGTH_SHORT).show();
            startActivity(intent);

            Log.d(TAG, "onSuccess: Route Navigating to login: ");
        } else {
            if (!user.isSign_up_complete()) {
                Log.d(TAG, "onDataChange: navigating to sign up");
                Intent intent = new Intent(NavigatingCardActivity.this, SignUpActivity.class);
                intent.putExtra("status", "not_done");
                startActivity(intent);
            } else {
                Log.d(TAG, "onDataChange: 3");
                Intent intent = new Intent(NavigatingCardActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
        finish();

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
