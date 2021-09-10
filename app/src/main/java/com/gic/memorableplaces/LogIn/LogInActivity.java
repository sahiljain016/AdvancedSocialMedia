package com.gic.memorableplaces.LogIn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gic.memorableplaces.CustomLibs.EndToEnd.EndToEndEncrypt;
import com.gic.memorableplaces.CustomLibs.TransitionButton.TransitionButton;
import com.gic.memorableplaces.Home.NavigatingCardActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.SignUp.SignUpActivity;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.makeramen.roundedimageview.RoundedImageView;
import com.virgilsecurity.android.ethree.interaction.EThree;

import java.util.Map;
import java.util.Objects;

import kotlin.jvm.functions.Function0;


public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private TextView loadingLoginTV;
    private EditText mEmail, mPassword;
    private Bitmap ButtonDoneIcon, ButtonFailIcon;
    private RoundedImageView RIV;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);
        mContext = LogInActivity.this;
        myRef = FirebaseDatabase.getInstance().getReference();

//        RIV = findViewById(R.id.IV_USER_IMAGES_FF);
//
//
//                GlideImageLoader.loadImageWithOutTransition(mContext,"https://firebasestorage.googleapis.com/v0/b/st-xavier-s-social-club-sahil.appspot.com/o/photos%2Fusers%2F0arilxGjhbhfGm4R9O7dvVUJ0WO2%2Fprofile_photo?alt=media&token=497bec36-4b0f-422e-83b3-9ba2397b2967".toString(),RIV);
//
//        int con = 1;
//        if (con == 2) {
        mEmail = findViewById(R.id.LogInEmail);
        mPassword = findViewById(R.id.LogInPassword);
        loadingLoginTV = findViewById(R.id.LoadingLoginText);


        Log.d(TAG, "ON CREATE : STARTED");

        loadingLoginTV.setVisibility(View.GONE);
        Drawable drawableDone = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_correct);
        Drawable drawableFail = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fail_red_cross);
        ButtonDoneIcon = drawableToBitmap(drawableDone);
        ButtonFailIcon = drawableToBitmap(drawableFail);
        //FIREBASE AUTHENTICATION
        setupFirebaseAuth();
        init();
        //}
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "CHECKING IF STRING IS NULL");

        if (string.equals("")) {
            return true;
        } else {

            return false;
        }

    }

    private boolean isPaused = true;
    private final Function0<String> getAuthTokenUserOne = () -> {

        Log.d(TAG, "getAutHtokenUserOne: ");

        final String[] token = {""};
        FirebaseFunctions.getInstance()
                .getHttpsCallable("getVirgilJwt")
                .call()
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {

                        if (task.isSuccessful()) {

                            token[0] = ((Map<String, String>) task.getResult().getData()).get("token");
                            // Log.d(TAG, "onComplete: task result: " + token[0]);
                            isPaused = false;
                        } else {
                            Log.d(TAG, String.format("onComplete: task error: %s", task.getException()));
                        }
                    }
                });

        while (true) {
            if (!isPaused) {
                Log.d(TAG, "invoke: token[0]: " + token[0]);
                return token[0];
            }
        }

    };
    
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void init() {

        final TransitionButton LoginButton = findViewById(R.id.btn_login);
        LoginButton.setOnClickListener(view -> {
            Log.d(TAG, "ON CLICK WORKS");

            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            LoginButton.setText("");
            if (isStringNull(email)) {
              /*Toast toast = Toast.makeText(mContext,"Add email & password to login!",Toast.LENGTH_LONG);
              TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
              v.setTextColor(Color.RED);
              toast.show();*/
                mEmail.setError("Enter your email to log in.");
                LoginButton.setText("Log In");
            }
            if (isStringNull(password)) {
                mPassword.setError("Enter your password to log in.");
                LoginButton.setText("Log In");
            } else {
                // LoginButton.startAnimation();
                LoginButton.startAnimation();
                //mProgressBar.setVisibility(View.VISIBLE);
                loadingLoginTV.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                final FirebaseUser user = mAuth.getCurrentUser();

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    try {
                                        //CHECKING IF EMAIL IS VERIFIED
                                        if (Objects.requireNonNull(user).isEmailVerified()) {
                                            Log.d(TAG, "OnComplete success : email is verfied");
                                            LoginButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                                @Override
                                                public void onAnimationStopEnd() {
                                                    myRef.child(mContext.getString(R.string.dbname_users))
                                                            .child(mAuth.getCurrentUser().getUid())
                                                            .child(mContext.getString(R.string.field_is_email_verified))
                                                            .setValue("true");

//                                                    EThreeParams params = new EThreeParams(mAuth.getCurrentUser().getUid(), getAuthTokenUserOne, mContext);
//                                                    EThree eThreeSender = new EThree(params);

                                                    EndToEndEncrypt E2EUser = new EndToEndEncrypt(mAuth.getCurrentUser().getUid(), mContext);
                                                    //DerivedPasswords derivedPasswords = EThree.derivePasswords(password);
                                                    // Log.d(TAG, "onAnimationStopEnd: backup Password: " + derivedPasswords.getBackupPassword());
                                                    ///Log.d(TAG, "onAnimationStopEnd: login Password: " + derivedPasswords.getLoginPassword());

                                                    com.virgilsecurity.common.callback.OnCompleteListener restoreListener = new com.virgilsecurity.common.callback.OnCompleteListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Log.d(TAG, "onSuccess: reached");
                                                            Intent intent = new Intent(LogInActivity.this, NavigatingCardActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                            Toast.makeText(LogInActivity.this, "     WELCOME TO THE \n St Xavier's Social Club!",
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onError(@NonNull Throwable throwable) {
                                                            Log.d(TAG, "onError: registering error");
                                                            throwable.printStackTrace();
                                                        }
                                                    };



//                                                    Intent intent = new Intent(LogInActivity.this, NavigatingCardActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//
//                                                    Toast.makeText(LogInActivity.this, "     WELCOME TO THE \n St Xavier's Social Club!",
//                                                            Toast.LENGTH_LONG).show();
//                                                    //
                                                    if (!E2EUser.hasLocalPrivateKey()) {
                                                        E2EUser.restorePrivateKey(EThree.derivePasswords(password).getBackupPassword()).addCallback(restoreListener);
                                                    } else {

                                                        Intent intent = new Intent(LogInActivity.this, NavigatingCardActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                        Toast.makeText(LogInActivity.this, "     WELCOME TO THE \n St Xavier's Social Club!",
                                                                Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });


                                        } else {
                                            LoginButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                            Toast.makeText(LogInActivity.this, "Your email is not verified.\n Check your email Inbox",
                                                    Toast.LENGTH_LONG).show();

//                                            final Handler handler = new Handler(Looper.getMainLooper());
//                                            handler.postDelayed(() -> {
//                                                LoginButton.setBackgroundResource(R.drawable.custom_button_login);
//                                                LoginButton.revertAnimation();
//                                            }, 2000);
                                            loadingLoginTV.setVisibility(View.GONE);
                                            mAuth.signOut();
                                        }
                                    } catch (NullPointerException e) {
                                        Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                                    LoginButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);

                                    Toast.makeText(LogInActivity.this, task.getException().toString(),
                                            Toast.LENGTH_LONG).show();

//                                    final Handler handler = new Handler(Looper.getMainLooper());
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            LoginButton.setBackgroundResource(R.drawable.custom_button_login);
//                                            LoginButton.revertAnimation();
//                                        }
//                                    }, 2000);

                                }
                                //LoginButton.setText("Log In");


                                //LoginButton.revertAnimation();
                                loadingLoginTV.setVisibility(View.GONE);


                                // ...
                            }
                        });

            }
        });

        TextView linkSignUp = findViewById(R.id.linkSignUpOnLogin);
        linkSignUp.setOnClickListener(view -> {
            Log.d(TAG, "SING UP LINK CLICKED ON LOGIN");
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

/*
 NAVIGATING TO HOME ACTIVIYT IF USER IS LOGGED IN & FINISHING LOG IN
*/
//        if (mAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
//
//        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(false);
    }
}