package com.gic.memorableplaces.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.DataModels.AllUserSettings;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.grantland.widget.AutofitHelper;

public class LogOutFragment extends Fragment {
    private static final String TAG = "LogOutFragment";
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    ImageView BackArrowIamgeViewSignOut;
    Button signOutFinalButton;
    private TextView titleSignOut;

    private ProgressBar mProgressBar;
    private TextView tvSigningOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_out, container, false);
        BackArrowIamgeViewSignOut = view.findViewById(R.id.cancelButtonToolBar);
        signOutFinalButton = view.findViewById(R.id.signout_final);
        mProgressBar = (ProgressBar) view.findViewById(R.id.signout_fragment_progressBar);
        tvSigningOut = view.findViewById(R.id.tv_signout_fragment);
        titleSignOut = view.findViewById(R.id.SignOutTitle);

        mProgressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        AutofitHelper.create(titleSignOut);


        setupFirebaseAuth();

        BackArrowIamgeViewSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireFragmentManager().beginTransaction().remove(LogOutFragment.this).commit();

            }
        });
        signOutFinalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OnClickListener signOutButton: attempting to signOut");
                mProgressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);
                mFirebaseMethods.SetOnlineStatus(false);
                mAuth.signOut();
                requireActivity().finish();


            }
        });

        return view;


    }

    /**
     * responsible for settings all the values from to the widgets
     *
     * @param allUserSettings
     */
    private void setProfileWidgets(AllUserSettings allUserSettings) {

        //UserAccountSettings settings = allUserSettings.getUserAccountSettings();
        User user = allUserSettings.getUser();
        titleSignOut.setText(String.format("Signing Out Of %s", user.getUsername()));

    }
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "onAuthStateChanged:signed_out : Navigating back to logIn screen");

                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                // ...
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //RETRIEVE USER INFORMATION FROM DATABASE
                if (mAuth.getCurrentUser() != null)
                    setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot, mAuth.getCurrentUser().getUid()));
                //RETRIEVE USER PHOTOS FROM DATABASE
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
