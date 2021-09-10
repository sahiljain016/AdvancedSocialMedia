package com.gic.memorableplaces.SignUp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.DataModels.UserCard;
import com.gic.memorableplaces.Home.HomeActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.Share.ShareActivity;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.QueryDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Objects.requireNonNull;

public class ProfilePhotoFragment extends Fragment {
    private static final String TAG = "ProfilePhotoFragment";

    private String sUsername, sUserDescription;
    private AppCompatButton mAddPP, mRemovePP;
    private CircleImageView mProfilePhoto;
    private ImageView EditDesp, ClearDesp;
    private TextView mUserDescription;
    private AppCompatButton mBackCard, mNextCard;

    private Context mContext;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String PPUrl;
    private StringBuilder stringBuilder, SocietyStringBuilder, PronounStringBuilder, CRStringBuilder, ZodiacStringBuilder, OtherPostStringBuilder,
            GameStringBuilder, MusicStringBuilder,MovieStringBuilder, BooksStringBuilder, HobbiesStringBuilder, Top5PlatformsStringBuilder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_pt_3, container, false);
        Log.d(TAG, "onCreateView: ProfilePhotoFragment");
        mContext = getActivity();
        stringBuilder = new StringBuilder();



        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAddPP = view.findViewById(R.id.AddPP);
        mRemovePP = view.findViewById(R.id.RemovePP);
        EditDesp = view.findViewById(R.id.EditAutoDesp);
        ClearDesp = view.findViewById(R.id.ClearDesp);
        mProfilePhoto = view.findViewById(R.id.profile_photo_card);
        mUserDescription = view.findViewById(R.id.autoDescription);
        mNextCard = view.findViewById(R.id.next_card);
        mBackCard = view.findViewById(R.id.back_card);

        Query Page_query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_page_number));

        Page_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.getValue().toString().equals(mContext.getString(R.string.card_completed)) || !snapshot.exists()){
                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field_page_number))
                            .setValue("3");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (getArguments() != null) {
            sUsername = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username)));
            sUserDescription = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_description)));

        }
        // GetDescriptionFromDatabase();
        GetDescriptionFromDatabase();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DevelopUserDescription();
            }
        }, 1000);
        mAddPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Changing Profile Photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra(getString(R.string.Update_profile), true);
                requireActivity().startActivity(intent);
            }
        });

        mBackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardInformationFragment fragment = new CardInformationFragment();
                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.Card_information_fragment));
                Transaction.commit();
                requireFragmentManager().beginTransaction().remove(ProfilePhotoFragment.this).commit();
            }
        });

        mNextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                        .child(requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(mContext.getString(R.string.field_card_bio))
                        .setValue(mUserDescription.getText().toString());
                myRef.child(mContext.getString(R.string.dbname_users))
                        .child(mAuth.getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field_page_number))
                        .setValue(mContext.getString(R.string.card_completed));
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                requireFragmentManager().beginTransaction().remove(ProfilePhotoFragment.this).commit();
            }
        });

        mRemovePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                firebaseMethods.removeProfilePhoto(PPUrl);
            }
        });
        Query query = myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_profile_photo));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (TextUtils.isEmpty(snapshot.getValue().toString())) {
                    mProfilePhoto.setImageResource(R.drawable.ic_default_user);

                    myRef.child(mContext.getString(R.string.dbname_user_photos))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field_profile_photo))
                            .setValue("N/A");

                } else {
                    PPUrl = snapshot.getValue().toString();
                    GlideImageLoader.loadImageWithOutTransition(mContext, snapshot.getValue().toString(), mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ClearDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Automatic Description.")
                        .setIcon(R.drawable.ic_alert_icon)
                        .setMessage("This will delete all of the auto description that has been created for you completely!")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        mUserDescription.setText("");
                                        stringBuilder.delete(0, stringBuilder.length());
                                    }
                                }
                        )
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .create();
                builder.show();
            }
        });
        return view;
    }

    private void GetDescriptionFromDatabase() {
        stringBuilder = new StringBuilder();
        Runnable DescriptionRunnable = new Runnable() {
            @Override
            public void run() {
                for (DataSnapshot DescriptionSnapshot : QueryDatabase.MethodSnapshot.getChildren()) {
                    if (DescriptionSnapshot.getKey().equals(mContext.getString(R.string.field_description))) {
                        Log.d(TAG, "run: value 1 " + DescriptionSnapshot.getValue().toString());
                        stringBuilder.append(DescriptionSnapshot.getValue().toString());
                    }
                }
            }
        };
        Runnable CourseRunnable = new Runnable() {
            @Override
            public void run() {
                for (DataSnapshot DescriptionSnapshot : QueryDatabase.MethodSnapshot.getChildren()) {
                    if (DescriptionSnapshot.getKey().equals(mContext.getString(R.string.field_course))) {
                        Log.d(TAG, "run: value 2 " + DescriptionSnapshot.getValue().toString());

                        stringBuilder.append(" pursuing " + "<B>").append(DescriptionSnapshot.getValue().toString()).append("</B>");
                    }
                }
            }
        };
        QueryDatabase.TwoLineQuerySingleValueEvent(DescriptionRunnable, mContext.getString(R.string.dbname_user_account_settings), mAuth.getCurrentUser().getUid());
        QueryDatabase.TwoLineQuerySingleValueEvent(CourseRunnable, mContext.getString(R.string.dbname_users), mAuth.getCurrentUser().getUid());
    }

    private void DevelopUserDescription() {
        Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                stringBuilder.append(sUserDescription);

                UserCard userCard = snapshot.getValue(UserCard.class);
                Log.d(TAG, "onDataChange: snapshot " + snapshot.getValue().toString());
                SocietyStringBuilder = new StringBuilder();
                GameStringBuilder = new StringBuilder();
                PronounStringBuilder = new StringBuilder();
                CRStringBuilder = new StringBuilder();
                OtherPostStringBuilder = new StringBuilder();
                ZodiacStringBuilder = new StringBuilder();
                MusicStringBuilder = new StringBuilder();
                MovieStringBuilder = new StringBuilder();
                BooksStringBuilder = new StringBuilder();
                HobbiesStringBuilder = new StringBuilder();
                Top5PlatformsStringBuilder = new StringBuilder();
                for (DataSnapshot CardChildren : snapshot.getChildren()) {

                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_society))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.society_name), SocietyStringBuilder, "");
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_games))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.field_game_name), GameStringBuilder, "");
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_music))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.field_track_name), MusicStringBuilder, mContext.getString(R.string.no_selection));
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_movie))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.field_movie_name), MovieStringBuilder, mContext.getString(R.string.no_selection));
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_books))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.field_book_name), BooksStringBuilder, mContext.getString(R.string.no_selection));
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_other_posts))) {
                        BuildFieldStringBuilder(CardChildren, mContext.getString(R.string.field_position_name_in_other_post), BooksStringBuilder, "false");
                    }
                    int i = 0;
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_hobbies))) {

                        for (DataSnapshot HobbiesChildren : CardChildren.getChildren()) {
                            Log.d(TAG, "onDataChange: card children count" + CardChildren.getChildrenCount());
                            if (i == (CardChildren.getChildrenCount() - 1)) {
                                HobbiesStringBuilder.append(HobbiesChildren.getValue().toString());
                            } else {
                                HobbiesStringBuilder.append(HobbiesChildren.getValue().toString()).append(", ");
                            }
                            i++;
                        }
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.field_other_posts))) {
                        if (!CardChildren.getValue().toString().equals("false")) {
                            for (DataSnapshot OtherPostChildren : CardChildren.getChildren()) {
                                Log.d(TAG, "onDataChange: card children count" + CardChildren.getChildrenCount());
                                if(OtherPostChildren.getKey().equals(mContext.getString(R.string.field_position_name_in_other_post))){
                                    OtherPostStringBuilder.append(OtherPostChildren.getValue().toString());
                                }
                                if(OtherPostChildren.getKey().equals(mContext.getString(R.string.field_organization_name_in_other_post))){
                                    OtherPostStringBuilder.append("at ").append(OtherPostChildren.getValue().toString()).append(". ");
                                }
                                if(OtherPostChildren.getKey().equals(mContext.getString(R.string.field_remark_in_other_post))){
                                    OtherPostStringBuilder.append(OtherPostChildren.getValue().toString());
                                }
                            }
                        }
                    }
                    if (Objects.equals(CardChildren.getKey(), mContext.getString(R.string.Top5Platform))) {
                        i = 0;
                        for (DataSnapshot Top5PlatformsChildren : CardChildren.getChildren()) {
                            if (i == (CardChildren.getChildrenCount() - 1)) {
                                Top5PlatformsStringBuilder.append(Top5PlatformsChildren.getValue().toString());
                            } else {
                                Top5PlatformsStringBuilder.append(Top5PlatformsChildren.getValue().toString()).append(", ");
                            }
                            i++;
                        }
                    }
                }

                stringBuilder.append(". I am in the ").append("<B>").append(userCard.getCollege_year()).append("</B>").append(" of my College Life. ");
                stringBuilder.append("I was born on ").append("<B>").append(userCard.getBirth_date()).append("</B>").append(" ");
                stringBuilder.append("& some quick math says that I am ").append("<B>").append(userCard.getAge()).append("</B>").append(" years old. ");
                stringBuilder.append("My zodiac Sign is ").append("<B>").append(userCard.getZodiac_sign()).append("</B>").append(". ");

                // Put If Condition for no gender
                if (!userCard.getGender().equals(mContext.getString(R.string.field_no_gender))) {
                    stringBuilder.append("I identify as ").append("<B>").append(userCard.getGender()).append("</B>").append(". ");
                }
                if (!userCard.getPronoun_preferred().equals("N/A")) {
                    stringBuilder.append("The pronouns that I prefer are ").append("<B>").append(userCard.getPronoun_preferred()).append("</B>").append(". ");
                }
                if (userCard.getClass_representative().equals("true")) {
                    stringBuilder.append("I am also the ").append("<B>").append("Class Representative (CR) ").append("</B>").append("of my class. ");
                }
                if(!TextUtils.isEmpty(OtherPostStringBuilder.toString())){
                    stringBuilder.append("I am the ").append("<B>").append(OtherPostStringBuilder.toString()).append("</B>").append(". ");
                }
                stringBuilder.append("Now, Here are some insights into my choices over common subjects in life. \n");
                if (!TextUtils.isEmpty(SocietyStringBuilder.toString())) {
                    stringBuilder.append("I am a part of <B>").append(SocietyStringBuilder.toString()).append("</B>. These were the best societies for me in my college. ");
                }
                if (!TextUtils.isEmpty(GameStringBuilder.toString())) {
                    stringBuilder.append("I am quite the gamer, the games that I love include <B>").append(GameStringBuilder.toString()).append("</B> ");
                    stringBuilder.append(". Also, the gaming platforms that I frequently use are <B>").append(Top5PlatformsStringBuilder.toString()).append("</B>.");
                }
                if (!TextUtils.isEmpty(HobbiesStringBuilder.toString())) {
                    stringBuilder.append("Few areas that are interesting to me include <B>").append(HobbiesStringBuilder.toString()).append("</B>, you can also call them my hobbies. ");
                }
                if (MusicStringBuilder.toString().equals(mContext.getString(R.string.no_selection)))
                    stringBuilder.append("I am not really fond of any music.");
                else if(!TextUtils.isEmpty(MusicStringBuilder.toString()))
                    stringBuilder.append("I am also quite fond of music, some of my favourite tracks are <B>").append(MusicStringBuilder.toString()).append("</B>. ");

                if (MovieStringBuilder.toString().equals(mContext.getString(R.string.no_selection)))
                    stringBuilder.append("I am not really fond of any movies.");
                else if(!TextUtils.isEmpty(MovieStringBuilder.toString()))
                    stringBuilder.append("I loved watching movies like <B>").append(MovieStringBuilder.toString()).append("</B>. ");

                if (BooksStringBuilder.toString().equals(mContext.getString(R.string.no_selection)))
                    stringBuilder.append("I am not really fond of any books.");
                else if(!TextUtils.isEmpty(BooksStringBuilder.toString()))
                    stringBuilder.append("I also like to read a few books, my best reads are <B>").append(BooksStringBuilder.toString()).append("</B>. ");

                stringBuilder.append("So, that's it about me. Thanks for reading. Bye!");

                mUserDescription.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
//                for(DataSnapshot CardChildren : snapshot.getChildren()){
//                    if(CardChildren.getKey().equals(mContext.getString(R.string.field_college_year))){
//                        stringBuilder.append("I am in the " + CardChildren.getValue().toString() + " of my College Life.");
//                    }
//                    if()
//                }
                EditDesp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog dialog = new Dialog(mContext);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        EditText editText = new EditText(mContext);
                        editText.setTextSize(18);
                        editText.setPadding(10, 5, 10, 5);
                        editText.setTextColor(Color.WHITE);
                        editText.setBackgroundColor(Color.BLACK);
                        Log.d(TAG, "onClick: string builder " + stringBuilder);
                        editText.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));
                        dialog.setContentView(editText);
                        dialog.show();
                    }
                });
                // stringBuilder.delete(0, stringBuilder.length());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void BuildFieldStringBuilder(DataSnapshot CardChildren, String field, StringBuilder stringBuilder, String NoValue) {
        int j = 0;

        if (CardChildren.getValue().toString().equals(NoValue)) {
            stringBuilder.append(CardChildren.getValue().toString());
        } else {
            Log.d(TAG, "BuildFieldStringBuilder: CardChildren " + CardChildren.getValue().toString());
            for (DataSnapshot ChildrenField : CardChildren.getChildren()) {
                for (DataSnapshot Fields : ChildrenField.getChildren()) {
                    if (Objects.equals(Fields.getKey(), field)) {
                        if (j == (CardChildren.getChildrenCount() - 1)) {
                            stringBuilder.append(Fields.getValue().toString());
                        } else {
                            stringBuilder.append(Fields.getValue().toString()).append(", ");
                        }
                    }
                }
                j++;
            }
        }

    }
}
