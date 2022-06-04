package com.gic.memorableplaces.Home;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.UserDetailsDatabase;
import com.gic.memorableplaces.SignUp.CourseAndFullNameCardFragment;
import com.gic.memorableplaces.interfaces.UserDetailsDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutorService;

import me.grantland.widget.AutofitTextView;

public class GetStartedFragment extends Fragment {
    private static final String TAG = "GetStartedFragment";
    private AutofitTextView ATV_MAIN_TITLE, ATV_GS_BUTTON,
            ATV_SERIAL_NO_1, ATV_TITLE_1, ATV_SUB_TITLE_1,
            ATV_SERIAL_NO_2, ATV_TITLE_2, ATV_SUB_TITLE_2,
            ATV_SERIAL_NO_3, ATV_TITLE_3, ATV_SUB_TITLE_3,
            ATV_SERIAL_NO_4, ATV_TITLE_4, ATV_SUB_TITLE_4,
            ATV_SERIAL_NO_5, ATV_TITLE_5, ATV_SUB_TITLE_5;
    private RelativeLayout RL_B;
    private boolean isAutoDespEnabled = false;
    //private MotionLayout ML;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private User user;
    private UserDetailsDatabase UD_DETAILS;
    private UserDetailsDao userDetailsDao;
    private ExecutorService databaseWriteExecutor;

    private Context mContext;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_started, container, false);
        mContext = getActivity();
        Log.d(TAG, "onCreateView: " + TAG);
        handler = new Handler(Looper.getMainLooper());

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        user = new User();
        UD_DETAILS = UserDetailsDatabase.getDatabase(mContext);
        databaseWriteExecutor = UD_DETAILS.databaseWriteExecutor;
        userDetailsDao = UD_DETAILS.userDetailsDao();

//        Bundle bundle = new Bundle();
//        if (getArguments() != null) {
//            bundle.putString(mContext.getString(R.string.field_username), getArguments().getString(mContext.getString(R.string.field_username)));
//            bundle.putString(mContext.getString(R.string.field_description), getArguments().getString(mContext.getString(R.string.field_description)));
//            bundle.putString(mContext.getString(R.string.field_course), getArguments().getString(mContext.getString(R.string.field_course)));
//            bundle.putString(mContext.getString(R.string.field_display_name), getArguments().getString(mContext.getString(R.string.field_display_name)));
//        }

        final Dialog Dialog = new Dialog(mContext);
        databaseWriteExecutor.execute(() -> {
            user = userDetailsDao.GetAllDetails(mAuth.getCurrentUser().getUid()).get(0);
            isAutoDespEnabled = user.isAutoDespEnabled();
            handler.post(() -> {
                requireNonNull(Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                Dialog.setContentView(R.layout.dialog_is_auto_desp_enabled);
                ImageView IV_BOX = Dialog.findViewById(R.id.IV_BOX_CFN);
                ImageView IV_TICK = Dialog.findViewById(R.id.IV_TICK_CFN);


                IV_BOX.setOnClickListener(v1 -> {

                    if (isAutoDespEnabled) {
                        IV_TICK.setVisibility(View.GONE);
                        isAutoDespEnabled = false;
                    } else {
                        IV_TICK.setVisibility(View.VISIBLE);
                        isAutoDespEnabled = true;
                    }

                });

                Dialog.setOnCancelListener(dialog -> {
                    getParentFragmentManager().addOnBackStackChangedListener(() -> {
                        Log.d(TAG, "onCreateView: fragment lists: " + getParentFragmentManager().getFragments());
                        Log.d(TAG, "onCreateView: fragemnet count: " + getParentFragmentManager().getBackStackEntryCount());
                    });
                    user.setAutoDespEnabled(isAutoDespEnabled);
                    databaseWriteExecutor.execute(() -> {
                        myRef.child(mContext.getString(R.string.dbname_users))
                                .child(mAuth.getCurrentUser().getUid())
                                .child(mContext.getString(R.string.field_auto_desp_enabled))
                                .setValue(isAutoDespEnabled);
                        userDetailsDao.UpdateAutoDespBool(isAutoDespEnabled, mAuth.getCurrentUser().getUid());
                        CourseAndFullNameCardFragment fragment = new CourseAndFullNameCardFragment();
                        FragmentTransaction Transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                        Transaction.replace(R.id.FrameLayoutCard, fragment);
                        Transaction.addToBackStack(mContext.getString(R.string.course_and_full_name_Card_fragment));
                        Transaction.commit();
                    });
                });

                RL_B.setOnClickListener(v -> {
                    Dialog.show();
                    if (isAutoDespEnabled) {
                        IV_TICK.setVisibility(View.VISIBLE);
                    } else {
                        IV_TICK.setVisibility(View.GONE);
                    }
                });
            });

        });

        ATV_MAIN_TITLE = view.findViewById(R.id.ATV_TITLE_GS);
        ATV_SERIAL_NO_1 = view.findViewById(R.id.ATV_SERIAL_NO_1_GS);
        ATV_SERIAL_NO_2 = view.findViewById(R.id.ATV_SERIAL_NO_2_GS);
        ATV_SERIAL_NO_3 = view.findViewById(R.id.ATV_SERIAL_NO_3_GS);
        ATV_SERIAL_NO_4 = view.findViewById(R.id.ATV_SERIAL_NO_4_GS);
        ATV_SERIAL_NO_5 = view.findViewById(R.id.ATV_SERIAL_NO_5_GS);
        ATV_TITLE_1 = view.findViewById(R.id.ATV_TITLE_1_GS);
        ATV_TITLE_2 = view.findViewById(R.id.ATV_TITLE_2_GS);
        ATV_TITLE_3 = view.findViewById(R.id.ATV_TITLE_3_GS);
        ATV_TITLE_4 = view.findViewById(R.id.ATV_TITLE_4_GS);
        ATV_TITLE_5 = view.findViewById(R.id.ATV_TITLE_5_GS);
        ATV_SUB_TITLE_1 = view.findViewById(R.id.ATV_SUB_TITLE_1_GS);
        ATV_SUB_TITLE_2 = view.findViewById(R.id.ATV_SUB_TITLE_2_GS);
        ATV_SUB_TITLE_3 = view.findViewById(R.id.ATV_SUB_TITLE_3_GS);
        ATV_SUB_TITLE_4 = view.findViewById(R.id.ATV_SUB_TITLE_4_GS);
        ATV_SUB_TITLE_5 = view.findViewById(R.id.ATV_SUB_TITLE_5_GS);
        ATV_GS_BUTTON = view.findViewById(R.id.ATV_BACK_CFN);
        RL_B = view.findViewById(R.id.RL_BUTTON_GS);
        // ML = findViewById(R.id.ML_GS);


        StartAnimation(ATV_SERIAL_NO_1, ATV_TITLE_1, ATV_SUB_TITLE_1, 0);
        StartAnimation(ATV_SERIAL_NO_2, ATV_TITLE_2, ATV_SUB_TITLE_2, 1000);
        StartAnimation(ATV_SERIAL_NO_3, ATV_TITLE_3, ATV_SUB_TITLE_3, 2000);
        StartAnimation(ATV_SERIAL_NO_4, ATV_TITLE_4, ATV_SUB_TITLE_4, 3000);
        StartAnimation(ATV_SERIAL_NO_5, ATV_TITLE_5, ATV_SUB_TITLE_5, 4000);
        handler = new Handler(Looper.getMainLooper());


        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface tf1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        ATV_MAIN_TITLE.setTypeface(tf, Typeface.NORMAL);
        ATV_GS_BUTTON.setTypeface(tf1, Typeface.NORMAL);
        ATV_SERIAL_NO_1.setTypeface(tf1, Typeface.NORMAL);
        ATV_SERIAL_NO_2.setTypeface(tf1, Typeface.NORMAL);
        ATV_SERIAL_NO_3.setTypeface(tf1, Typeface.NORMAL);
        ATV_SERIAL_NO_4.setTypeface(tf1, Typeface.NORMAL);
        ATV_SERIAL_NO_5.setTypeface(tf1, Typeface.NORMAL);
        ATV_TITLE_1.setTypeface(tf1, Typeface.NORMAL);
        ATV_TITLE_2.setTypeface(tf1, Typeface.NORMAL);
        ATV_TITLE_3.setTypeface(tf1, Typeface.NORMAL);
        ATV_TITLE_4.setTypeface(tf1, Typeface.NORMAL);
        ATV_TITLE_5.setTypeface(tf1, Typeface.NORMAL);
        ATV_SUB_TITLE_1.setTypeface(tf1, Typeface.NORMAL);
        ATV_SUB_TITLE_2.setTypeface(tf1, Typeface.NORMAL);
        ATV_SUB_TITLE_3.setTypeface(tf1, Typeface.NORMAL);
        ATV_SUB_TITLE_4.setTypeface(tf1, Typeface.NORMAL);
        ATV_SUB_TITLE_5.setTypeface(tf1, Typeface.NORMAL);


        return view;
    }


    private void StartAnimation(AutofitTextView Sn, AutofitTextView tittle, AutofitTextView sub_title, int startDelay) {

        Animation slideOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out);
        Animation slideIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in);
        slideIn.setStartOffset(startDelay);
        Sn.animate().alpha(1).setDuration(750).setStartDelay(startDelay).start();
        tittle.setAnimation(slideOut);
        sub_title.setAnimation(slideOut);
        slideOut.start();

        tittle.setAnimation(slideIn);
        sub_title.setAnimation(slideIn);
        // slideIn.start();
        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


                tittle.animate().alpha(1).setDuration(1500).setStartDelay(startDelay);
                sub_title.animate().alpha(1).setDuration(1500).setStartDelay(startDelay);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
