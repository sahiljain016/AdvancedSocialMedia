package com.gic.memorableplaces.SignUp;

import static com.gic.memorableplaces.LogIn.LogInActivity.drawableToBitmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.CustomLibs.EndToEnd.EndToEndEncrypt;
import com.gic.memorableplaces.CustomLibs.TransitionButton.TransitionButton;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.Home.GetStartedFragment;
import com.gic.memorableplaces.LogIn.LogInActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.UserDetailsDatabase;
import com.gic.memorableplaces.interfaces.UserDetailsDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.virgilsecurity.android.ethree.interaction.EThree;
import com.virgilsecurity.common.callback.OnResultListener;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class SignUpFragment extends Fragment {
    private static final String TAG = "SignUpFragment";

    private Context mContext;
    private EditText /*mFullName,*/ mEmail, mPassword, mConfirmPassword, mUsername/*, mPhoneNumber, mUID*/;
    private String FullName, Email, Password, ConfirmPassword, Username, sToken;
    private boolean isPaused = true;
    //String check = "";
    Boolean checkIfEmpty;
    //private long PhoneNumber, UID;
    private TextView loadingSignUpTV, LoginLinkOnSignUp;
    //    private ProgressBar mProgressBar;
    private TransitionButton btn_signup;
    public static Bitmap ButtonDoneIcon = null, ButtonFailIcon = null;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;

    private User user;
    private UserDetailsDatabase UD_DETAILS;
    private UserDetailsDao userDetailsDao;
    private ExecutorService databaseWriteExecutor;

    private String append = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Log.d(TAG, "onCreateView: ChatFragment");

        mContext = getActivity();
        //INITIALISING ALL WIDGETS
        user = new User();
        user.SetDefault();
        UD_DETAILS = UserDetailsDatabase.getDatabase(mContext);
        databaseWriteExecutor = UD_DETAILS.databaseWriteExecutor;
        userDetailsDao = UD_DETAILS.userDetailsDao();
        //mProgressBar = findViewById(R.id.SignUpButtonProgressBar);
        mEmail = view.findViewById(R.id.SignUpEmail);
        mPassword = view.findViewById(R.id.SignUpPassword);
        mConfirmPassword = view.findViewById(R.id.SignUpCPassword);
        loadingSignUpTV = view.findViewById(R.id.LoadingSignUpText);
        btn_signup = view.findViewById(R.id.btn_signUp);
        //mFullName = findViewById(R.id.SignUpFullName);
        //   mUID = findViewById(R.id.SignUpUID);
        // mPhoneNumber = findViewById(R.id.SignUpPhoneNumber);
        mUsername = view.findViewById(R.id.SignUpUsername);

        LoginLinkOnSignUp = view.findViewById(R.id.linkLogInOnSignUp);
//        mProgressBar.setVisibility(View.GONE);
        loadingSignUpTV.setVisibility(View.GONE);

        if (getArguments() != null) {
            if (getArguments().getString("status").equals("not_done")) {
                GetStartedFragment fragment = new GetStartedFragment();
                FragmentTransaction Transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCard, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.get_started_fragment));
                Transaction.commit();
            }
        }
        // FIREBASE
        setupFirebaseAuth();
        firebaseMethods = new FirebaseMethods(mContext);
        Drawable drawableDone = ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_correct);
        Drawable drawableFail = ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_fail_red_cross);
        ButtonDoneIcon = drawableToBitmap(drawableDone);
        ButtonFailIcon = drawableToBitmap(drawableFail);
        //Initialising Authentication
        init();

        PasswordFocusGained();
        ConfirmPasswordFocusGained();
        /*ChangeEmailETColour();
        ChangeFullNameColour();
        ChangeUserNameColour();*/


        return view;
    }

    /*
    IF PASSWORD GAINS FOCUS AFTER EMPTY STRING
     */
    private void PasswordFocusGained() {
        mPassword.setOnFocusChangeListener((view, b) -> {
            if (mPassword.hasFocus()) {
                // mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mPassword.setText("");
                //mPassword.setTextColor(Color.BLACK);
            }
        });

    }

    /*
   IF Confirm PASSWORD GAINS FOCUS AFTER EMPTY STRING
    */
    private void ConfirmPasswordFocusGained() {
        mConfirmPassword.setOnFocusChangeListener((view, b) -> {
            if (mConfirmPassword.hasFocus()) {
                mConfirmPassword.setText("");
            }
        });


    }


    private void RegisterUserWithVirgil(String BackupPassword, String finalMUsername, EndToEndEncrypt E2E) {
        com.virgilsecurity.common.callback.OnCompleteListener onBackupListener = new com.virgilsecurity.common.callback.OnCompleteListener() {
            @Override
            public void onSuccess() {

                Log.d(TAG, "onSuccess: Route Get Started fragment");
                Log.d(TAG, "onClick: username firebase methods " + Username);
//                GetStartedFragment fragment = new GetStartedFragment();
//                FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.FrameLayoutCard, fragment);
//                Transaction.addToBackStack(mContext.getString(R.string.get_started_fragment));
//                Transaction.commit();


                firebaseMethods.AddNewUser(FullName, 0, 9, Email, finalMUsername, "", "",
                        "", "", "");
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d(TAG, "onError: backup error: " + throwable.getMessage());
                //Inflate Error Dialog
            }
        };


        final com.virgilsecurity.common.callback.OnCompleteListener onRegisterListener =
                new com.virgilsecurity.common.callback.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "User one backup");
                        E2E.backupPrivateKey(BackupPassword).addCallback(onBackupListener);


                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        Log.e(TAG, "User one backup failed", throwable);

                    }
                };

        E2E.RegisterAndSaveKeys().addCallback(onRegisterListener);
    }


    private void init() {
        btn_signup.setOnClickListener(view -> {
            Email = mEmail.getText().toString();
            Username = mUsername.getText().toString();
            //FullName = mFullName.getText().toString();
            Password = mPassword.getText().toString();
            ConfirmPassword = mConfirmPassword.getText().toString();

            if (checkInputStrings(Email, Username, FullName, Password, ConfirmPassword)) {
// <------------------------------------------------------ passsword check if equal start ------------------------------>
                if (Password.equals(ConfirmPassword) /*&& CheckIfPhoneNoExists(PhoneNumber).equals("true")*/) {
                    btn_signup.startAnimation();
                    loadingSignUpTV.setVisibility(View.VISIBLE);
                    btn_signup.setText("");

                    firebaseMethods.registerNewUser(Email, Password, Username, getActivity(), btn_signup, new OnResultListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, "onSuccess: Route OnResultListener");
//                            user.setEmail(Email);
//                            user.setUsername(Username);
//                            user.setUser_id(mAuth.getCurrentUser().getUid());
//                            user.setEmail_verified(true);

                            myRef.child(mContext.getString(R.string.dbname_users))
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_email_verified))
                                    .setValue(true);


                            databaseWriteExecutor.execute(() -> {
                                userDetailsDao.UpdateEmailVerifiedBool(true, mAuth.getCurrentUser().getUid());
                                GetStartedFragment fragment = new GetStartedFragment();
                                FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                Transaction.replace(R.id.FrameLayoutCard, fragment);
                                Transaction.addToBackStack(mContext.getString(R.string.get_started_fragment));
                                Transaction.commit();
//                                    EndToEndEncrypt E2EUser = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), mContext);
//                                    RegisterUserWithVirgil(EThree.derivePasswords(Password).getBackupPassword(), E2EUser);
                            });
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {

                        }
                    });
                }
                // <------------------------------------------------------ password check else start ------------------------------>
               /* else if (CheckIfPhoneNoExists(PhoneNumber).equals("false")  ){
                    mProgressBar.setVisibility(View.GONE);
                    loadingSignUpTV.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(mContext, "Phone number is already linked!",
                            Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    v.setTextSize(18);
                    toast.show();
                }*/
                else {
                    btn_signup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    // mProgressBar.setVisibility(View.GONE);
                    loadingSignUpTV.setVisibility(View.GONE);
                    Toast.makeText(mContext, "Check Your Passwords again! They don't match.",
                            Toast.LENGTH_LONG).show();
                    final Handler handler = new Handler(Looper.getMainLooper());
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                btn_signup.setBackgroundResource(R.drawable.custom_button_login);
//                                btn_signup.revertAnimation();
//                            }
//                        }, 2000);

                }
// <------------------------------------------------------ password check else end ------------------------------>
            }
// <------------------------------------------------------ this else is the else of main if ------------------------------>
            else {
                btn_signup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                //mProgressBar.setVisibility(View.GONE);
                loadingSignUpTV.setVisibility(View.GONE);
                Toast.makeText(mContext, "Sign Up failed! Please check your credentials",
                        Toast.LENGTH_LONG).show();

//                    final Handler handler = new Handler(Looper.getMainLooper());
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_signup.setBackgroundResource(R.drawable.custom_button_login);
//                            btn_signup.revertAnimation();
//                        }
//                    }, 2000);
            }
        });

        /*
  NAVIGATION TO LOGIN FROM SIGN UP
 */
        LoginLinkOnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);
                getActivity().finish();
//                PrepareCardFragment fragment = new PrepareCardFragment();
                //fragment.setArguments(bundle);
//                FragmentTransaction Transaction = requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.FrameLayoutCard, fragment);
//                Transaction.addToBackStack(mContext.getString(R.string.prepare_card_fragment));
//                Transaction.commit();
            }
        });

    }

    /*
   CHECKING IF ALL DETAILS ARE FILLED
    */
    private boolean checkInputStrings(String Email, String Username, String FullName, String Password, String ConfirmPassword) {
        if (Email.equals("")) {
            /*mEmail.setText("Email Required");
            mEmail.setTextColor(Color.RED);*/
            mEmail.setError("Email Required");

            checkIfEmpty = false;
        } else {
            checkIfEmpty = true;
        }
        if (Username.equals("")) {
            /*mUsername.setText("Username Required");
            mUsername.setTextColor(Color.RED);*/
            mUsername.setError("Username Required");
            checkIfEmpty = false;

        } else {
            checkIfEmpty = true;
        }
//        if (FullName.equals("")) {
//             /* mFullName.setText("Full Name Required");
//              mFullName.setTextColor(Color.RED);*/
//            mFullName.setError("Full Name Required");
//            checkIfEmpty = false;
//
//        }
//        else {
//            checkIfEmpty = true;
//        }
        if (Password.equals("")) {
            /* *//* mPassword.setText("Password Required");
             mPassword.setTextColor(Color.RED);*//*
             mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
            mPassword.setError("Password Required");
            checkIfEmpty = false;
        } else {
            checkIfEmpty = true;
        }
        if (ConfirmPassword.equals("")) {
             /*mConfirmPassword.setText("Please Confirm Your Password");
             mConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
             mConfirmPassword.setTextColor(Color.RED);*/
            mConfirmPassword.setError("Please Confirm Your Password");
            checkIfEmpty = false;
        } else {
            checkIfEmpty = true;
        }

//        if(Password.length() < 7 ){
//            mPassword.setError("Password should be greater than 7 characters");
//        }
        return checkIfEmpty;

    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks if user already exists
     *
     * @param username
     */
    private void CheckIfUsernameExists(final String username) {
        Log.d(TAG, "CheckIfUsernameExists: " + username);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot SingleSnapshot : snapshot.getChildren()) {
                    if (SingleSnapshot.exists()) {
                        Log.d(TAG, "CheckIfUsernameExists: Found A Match " + SingleSnapshot.getValue(User.class).getUsername());
                        append = Objects.requireNonNull(myRef.push().getKey()).substring(6, 10);
                        Log.d(TAG, "Username already exists, appending random string to name" + append);
                    }
                }

                String mUsername = "";
                mUsername = username + append;
                String finalMUsername = mUsername;
                databaseWriteExecutor.execute(() -> {
                    user.setUsername(finalMUsername);
                    user.setUser_id(mAuth.getCurrentUser().getUid());
                    user.setEmail(Email);
                    userDetailsDao.InsertNewDetail(user);

                    EndToEndEncrypt E2EUser = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), mContext);
                    RegisterUserWithVirgil(EThree.derivePasswords(Password).getBackupPassword(), finalMUsername, E2EUser);
//                    firebaseMethods.AddNewUser(FullName, 0, 9, Email, finalMUsername, "", "",
//                            "", "", "");
                });
                Log.d(TAG, "onSuccess: Route CheckIfUsernameExists");
                //  mAuth.signOut();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            //check if the user is logged in


            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CheckIfUsernameExists(Username);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // finish();

            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
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
