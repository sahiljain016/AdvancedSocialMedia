package com.gic.memorableplaces.FilterFriends;

import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.gic.memorableplaces.Adapters.DespAndQARecyclerViewAdapter;
import com.gic.memorableplaces.DataModels.FFUserDetails;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import pl.droidsonroids.gif.GifImageView;

public class UserDetailsFragment extends Fragment {
    private static final String TAG = "UserDetailsFragment";

    private Context mContext;
    private Handler handler;
    private View view;
    private Thread thread1, thread2;
    private final Object lock = new Object();
    private View vDialog;
    private AlertDialog MessageDialog;
    private LayoutInflater inflaterDialog;
    private AlertDialog.Builder builder;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private int iCurrentTab = 1;
    private boolean isFirstTab = true, isSecondTab = true, isThirdTab = true;
    private int CurrentLayout = R.layout.layout_ff_exp_set1;

    private ImageView IV_BACK, IV_DOT_1, IV_DOT_2, IV_DOT_3, IV_DOT_4, IV_DOT_5, IV_BLUR_TOP, IV_LEFT_HUMAN, IV_RIGHT_HUMAN, IV_FOLLOW;
    private CardView CV_CARD, CV_LIKE, CV_HI, CV_MATCH_FF_EXP, CV_TAB_1, CV_TAB_2, CV_TAB_3, CV_IMG_2;
    private AutofitTextView ATV_FULLNAME, ATV_COMMON_LINE, ATV_PREVIEW, ATV_PERCENTAGE, ATV_RIGHT_HUMAN, ATV_LEFT_HUMAN;
    private RoundedImageView RIV_IMG_1, RIV_IMG_2, RIV_IMG_3, RIV_IMG_4, RIV_IMG_5,
            RIV_IMG_1_O, RIV_IMG_2_O, RIV_IMG_3_O, RIV_IMG_4_O, RIV_IMG_5_O, IV_IMAGES;
    private ProgressBar PB_MATCH_PROGRESS;
    private TextView TV_FILTER_1, TV_FILTER_2, TV_FILTER_3, TV_FILTER_4, TV_FILTER_5, TV_FILTER_6;
    private ImageView IV_FILTER_1, IV_FILTER_2, IV_FILTER_3, IV_FILTER_4, IV_FILTER_5, IV_FILTER_6;
    private RelativeLayout RL_FILTER_1, RL_FILTER_2, RL_FILTER_3, RL_FILTER_4, RL_FILTER_5, RL_FILTER_6;
    private MotionLayout ML_OUTER, ML_DETAILS, ML_TAB_1, ML_TAB_2, ML_TAB_3, ML_BOTTOM_INDICATOR, ML_SET_1, ML_SET_2, ML_SET_3;
    private RelativeLayout RL_BG_BLUR, RL_VIEW_STUB_PARENT;
    private TextView TV_MATCH_POWER, TV_DESP;
    private ImageFilterView IV_SWITCH_IMAGE;
    private ViewStub VS_SET_1, VS_SET_2, VS_SET_3;
    private View V_BG_LEFT_HUMAN, V_BG_RIGHT_HUMAN;
//    private BlurLayout BL_BG;

    private FFUserDetails ffUserDetails;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_details, container, false);
        Log.d(TAG, "onCreateView: UserDetailsFragment");

        mContext = getActivity();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(mContext);
        MessageDialog = builder.create();

        IV_IMAGES = view.findViewById(R.id.IV_USER_IMAGES_FF);
        IV_BACK = view.findViewById(R.id.IV_BACK_exp);
        IV_DOT_1 = view.findViewById(R.id.IV_DOT_1_FF);
        IV_DOT_2 = view.findViewById(R.id.IV_DOT_2_FF);
        IV_DOT_3 = view.findViewById(R.id.IV_DOT_3_FF);
        IV_DOT_4 = view.findViewById(R.id.IV_DOT_4_FF);
        IV_DOT_5 = view.findViewById(R.id.IV_DOT_5_FF);

        ML_TAB_1 = view.findViewById(R.id.CL_ABOUT_ME);
        ML_TAB_2 = view.findViewById(R.id.CL_CONNECTED_PEOPLE);
        ML_TAB_3 = view.findViewById(R.id.CL_OPTIONS_FF_EXP);

        ML_OUTER = view.findViewById(R.id.ML_FF_EXP);

        CV_TAB_1 = view.findViewById(R.id.CV_ABOUT_ME);
        CV_TAB_2 = view.findViewById(R.id.CV_CONNECTED_PEOPLE);
        CV_TAB_3 = view.findViewById(R.id.CV_OPTIONS_FF_EXP);


        ML_BOTTOM_INDICATOR = view.findViewById(R.id.ML_BOTTOM_INDICATORS_FF_EXP);
        VS_SET_1 = view.findViewById(R.id.VS_FF_EXP_SET_1);
        VS_SET_2 = view.findViewById(R.id.VS_FF_EXP_SET_2);
        VS_SET_3 = view.findViewById(R.id.VS_FF_EXP_SET_3);
        RL_VIEW_STUB_PARENT = view.findViewById(R.id.RL_CENTER_LAYOUT_FF_EXP);


        TV_MATCH_POWER = view.findViewById(R.id.TV_TITLE_MATCH);

//        CV_FOLLOW = view.findViewById(R.id.CV_FOLLOW_EXP);
//        CV_LIKE = view.findViewById(R.id.CV_LIKE_EXP);
//        CV_HI = view.findViewById(R.id.CV_SEND_HI_EXP);
        //CV_MATCH_FF_EXP = view.findViewById(R.id.CV_MATCH_FF_EXP);
      //  IV_SWITCH_IMAGE = view.findViewById(R.id.IV_MATCH_FF_EXP);

        ATV_PREVIEW = view.findViewById(R.id.ATV_PREVIEW_TEXT);

//
//        RL_FILTER_1 = view.findViewById(R.id.RL_FILTER_1);
//        RL_FILTER_2 = view.findViewById(R.id.RL_FILTER_2);
//        RL_FILTER_3 = view.findViewById(R.id.RL_FILTER_3);
//        RL_FILTER_4 = view.findViewById(R.id.RL_FILTER_4);
//        RL_FILTER_5 = view.findViewById(R.id.RL_FILTER_5);
//        RL_FILTER_6 = view.findViewById(R.id.RL_FILTER_6);


//         new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // DO your work here
//                // get the data
//                VS_CENTER_LAYOUT.setLayoutResource(R.layout.layout_ff_exp_set1);
//                    requireActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // update UI
//                            long TimeBefore = System.currentTimeMillis();
//                            VS_CENTER_LAYOUT.inflate();
//                            VS_CENTER_LAYOUT.setVisibility(View.VISIBLE);
//                            Log.d(TAG, "onCreateView: before execution " + (System.currentTimeMillis() - TimeBefore));
//                        }
//                    });}
//            }).start();
        handler = new Handler(Looper.getMainLooper());
//        final boolean isThreadRunning = false;
        VS_SET_1.setLayoutResource(R.layout.layout_ff_exp_set1);
        VS_SET_1.inflate();
        VS_SET_1.setVisibility(VISIBLE);

        VS_SET_2.setLayoutResource(R.layout.layout_ff_exp_set2);
        VS_SET_2.inflate();
        VS_SET_2.setVisibility(View.GONE);

        VS_SET_3.setLayoutResource(R.layout.layout_ff_exp_set3);
        VS_SET_3.inflate();
        VS_SET_3.setVisibility(View.GONE);

        Thread1();
        thread2.start();


        //Thread1();
//        thread1.start();
        // thread2.start();

        if (getArguments() != null) {
            try {
                ffUserDetails = new FFUserDetails();
                ffUserDetails = (FFUserDetails) getArguments().getSerializable(mContext.getString(R.string.ff_user_details));
                GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(0), IV_IMAGES);
            } catch (Exception ignored) {

            }
        }


        Log.d(TAG, "onCreateView: DESP: " + ffUserDetails.getDesp());
        ML_TAB_1.transitionToEnd();
        ML_BOTTOM_INDICATOR.getTransition(R.id.TRANS_BI_FF_EXP_1);

        CV_TAB_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: TAB 1 Cliked");
                CurrentLayout = R.layout.layout_ff_exp_set1;

                if (iCurrentTab != 1) {
                    // RL_VIEW_STUB_PARENT.animate().alpha(0).setDuration(500).start();
                    // VS_SET_1.animate().alpha(0).setDuration(500).start();

                    handler.postDelayed(() -> {
//                        VS_SET_1.setVisibility(View.VISIBLE);
//                        VS_SET_2.setVisibility(View.GONE);
//                        VS_SET_3.setVisibility(View.GONE);
                        //RL_VIEW_STUB_PARENT.removeAllViewsInLayout();
                        iCurrentTab = 1;

                        //  thread1.run();

                        VS_SET_1.setVisibility(VISIBLE);
                        VS_SET_2.setVisibility(View.GONE);
                        VS_SET_3.setVisibility(View.GONE);


                    }, 500);
                }
                //pauseThread();
                ML_TAB_2.transitionToStart();
                ML_TAB_3.transitionToStart();
                ML_TAB_1.transitionToEnd();

                if (RL_VIEW_STUB_PARENT.getTag().equals("2")) {
                    ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_2_1);
                    ML_BOTTOM_INDICATOR.transitionToEnd();
                } else if (RL_VIEW_STUB_PARENT.getTag().equals("3")) {
                    ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_3_1);
                    ML_BOTTOM_INDICATOR.transitionToEnd();
                }
                RL_VIEW_STUB_PARENT.setTag("1");
            }
        });

        CV_TAB_2.setOnClickListener(v -> {
            Log.d(TAG, "onClick: TAB 2 Cliked");
            CurrentLayout = R.layout.layout_ff_exp_set2;


            if (iCurrentTab != 2) {
                //RL_VIEW_STUB_PARENT.animate().alpha(0).setDuration(500).start();
//                ML_SET_1.transitionToStart();
                handler.postDelayed(() -> {
//                    VS_SET_1.setVisibility(View.GONE);
//                    VS_SET_2.setVisibility(View.VISIBLE);
//                    VS_SET_3.setVisibility(View.GONE);
                    // RL_VIEW_STUB_PARENT.removeAllViewsInLayout();
                    VS_SET_2.setVisibility(VISIBLE);
                    VS_SET_3.setVisibility(View.GONE);
                    VS_SET_1.setVisibility(View.GONE);

                    iCurrentTab = 2;
                    //thread1.run();
                    //Thread1(R.id.ML_SCENE_2, VS_SET_2, R.layout.layout_ff_exp_set2);
                }, 500);
            }
            //pauseThread();
            ML_TAB_3.transitionToStart();
            ML_TAB_1.transitionToStart();
            ML_TAB_2.transitionToEnd();

            if (RL_VIEW_STUB_PARENT.getTag().equals("1")) {
                ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_1_2);
                ML_BOTTOM_INDICATOR.transitionToEnd();
            } else if (RL_VIEW_STUB_PARENT.getTag().equals("3")) {
                ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_3_2);
                ML_BOTTOM_INDICATOR.transitionToEnd();
            }
            RL_VIEW_STUB_PARENT.setTag("2");

        });

        CV_TAB_3.setOnClickListener(v -> {
            Log.d(TAG, "onClick: TAB 3 Cliked");
            CurrentLayout = R.layout.layout_ff_exp_set3;
            if (iCurrentTab != 3) {
                //  RL_VIEW_STUB_PARENT.animate().alpha(0).setDuration(500).start();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //RL_VIEW_STUB_PARENT.removeAllViewsInLayout();
//                        VS_SET_1.setVisibility(View.GONE);
//                        VS_SET_2.setVisibility(View.GONE);
//                        VS_SET_3.setVisibility(View.VISIBLE);
                        //Thread1(R.id.ML_SET_3, VS_SET_3, R.layout.layout_ff_exp_set3);
                        iCurrentTab = 3;
                        VS_SET_2.setVisibility(View.GONE);
                        VS_SET_3.setVisibility(View.VISIBLE);
                        VS_SET_1.setVisibility(View.GONE);
                        //thread1.run();

                    }
                }, 500);
            }
            //pauseThread();

            ML_TAB_2.transitionToStart();
            ML_TAB_3.transitionToEnd();
            ML_TAB_1.transitionToStart();

            if (RL_VIEW_STUB_PARENT.getTag().equals("2")) {
                ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_2_3);
                ML_BOTTOM_INDICATOR.transitionToEnd();
            } else if (RL_VIEW_STUB_PARENT.getTag().equals("1")) {
                ML_BOTTOM_INDICATOR.setTransition(R.id.TRANS_BI_FF_EXP_1_3);
                ML_BOTTOM_INDICATOR.transitionToEnd();
            }

            RL_VIEW_STUB_PARENT.setTag("3");
        });

        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        ATV_PREVIEW.setTypeface(title, Typeface.NORMAL);
//        ATV_FULLNAME.setTypeface(title, Typeface.NORMAL);
//        ATV_DESP.setTypeface(title, Typeface.NORMAL);
//        TV_MATCH_POWER.setTypeface(title, Typeface.NORMAL);

//        CV_MATCH_FF_EXP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(IV_SWITCH_IMAGE.getRotation() == 0){
//                    ML_SWITCH.transitionToEnd();
//                }else{
//                    ML_SWITCH.transitionToStart();
//                }
//            }
//        });
        ML_OUTER.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
//                Log.d(TAG, "onTransitionStarted: prog1: " + motionLayout.getProgress());
                IV_IMAGES.animate().alpha(0).setDuration(200).setStartDelay(700).start();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(0), IV_IMAGES);
                        IV_IMAGES.animate().alpha(1).setDuration(100).start();

                    }
                }, 900);


            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {
                Log.d(TAG, "onTransitionChange: prog: " + String.valueOf(motionLayout.getProgress()).substring(0, 3));
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                Log.d(TAG, "onTransitionStarted: prog3: " + motionLayout.getProgress());
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
                Log.d(TAG, "onTransitionStarted: prog4: " + motionLayout.getProgress());
            }
        });

//        Log.d(TAG, String.format("onCreateView: alsImagesList: %s", alsImagesList));
//        Log.d(TAG, String.format("onCreateView: alsMyFilterList: %s", alsMyFilterList));
//        Log.d(TAG, String.format("onCreateView: alsTargetFilterList: %s", alsTargetFilterList));
//        Log.d(TAG, String.format("onCreateView: alsIconList: %s", alsIconList));


        IV_BACK.setOnClickListener(v -> {
            requireFragmentManager().beginTransaction().remove(UserDetailsFragment.this).commit();
        });
//
//        Typeface title = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
//        ATV_DESP.setTypeface(title, Typeface.NORMAL);
//        ATV_FULLNAME.setTypeface(title, Typeface.NORMAL);
//        ATV_FULLNAME.setText(FullNAME);
//        ATV_DESP.setText(DESP);

        // SetImages();
        //SetFilters();


        return view;
    }

    public void Thread1() {
        try {
            Class.forName("dalvik.system.CloseGuard")
                    .getMethod("setEnabled", boolean.class)
                    .invoke(null, true);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
//
//        thread1 = new Thread(() -> {
//            try {
//
//                RL_VIEW_STUB_PARENT.removeAllViewsInLayout();
//
//                VS_SET_1.setLayoutResource(CurrentLayout);
//                RL_VIEW_STUB_PARENT.addView(VS_SET_1);
//
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        long TimeBefore = System.currentTimeMillis();
//                        RL_VIEW_STUB_PARENT.animate().alpha(1).setDuration(500).start();
//                        View VS_VIEW = VS_SET_1.inflate();
//                        VS_SET_1.setVisibility(VISIBLE);
//                        Log.d(TAG, "onCreateView: before execution " + (System.currentTimeMillis() - TimeBefore));
//
//                    }
//                });
//
//                VS_SET_1.setOnInflateListener(new ViewStub.OnInflateListener() {
//                    @Override
//                    public void onInflate(ViewStub stub, View inflated) {
//
//                        thread2.run();
//
//                    }
//                });
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }

//        });

        thread2 = new Thread(() -> {
            try {


//                VS_SET_1.setOnInflateListener((stub, inflated) -> {


//                    if (iCurrentTab == 1) {
                // long TimeBefore = System.currentTimeMillis();
                ML_SET_1 = view.findViewById(R.id.ML_SET_1);
                RIV_IMG_1 = view.findViewById(R.id.RIV_IMG_INNER_1);
                RIV_IMG_1_O = view.findViewById(R.id.RIV_IMG_OUTER_1);
                RIV_IMG_2 = view.findViewById(R.id.RIV_IMG_INNER_2);
                CV_IMG_2 = view.findViewById(R.id.CV_IMG_2);
                RIV_IMG_2_O = view.findViewById(R.id.RIV_IMG_OUTER_2);
                RIV_IMG_3 = view.findViewById(R.id.RIV_IMG_INNER_3);
                RIV_IMG_3_O = view.findViewById(R.id.RIV_IMG_OUTER_3);
                RIV_IMG_4 = view.findViewById(R.id.RIV_IMG_INNER_4);
                RIV_IMG_4_O = view.findViewById(R.id.RIV_IMG_OUTER_4);
                RIV_IMG_5 = view.findViewById(R.id.RIV_IMG_INNER_5);
                RIV_IMG_5_O = view.findViewById(R.id.RIV_IMG_OUTER_5);
                TV_DESP = view.findViewById(R.id.TV_DESP_FF_EXP);
                ATV_FULLNAME = view.findViewById(R.id.ATV_FULLNAME_EXP);

                SetTypeFace("fonts/Capriola.ttf", ATV_FULLNAME);

                ATV_FULLNAME.setText(ffUserDetails.getFullName());
                TV_DESP.setText(ffUserDetails.getDesp());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        SetImages();
                    }
                });


                //Log.d(TAG, "onCreateView: before execution " + (System.currentTimeMillis() - TimeBefore));
                isFirstTab = false;

//                    } else if (iCurrentTab == 2) {
                Log.d(TAG, "Thread1: second tab reached");
                ML_SET_2 = view.findViewById(R.id.ML_SET_2);
                PB_MATCH_PROGRESS = view.findViewById(R.id.PB_MATCH_PERC_OUTER);
                ATV_LEFT_HUMAN = view.findViewById(R.id.ATV_LEFT_HUMAN_NAME);
                ATV_RIGHT_HUMAN = view.findViewById(R.id.ATV_RIGHT_HUMAN_NAME);
                V_BG_LEFT_HUMAN = view.findViewById(R.id.V_BG_LEFT_HUMAN);
                V_BG_RIGHT_HUMAN = view.findViewById(R.id.V_BG_RIGHT_HUMAN);

                ATV_PERCENTAGE = view.findViewById(R.id.ATV_PERCENTAGE);
                ffUserDetails.setMatchPercentage(100);
                PB_MATCH_PROGRESS.setProgress(ffUserDetails.getMatchPercentage());
                ATV_PERCENTAGE.setText(ffUserDetails.getMatchPercentage() + "%");

                if (ffUserDetails.getMatchPercentage() > 0 && ffUserDetails.getMatchPercentage() <= 25) {
                    V_BG_RIGHT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_red_ff);
                    V_BG_LEFT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_red_ff);
                } else if (ffUserDetails.getMatchPercentage() > 25 && ffUserDetails.getMatchPercentage() <= 50) {
                    V_BG_RIGHT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_yellow_ff);
                    V_BG_LEFT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_yellow_ff);
                } else if (ffUserDetails.getMatchPercentage() > 50 && ffUserDetails.getMatchPercentage() <= 75) {
                    V_BG_RIGHT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_blue_ff);
                    V_BG_LEFT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_blue_ff);
                } else if (ffUserDetails.getMatchPercentage() > 75 && ffUserDetails.getMatchPercentage() <= 100) {
                    V_BG_RIGHT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_green_ff);
                    V_BG_LEFT_HUMAN.setBackgroundResource(R.drawable.rounded_rect_green_ff);
                }
                ATV_LEFT_HUMAN.setText(ffUserDetails.getFullName().substring(0, ffUserDetails.getFullName().indexOf(" ")));
                ATV_RIGHT_HUMAN.setText(ffUserDetails.getMyName().substring(0, ffUserDetails.getMyName().indexOf(" ")));
                isSecondTab = false;

                Log.d(TAG, "Thread1: second tab reached");
                ML_SET_3 = view.findViewById(R.id.ML_SET_3);
                CV_CARD = view.findViewById(R.id.CV_CARD_EXP);
                IV_FOLLOW = view.findViewById(R.id.IV_FOLLOW_EXP);
                CV_LIKE = view.findViewById(R.id.CV_LIKE_EXP);
                CV_HI = view.findViewById(R.id.CV_SEND_HI_EXP);

                TV_FILTER_1 = view.findViewById(R.id.TV_FILTER_1_exp);
                TV_FILTER_2 = view.findViewById(R.id.TV_FILTER_2_exp);
                TV_FILTER_3 = view.findViewById(R.id.TV_FILTER_3_exp);
                TV_FILTER_4 = view.findViewById(R.id.TV_FILTER_4_exp);
                TV_FILTER_5 = view.findViewById(R.id.TV_FILTER_5_exp);
                TV_FILTER_6 = view.findViewById(R.id.TV_FILTER_6_exp);

                RL_FILTER_1 = view.findViewById(R.id.RL_FILTER_1);
                RL_FILTER_2 = view.findViewById(R.id.RL_FILTER_2);
                RL_FILTER_3 = view.findViewById(R.id.RL_FILTER_3);
                RL_FILTER_4 = view.findViewById(R.id.RL_FILTER_4);
                RL_FILTER_5 = view.findViewById(R.id.RL_FILTER_5);
                RL_FILTER_6 = view.findViewById(R.id.RL_FILTER_6);

                IV_FILTER_1 = view.findViewById(R.id.IV_FILTER_ICON);
                IV_FILTER_2 = view.findViewById(R.id.IV_FILTER_ICON_2);
                IV_FILTER_3 = view.findViewById(R.id.IV_FILTER_ICON_3);
                IV_FILTER_4 = view.findViewById(R.id.IV_FILTER_ICON_4);
                IV_FILTER_5 = view.findViewById(R.id.IV_FILTER_ICON_5);
                IV_FILTER_6 = view.findViewById(R.id.IV_FILTER_ICON_6);
                // ML_SET_3.transitionToEnd();
                SetFilters();
                CV_CARD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new DataDisplayFragment();
                        String FragmentName = "Data_Display_Fragment";
                        Bundle bundle = new Bundle();
                        bundle.putString(mContext.getString(R.string.field_user_id), ffUserDetails.getTargetUID());
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.FrameLayoutFilters, Objects.requireNonNull(fragment));
                        transaction.addToBackStack(FragmentName);
                        transaction.commit();
                    }
                });
                IV_FOLLOW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseMethods mFirebaseMethods = new FirebaseMethods(mContext);
                        Runnable ExistsRunnable = new Runnable() {
                            @Override
                            public void run() {
                                IV_FOLLOW.setTag("Followed");
                                IV_FOLLOW.setImageResource(R.drawable.ic_followed_without_bubble);
                            }
                        };
                        Runnable DoesNotExistsRunnable = new Runnable() {
                            @Override
                            public void run() {
                                IV_FOLLOW.setTag("Not Followed");
                                IV_FOLLOW.setImageResource(R.drawable.ic_follow_without_bubble);
                            }
                        };

                        Runnable MainRunnable = new Runnable() {
                            @Override
                            public void run() {
                                String message;
                                if (IV_FOLLOW.getTag().toString().equals("Not Followed")) {
                                    IV_FOLLOW.setTag("Followed");
                                    IV_FOLLOW.setImageResource(R.drawable.ic_followed_without_bubble);


                                    mFirebaseMethods.addNewFollowerAndFollowing(ffUserDetails.getTargetUID());
                                    // Log.d(TAG, "onBindViewHolder: username: " + hmDetailsFinal.get(mUIDList.get(position)).get(mContext.getString(R.string.field_username)));


                                    mFirebaseMethods.addFollowOrMessageToFilterList(ffUserDetails.getTargetUID(),
                                            ffUserDetails.getTargetUsername()
                                            , ffUserDetails.getFiltersMatched(), ffUserDetails.getMyUsername(), ffUserDetails.getMyProfilePic()
                                            , ffUserDetails.getAlsImagesList().get(0), "",
                                            mContext.getString(R.string.field_follow_list_for_notifications), mContext.getString(R.string.field_filters_matched));


                                    message = "You Followed " + ffUserDetails.getTargetUsername() + "!";
                                } else {
                                    IV_FOLLOW.setTag("Not Followed");
                                    IV_FOLLOW.setImageResource(R.drawable.ic_follow_without_bubble);
                                    message = "You UnFollowed " + ffUserDetails.getTargetUsername() + "!";
                                    mFirebaseMethods.removeFollowerAndFollowing(ffUserDetails.getTargetUID());
                                    mFirebaseMethods.DeleteFollowFromFilterList(ffUserDetails.getTargetUID());
                                }

                                MiscTools.InflateBalloonTooltip(mContext, message, 0, v);
                            }
                        };

                        mFirebaseMethods.CheckIfFollowing(ffUserDetails.getTargetUID(), ExistsRunnable, DoesNotExistsRunnable,MainRunnable);

                    }
                });

                CV_HI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inflaterDialog = requireActivity().getLayoutInflater();
                        vDialog = inflaterDialog.inflate(R.layout.dialog_age_filter_search, null);
                        builder.setView(vDialog);
                        MessageDialog = builder.create();
                        MessageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        MessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        MessageDialog.show();

                        AnimatedRecyclerView rResults = vDialog.findViewById(R.id.ARV_MESSAGES);
                        GifImageView MessageGif = vDialog.findViewById(R.id.Message_sent_gif);
                        ArrayList<String> Messages = new ArrayList<>();
                        Messages.add("Hello this is sahil jain, how are you doing? yum yum yum!");
                        DespAndQARecyclerViewAdapter mResultAdapter = new DespAndQARecyclerViewAdapter(Messages, mContext, true,
                                ffUserDetails.getTargetUID(), ffUserDetails.getMyProfilePic(), ffUserDetails.getAlsImagesList().get(0), ffUserDetails.getMyUsername(),
                                ffUserDetails.getTargetUsername(), rResults,
                                MessageDialog, MessageGif);
                        rResults.setItemAnimator(new DefaultItemAnimator());
                        rResults.setAdapter(mResultAdapter);
                        mResultAdapter.notifyDataSetChanged();
                        rResults.scheduleLayoutAnimation();
                    }
                });
                isThirdTab = false;
                //}

//
//                });

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        });
    }

//    void pauseThread() {
//        pause = true;
//    }
//
//    void resumeThread() {
//        pause = false;
//        synchronized (lock) {
//            lock.notifyAll();
//        }
//    }
//
//    void Pause() {
//        synchronized (lock) {
//            try {
//                lock.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void SetTypeFace(String FontPath, AutofitTextView atv) {
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), FontPath);
        atv.setTypeface(tf, Typeface.NORMAL);

    }

    public void SetImages() {
        long TimeBefore = System.currentTimeMillis();
        String IMG1 = "", IMG2 = "", IMG3 = "", IMG4 = "", IMG5 = "";
        if (ffUserDetails.getAlsImagesList().size() >= 1)
            IMG1 = ffUserDetails.getAlsImagesList().get(0);
        if (ffUserDetails.getAlsImagesList().size() >= 2)
            IMG2 = ffUserDetails.getAlsImagesList().get(1);
        if (ffUserDetails.getAlsImagesList().size() >= 3)
            IMG3 = ffUserDetails.getAlsImagesList().get(2);
        if (ffUserDetails.getAlsImagesList().size() >= 4)
            IMG4 = ffUserDetails.getAlsImagesList().get(3);
        if (ffUserDetails.getAlsImagesList().size() == 5)
            IMG4 = ffUserDetails.getAlsImagesList().get(4);

        if (!TextUtils.isEmpty(IMG1)) {
            RIV_IMG_1.setVisibility(VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(0), RIV_IMG_1);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(0), RIV_IMG_1_O);
            RIV_IMG_1.setOnClickListener(v -> GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(0), IV_IMAGES));
        }
        if (!TextUtils.isEmpty(IMG2)) {
            RIV_IMG_2.setVisibility(VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(1), RIV_IMG_2);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(1), RIV_IMG_2_O);
            RIV_IMG_2.setOnClickListener(v -> GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(1), IV_IMAGES));
        }
        if (!TextUtils.isEmpty(IMG3)) {
            RIV_IMG_3.setVisibility(VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(2), RIV_IMG_3);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(2), RIV_IMG_3_O);
            RIV_IMG_3.setOnClickListener(v -> GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(2), IV_IMAGES));

        }
        if (!TextUtils.isEmpty(IMG4)) {
            RIV_IMG_4.setVisibility(VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(3), RIV_IMG_4);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(3), RIV_IMG_4_O);
            RIV_IMG_4.setOnClickListener(v -> GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(3), IV_IMAGES));

        }
        if (!TextUtils.isEmpty(IMG5)) {
            RIV_IMG_5.setVisibility(VISIBLE);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(4), RIV_IMG_5);
            GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(4), RIV_IMG_5_O);
            RIV_IMG_5.setOnClickListener(v -> GlideImageLoader.loadImageWithOutTransition(mContext, ffUserDetails.getAlsImagesList().get(4), IV_IMAGES));

        }

        Log.d(TAG, "onCreateView: Set Images Method before execution " + (System.currentTimeMillis() - TimeBefore));
    }

    private void SetFilters() {
        int SimilarityCount = 0;

        Log.d(TAG, "SetFilters: AlsTargetFilterList " + ffUserDetails.getAlsTargetFilterList());
        Log.d(TAG, "SetFilters: AliIconList " + ffUserDetails.getAliIconList());
        Log.d(TAG, "SetFilters: AlsMyFilter " + ffUserDetails.getAlsMyFilterList());

        if (ffUserDetails.getAlsTargetFilterList().size() >= 1) {
            RL_FILTER_1.setVisibility(VISIBLE);
            TV_FILTER_1.setText(ffUserDetails.getAlsTargetFilterList().get(0));
            IV_FILTER_1.setImageResource(ffUserDetails.getAliIconList().get(0));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(0))) {
                SimilarityCount++;
                RL_FILTER_1.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }
        if (ffUserDetails.getAlsTargetFilterList().size() >= 2) {
            RL_FILTER_2.setVisibility(VISIBLE);
            TV_FILTER_2.setText(ffUserDetails.getAlsTargetFilterList().get(1));
            IV_FILTER_2.setImageResource(ffUserDetails.getAliIconList().get(1));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(1))) {
                SimilarityCount++;
                RL_FILTER_2.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }
        if (ffUserDetails.getAlsTargetFilterList().size() >= 3) {
            RL_FILTER_3.setVisibility(VISIBLE);
            TV_FILTER_3.setText(ffUserDetails.getAlsTargetFilterList().get(2));
            IV_FILTER_3.setImageResource(ffUserDetails.getAliIconList().get(2));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(2))) {
                SimilarityCount++;
                RL_FILTER_3.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }
        if (ffUserDetails.getAlsTargetFilterList().size() >= 4) {
            RL_FILTER_4.setVisibility(VISIBLE);
            TV_FILTER_4.setText(ffUserDetails.getAlsTargetFilterList().get(3));
            IV_FILTER_4.setImageResource(ffUserDetails.getAliIconList().get(3));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(3))) {
                SimilarityCount++;
                RL_FILTER_4.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }
        if (ffUserDetails.getAlsTargetFilterList().size() >= 5) {
            RL_FILTER_5.setVisibility(VISIBLE);
            TV_FILTER_5.setText(ffUserDetails.getAlsTargetFilterList().get(4));
            IV_FILTER_5.setImageResource(ffUserDetails.getAliIconList().get(4));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(4))) {
                SimilarityCount++;
                RL_FILTER_5.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }
        if (ffUserDetails.getAlsTargetFilterList().size() >= 6) {
            RL_FILTER_6.setVisibility(VISIBLE);
            TV_FILTER_6.setText(ffUserDetails.getAlsTargetFilterList().get(5));
            IV_FILTER_6.setImageResource(ffUserDetails.getAliIconList().get(5));
            if (ffUserDetails.getAlsMyFilterList().contains(ffUserDetails.getAlsTargetFilterList().get(5))) {
                SimilarityCount++;
                RL_FILTER_6.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.matched_filter_colour));
            }
        }

//        if (SimilarityCount == 0)
//            ATV_COMMON_LINE.setText("None of these filters match! Let's keep looking.");
//        else
//            ATV_COMMON_LINE.setText("You have " + SimilarityCount + " interests/details in common!");

    }


}
