package com.gic.memorableplaces.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.gic.memorableplaces.Share.ShareActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gic.memorableplaces.Dialogs.ConfirmPasswordDialog;
import com.gic.memorableplaces.DataModels.AllUserSettings;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.UniversalImageLoader;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements
    ConfirmPasswordDialog.OnConfirmingPasswordListener{

private EditText ConfirmPasswordETDialog;
    @Override
    public void OnConfirmPassword(String password) {
        Log.d(TAG, "OnConfirmPassword: Authenticating user");

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()), password);

        // Prompt the user to re-provide their sign-in credentials
        //////////////////////////////re authenticating the user
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            ///////////////////////check to see if email already exists.
                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    Log.d(TAG, "" + Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size());
                                    if (task.isSuccessful()){
                                        try{
                                                if (task.getResult().getSignInMethods().size() == 0) {
                                                    // email not existed
                                                    Log.d(TAG, "onComplete: Email not exists already");
                                                    mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "User email address updated.");
                                                                        Toast.makeText(getActivity(), "Email Updated Successfully!", Toast.LENGTH_SHORT).show();
                                                                        mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                    }
                                                                }
                                                            });

                                                } else {
                                                    // email existed
                                                    Log.d(TAG, "onComplete: Email exists already cannot change");
                                                    mEmail.setError("Email already in use!");
                                                }
                                        }catch (NullPointerException e){

                                            Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                        }
                                }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        else{
                            Log.d(TAG, "user not authenticated. ");
                        }
                    }
                });
    }


    private static final String TAG = "EditProfieFragment";
    private static final int Activity_num = 4;
    private AllUserSettings mAllUserSettings ;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    // edit profile fragment widgets
    private EditText mFullName, mUsername, mDescription, mWebsite, mEmail, mPhone, mUID, mCourse, mGender;
    private TextView mcancelButtonEditProfile, mdoneButtonEditProfile,mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile,container,false);

        mFullName = view.findViewById(R.id.FullNameEP);
        mUsername = view.findViewById(R.id.UsernameEP);
        mDescription = view.findViewById(R.id.DescriptionEP);
        mWebsite = view.findViewById(R.id.WebsiteEP);
        mEmail = view.findViewById(R.id.EmailEP);
        mPhone = view.findViewById(R.id.PhoneEP);
        mUID = view.findViewById(R.id.UIDEP);
        mCourse = view.findViewById(R.id.CourseEP);
        mGender = view.findViewById(R.id.GenderEP);
        ConfirmPasswordETDialog = view.findViewById(R.id.dialogPasswordEditText);

        mProfilePhoto = view.findViewById(R.id.editProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());

        mcancelButtonEditProfile = view.findViewById(R.id.cancelButtonToolBar);
        mdoneButtonEditProfile = view.findViewById(R.id.doneButtonToolBar) ;
        mChangeProfilePhoto = view.findViewById(R.id.ChangeProfilePhoto) ;


        setupFirebaseAuth();

        mcancelButtonEditProfile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "onClick: Cancel Button Edit Profile On Click Works.");
                 requireFragmentManager().beginTransaction().remove(EditProfileFragment.this).commit();
              }
         });

        mdoneButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to save details.");
                SaveProfileSettings();
                //EditProfileFragment test = (EditProfileFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(getString(R.string.edit_profile_fragment));
               /* if (!test.isResumed() && test !=null){
                    Toast toast = Toast.makeText(getActivity(),"Details Saved", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.WHITE);
                    v.setTextSize(18);
                    toast.show();
                }*/
            }
        });

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Changing Profile Photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra(getString(R.string.Update_profile),true);
                requireActivity().startActivity(intent);
            }
        });
        return view;
    }

    /**
     * Responsible for saving the information to the database that the user edited in the widgets.
     * before saving it will check whether the user name, email, phone number, UID is unique or not.
     */
   private void SaveProfileSettings(){
       final String displayName = mFullName.getText().toString();
       final String username = mUsername.getText().toString();
       final String description = mDescription.getText().toString();
       final String website = mWebsite.getText().toString();
       final String email = mEmail.getText().toString();
       final long phoneNumber = Long.parseLong(mPhone.getText().toString());
       final long UID = Long.parseLong(mUID.getText().toString());
       final String course = mCourse.getText().toString();
       final String gender = mGender.getText().toString();

       // Case 1 : if user changes their username
       if(!mAllUserSettings.getUser().getUsername().equals(username)){
           CheckIfUsernameExists(username);

       }
       // Case 2 : if user changes their phone number
       if(!(mAllUserSettings.getUser().getPhone_number() == phoneNumber)){
           CheckIfPhoneNumberExists(phoneNumber);
       }
       // Case 3 : if user changes their email
       if(!mAllUserSettings.getUser().getEmail().equals(email)){
           // Step 1) Re authenticate user
           //   - Confirm the password & email
           ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
           dialog.show(getFragmentManager(),getString(R.string.confirm_password_dialog_fragment));
           dialog.setTargetFragment(EditProfileFragment.this,1);
                }
       // Case 4 : if user changes their UID number
       if(!(mAllUserSettings.getUser().getCollege_uid() == UID)){
           //Change the user's UID
           CheckIfCollegeUIDexists(UID);
       }
       //Case 5 : if user changes their display name
       if(!mAllUserSettings.getUserAccountSettings().getDisplay_name().equals(displayName)){
         //Change the user's display name
        mFirebaseMethods.UpdateUserAccountSettings(displayName,null,null,null,null);
       }
       if(!mAllUserSettings.getUserAccountSettings().getWebsite().equals(website)){
           //Change the user's website
           mFirebaseMethods.UpdateUserAccountSettings(null,website,null,null,null);
       }
       if(!mAllUserSettings.getUserAccountSettings().getCard_bio().equals(description)){
           //Change the user's description
           mFirebaseMethods.UpdateUserAccountSettings(null,null,description,null,null);
       }
       if(!mAllUserSettings.getUser().getCourse().equals(course)){
           //Change the user's website
           mFirebaseMethods.UpdateUserAccountSettings(null,null,null,course,null);
       }
       if(!mAllUserSettings.getUser().getGender().equals(gender)){
           //Change the user's website
           mFirebaseMethods.UpdateUserAccountSettings(null,null,null,null,gender);
       }




   }

    /**
     * checks if user already exists
     * @param username
     */
    private void CheckIfUsernameExists(final String username) {
        Log.d(TAG, "CheckIfUsernameExists: " + username + " already exists!");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    //add the username
                    mFirebaseMethods.updateUsername(username);

                }
                for(DataSnapshot SingleSnapshot : snapshot.getChildren()){
                    if(SingleSnapshot.exists()){
                        Log.d(TAG, "CheckIfUsernameExists: Found A Match " + Objects.requireNonNull(SingleSnapshot.getValue(User.class)).getUsername());
                        mUsername.setError("Username already exists! Try changing it a little bit.");


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    /**
     * checks if phone number already exists
     * @param phoneNumber
     */
    private void CheckIfPhoneNumberExists(final long phoneNumber) {
        Log.d(TAG, "CheckIfUsernameExists: " + phoneNumber + " already exists!");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_phoneNumber))
                .equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    //add the phoneNumber
                    mFirebaseMethods.updatePhoneNumber(phoneNumber);
                }
                for(DataSnapshot SingleSnapshot : snapshot.getChildren()){
                    if(SingleSnapshot.exists()){
                        Log.d(TAG, "CheckIfPhoneNumberExists: Found A Match " + SingleSnapshot.getValue(User.class).getPhone_number());
                        mPhone.setError("Phone Number is already in use!");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    /**
     * checks if college uid already exists
     * @param collegUID
     */
    private void CheckIfCollegeUIDexists(final long collegUID) {
        Log.d(TAG, "CheckIfUsernameExists: " + collegUID + " already exists!");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_collegeUID))
                .equalTo(collegUID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    //add the phoneNumber
                    mFirebaseMethods.updateCollegeUID(collegUID);
                }
                for(DataSnapshot SingleSnapshot : snapshot.getChildren()){
                    if(SingleSnapshot.exists()){
                        Log.d(TAG, "CheckIfPhoneNumberExists: Found A Match " + Objects.requireNonNull(SingleSnapshot.getValue(User.class)).getCollege_uid());
                        mUID.setError("This UID is already linked.");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * responsible for settings all the values from to the widgets
     * @param allUserSettings
     */
    private void setEditProfileWidgets(AllUserSettings allUserSettings){
        //Log.d(TAG, "setEditProfileWidgets: setting widgets with data retrieved from firebase" + allUserSettings.toString());
        //Log.d(TAG, "setEditProfileWidgets: setting widgets with data retrieved from firebase" + allUserSettings.getUser().getEmail());
        //User user = allUserSettings.getUser();

        mAllUserSettings = allUserSettings;
        UserAccountSettings settings  = allUserSettings.getUserAccountSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mFullName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getCard_bio());
        mEmail.setText(allUserSettings.getUser().getEmail());
        mPhone.setText(String.valueOf(allUserSettings.getUser().getPhone_number()));
        mUID.setText(String.valueOf(allUserSettings.getUser().getCollege_uid()));
        mCourse.setText(allUserSettings.getUser().getCourse());
        mGender.setText(allUserSettings.getUser().getGender());

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
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();
        userID = mAuth.getCurrentUser().getUid();

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
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //RETRIEVE USER INFORMATION FROM DATABASE
                setEditProfileWidgets(mFirebaseMethods.getUserSettings(snapshot,mAuth.getCurrentUser().getUid()));

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
