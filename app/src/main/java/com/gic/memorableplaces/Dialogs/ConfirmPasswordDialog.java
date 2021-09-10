package com.gic.memorableplaces.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gic.memorableplaces.R;

import java.util.Objects;

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public interface OnConfirmingPasswordListener{
        public void OnConfirmPassword(String password);
    }
    OnConfirmingPasswordListener onConfirmingPasswordListener;

    EditText mPassword;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, true);
        Log.d(TAG, "onCreateView: inflating dialog confirm password");
        setupFirebaseAuth();

        TextView confirmDialog = (TextView) view.findViewById(R.id.positive);
        TextView cancelDialog = (TextView) view.findViewById(R.id.negative);
        mPassword = (EditText) view.findViewById(R.id.dialogPasswordEditText) ;
        
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: confirmDialog : confirming password by submitting to firebase");
                String password = mPassword.getText().toString();
                if(!password.equals("")){
                    onConfirmingPasswordListener.OnConfirmPassword(password);
                    checkpassword(password);
                }
                else{
                    mPassword.setError("You must enter your password.");
                }

            }
        });
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: cancelDialog");
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
           onConfirmingPasswordListener = (OnConfirmingPasswordListener) getTargetFragment();

        }catch(ClassCastException e){

            Log.e(TAG, "onAttach: ClassCastException"+ e.getMessage() );
        }
    }
    public void checkpassword(String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()), password);
        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(getDialog()).dismiss();
                } else {
                    mPassword.setError("Wrong Password!");

                }
            }
        });
    }
     /*
    ------------------------------------ Firebase ---------------------------------------------
     */


    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


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
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
