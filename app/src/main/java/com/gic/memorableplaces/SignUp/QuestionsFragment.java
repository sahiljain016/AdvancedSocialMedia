package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.gic.memorableplaces.Adapters.QuestionsRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.CardStack.SwipeableLayoutManager;
import com.gic.memorableplaces.DataModels.Questions;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.FilterDetailsDatabase;
import com.gic.memorableplaces.interfaces.QuestionsDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import me.grantland.widget.AutofitTextView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class QuestionsFragment extends Fragment implements QuestionsRecyclerViewAdapter.OnAnswerChanged {
    private static final String TAG = "QuestionsFragment";
    private Context mContext;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;

    private SwipeableLayoutManager swipeableLayoutManager;
    private ExecutorService databaseWriteExecutor;
    private QuestionsDao qDao;

    private QuestionsRecyclerViewAdapter qrva;
    private RecyclerView RV_NUMBERS_Q;
    private CardStackView CSV_QUESTION_Q;
    private AppCompatImageButton ACIB_FAV_Q;
    private AppCompatImageButton ACIB_REMOVE_FAV_Q;

    private ArrayList<String> alsQuesList, alsAnswersList;
    private ArrayList<Integer> aliSelectedQues;

    private boolean isAutoDespEnabled;
    private int CurrentItem = 1, sPos;
    private Dialog ET_Dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);
        mContext = getActivity();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);

        alsQuesList = new ArrayList<>();
        alsAnswersList = new ArrayList<>();
        aliSelectedQues = new ArrayList<>();

        RV_NUMBERS_Q = view.findViewById(R.id.RV_NUMBERS_Q);
        AppCompatImageButton ACIB_BACK = view.findViewById(R.id.ACB_BACK_Q);
        ACIB_FAV_Q = view.findViewById(R.id.ACIB_FAV_Q);
        ACIB_REMOVE_FAV_Q = view.findViewById(R.id.ACIB_REMOVE_FAV_Q);
        CSV_QUESTION_Q = view.findViewById(R.id.CSV_QUESTION_Q);
        AutofitTextView ATV_TITLE_1 = view.findViewById(R.id.ATV_TITLE_1_Q);

        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");
        ATV_TITLE_1.setTypeface(cap);

        Bundle PrevBundle = new Bundle();
        if (getArguments() != null) {
            if (getArguments().getBoolean("auto_desp")) {
                isAutoDespEnabled = true;
                PrevBundle.putString(mContext.getString(R.string.field_description), String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_description))));
                Log.d(TAG, "onCreateView: desp: " + String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_description))));
            } else {
                isAutoDespEnabled = false;
            }
            PrevBundle.putString(mContext.getString(R.string.field_username), String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username))));
            PrevBundle.putString(mContext.getString(R.string.field_display_name), String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_display_name))));
            PrevBundle.putString(mContext.getString(R.string.field_course), String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_course))));
        }


        /*swipeableLayoutManager = new SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(450)
                .setMaxShowCount(1)
                .setScaleGap(0.1f)
                .setTransYGap(100);*/


        SetFav();
        DisplayQuestions();
        ShowQuestionSelection();


        ET_Dialog = MiscTools.InflateDialog(mContext, R.layout.layout_answer_q);
        EditText ET = ET_Dialog.findViewById(R.id.ET_ANSWER_Q);
        TextView TV = ET_Dialog.findViewById(R.id.TV_CHARACTER_NO_Q);
        AutofitTextView ATV_OPT_1 = ET_Dialog.findViewById(R.id.ATV_OPTION_1_Q);
        AutofitTextView ATV_OPT_2 = ET_Dialog.findViewById(R.id.ATV_OPTION_2_Q);
        AutofitTextView ATV_OPT_3 = ET_Dialog.findViewById(R.id.ATV_OPTION_3_Q);
        AutofitTextView ATV_OPT_4 = ET_Dialog.findViewById(R.id.ATV_OPTION_4_Q);

        AtomicReference<String> PrevAnswer = new AtomicReference<>("");
        ShowSpotlights();
        ET_Dialog.setOnShowListener(dialog -> {
            String Options = alsQuesList.get(qrva.GetPosition() - 1).substring(alsQuesList.get(qrva.GetPosition() - 1).indexOf("-o") + 2);
            PrevAnswer.set(alsAnswersList.get(qrva.GetPosition() - 1));
            if (Options.equals("N/A")) {
                ATV_OPT_1.setVisibility(View.GONE);
                ATV_OPT_2.setVisibility(View.GONE);
                ATV_OPT_3.setVisibility(View.GONE);
                ATV_OPT_4.setVisibility(View.GONE);
                ET.setVisibility(View.VISIBLE);
                TV.setVisibility(View.VISIBLE);
                if (!alsAnswersList.get(qrva.GetPosition() - 1).equals("N/A")) {
                    ET.setText(alsAnswersList.get(qrva.GetPosition() - 1));
                } else {
                    ET.setText("");
                }
                TV.setText(ET.length() + "/150");
            } else {
                ATV_OPT_1.setVisibility(View.VISIBLE);
                ATV_OPT_2.setVisibility(View.VISIBLE);
                ATV_OPT_3.setVisibility(View.VISIBLE);
                ATV_OPT_4.setVisibility(View.VISIBLE);
                ET.setVisibility(View.GONE);
                TV.setVisibility(View.GONE);

                String Opt1 = Options.substring(0, Options.indexOf("2"));
                String Opt2 = Options.substring((Options.indexOf("2") + 1), Options.indexOf("3"));
                String Opt3 = Options.substring((Options.indexOf("3") + 1), Options.indexOf("4"));
                String Opt4 = Options.substring(Options.indexOf("4") + 1);

                ATV_OPT_1.setText(Opt1);
                ATV_OPT_2.setText(Opt2);
                ATV_OPT_3.setText(Opt3);
                ATV_OPT_4.setText(Opt4);

                ATV_OPT_1.setVisibility(View.VISIBLE);
                ATV_OPT_2.setVisibility(View.VISIBLE);
                ATV_OPT_3.setVisibility(View.VISIBLE);
                ATV_OPT_4.setVisibility(View.VISIBLE);


                if (alsAnswersList.get(qrva.GetPosition() - 1).equals(Opt1)) {
                    ATV_OPT_1.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                    ATV_OPT_1.setTag("selected");
                } else if (alsAnswersList.get(qrva.GetPosition() - 1).equals(Opt2)) {
                    ATV_OPT_2.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                    ATV_OPT_2.setTag("selected");
                } else if (alsAnswersList.get(qrva.GetPosition() - 1).equals(Opt3)) {
                    ATV_OPT_3.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                    ATV_OPT_3.setTag("selected");
                } else if (alsAnswersList.get(qrva.GetPosition() - 1).equals(Opt4)) {
                    ATV_OPT_4.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                    ATV_OPT_4.setTag("selected");
                }


                ATV_OPT_1.setOnClickListener(v -> {
                    if (ATV_OPT_1.getTag().equals("unselected")) {

                        ATV_OPT_1.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                        ATV_OPT_2.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_3.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_4.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

                        ATV_OPT_2.setTag("unselected");
                        ATV_OPT_3.setTag("unselected");
                        ATV_OPT_4.setTag("unselected");
                        ATV_OPT_1.setTag("selected");
                        alsAnswersList.set(qrva.GetPosition() - 1, Opt1);
                    } else {
                        ATV_OPT_1.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_1.setTag("unselected");
                        alsAnswersList.set(qrva.GetPosition() - 1, "N/A");
                    }
                });
                ATV_OPT_2.setOnClickListener(v -> {
                    if (ATV_OPT_2.getTag().equals("unselected")) {
                        ATV_OPT_2.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                        ATV_OPT_1.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_3.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_4.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

                        ATV_OPT_1.setTag("unselected");
                        ATV_OPT_3.setTag("unselected");
                        ATV_OPT_4.setTag("unselected");
                        ATV_OPT_2.setTag("selected");
                        alsAnswersList.set(qrva.GetPosition() - 1, Opt2);
                    } else {
                        ATV_OPT_2.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_2.setTag("unselected");
                        alsAnswersList.set(qrva.GetPosition() - 1, "N/A");
                    }
                });
                ATV_OPT_3.setOnClickListener(v -> {
                    if (ATV_OPT_3.getTag().equals("unselected")) {

                        ATV_OPT_3.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                        ATV_OPT_2.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_1.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_4.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

                        ATV_OPT_2.setTag("unselected");
                        ATV_OPT_1.setTag("unselected");
                        ATV_OPT_4.setTag("unselected");
                        ATV_OPT_3.setTag("selected");
                        alsAnswersList.set(qrva.GetPosition() - 1, Opt3);
                    } else {
                        ATV_OPT_3.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_3.setTag("unselected");
                        alsAnswersList.set(qrva.GetPosition() - 1, "N/A");
                    }
                });
                ATV_OPT_4.setOnClickListener(v -> {
                    if (ATV_OPT_4.getTag().equals("unselected")) {

                        ATV_OPT_4.setBackgroundResource(R.drawable.rounded_background_yellow_20sdp);
                        ATV_OPT_2.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_3.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_1.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);

                        ATV_OPT_2.setTag("unselected");
                        ATV_OPT_3.setTag("unselected");
                        ATV_OPT_1.setTag("unselected");
                        ATV_OPT_4.setTag("selected");
                        alsAnswersList.set(qrva.GetPosition() - 1, Opt4);
                    } else {
                        ATV_OPT_4.setBackgroundResource(R.drawable.border_rounded_grey_20sdp);
                        ATV_OPT_4.setTag("unselected");
                        alsAnswersList.set(qrva.GetPosition() - 1, "N/A");
                    }
                });

            }

        });
        ET_Dialog.setOnCancelListener(dialog ->
        {
            String Options = alsQuesList.get(qrva.GetPosition() - 1).substring(alsQuesList.get(qrva.GetPosition() - 1).indexOf("-o") + 2);

            //                ACIB_FAV_Q.setImageResource(R.drawable.ic_filled_star_grey);
            //                ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
            //                ACIB_FAV_Q.setTag("unselected");
            if (Options.equals("N/A")) {
                if (TextUtils.isEmpty(ET.getText().toString())) {
                    alsAnswersList.set((qrva.GetPosition() - 1), "N/A");
                } else {
                    alsAnswersList.set((qrva.GetPosition() - 1), ET.getText().toString());
                }
            }
            Log.d(TAG, "onCreateView: aliSelectedQues: " + aliSelectedQues);
            Log.d(TAG, "onCreateView: aliSelectedQues: " + aliSelectedQues);
            if (aliSelectedQues.contains(qrva.GetPosition())) {
                if (!alsAnswersList.get(qrva.GetPosition() - 1).equals("N/A")) {
                    firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1));
                    databaseWriteExecutor.execute(() -> qDao.InsertNewAnswer(new Questions(String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1))));
                } else {
                    databaseWriteExecutor.execute(() -> qDao.RemoveAnswer(String.valueOf(qrva.GetPosition())));
                    aliSelectedQues.remove((Integer) qrva.GetPosition());
                    ACIB_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                    firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()));
                }
            }
        });

        ACIB_BACK.setOnClickListener(v ->

        {
            PrevBundle.putBoolean("auto_desp", isAutoDespEnabled);
            requireFragmentManager().beginTransaction().remove(QuestionsFragment.this).commit();
            FillDetailsFragment fragment = new FillDetailsFragment();
            fragment.setArguments(PrevBundle);
            FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FrameLayoutCard, fragment);
            Transaction.addToBackStack(mContext.getString(R.string.fragment_fill_details));
            Transaction.commit();
        });
        AppCompatImageButton ACIB_NEXT = view.findViewById(R.id.ACB_NEXT_Q);

        ACIB_NEXT.setOnClickListener(v -> {
            PrevBundle.putBoolean("auto_desp", isAutoDespEnabled);
            ProfilePhotoFragment fragment = new ProfilePhotoFragment();
            fragment.setArguments(PrevBundle);
            requireFragmentManager().beginTransaction().remove(QuestionsFragment.this).commit();
            FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FrameLayoutCard, fragment);
            Transaction.addToBackStack(mContext.getString(R.string.questions_fragment));
            Transaction.commit();
        });


        return view;
    }

    private void ShowSpotlights() {
        SpotlightConfig spotlightConfig = new SpotlightConfig();

        spotlightConfig.setIntroAnimationDuration(400);
        spotlightConfig.setRevealAnimationEnabled(true);
        spotlightConfig.setPerformClick(true);
        spotlightConfig.setFadingTextDuration(400);
        spotlightConfig.setHeadingTvColor(Color.parseColor("#FFFFFF"));
        spotlightConfig.setHeadingTvSize(32);
        spotlightConfig.setSubHeadingTvColor(Color.parseColor("#DCDCDC"));
        spotlightConfig.setSubHeadingTvSize(16);
        spotlightConfig.setMaskColor(Color.parseColor("#dc000000"));
        spotlightConfig.setLineAnimationDuration(400);
        spotlightConfig.setLineAndArcColor(Color.parseColor("#FFFFD6A5"));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);


        SpotlightView.Builder SB_ANSWER = new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Select Question")
                .subHeadingTvText("After Answering your question. Click this button to include the q/a to your profile. You can select a maximum of 10 Answers.")
                .target(ACIB_FAV_Q)
                .usageId("SB_ANSWER")
                .setListener(spotlightViewId -> {
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                });


        new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Questions")
                .subHeadingTvText("Click on individual number to navigate to the number of question or swipe the card to move to the next question.")
                .target(RV_NUMBERS_Q)
                .usageId("SB_SAME_DETAIL")
                .setListener(spotlightViewId -> {
                    SB_ANSWER.show();
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                }).show();
    }

    private void SetFav() {
        FilterDetailsDatabase FD_DETAILS = FilterDetailsDatabase.getDatabase(mContext);
        qDao = FD_DETAILS.questionsDao();

        databaseWriteExecutor = FD_DETAILS.databaseWriteExecutor;

        for (int i = 0; i < 20; i++)
            alsAnswersList.add("N/A");

        // Log.d(TAG, "SetFav: alsAnswersList: " + alsAnswersList);
        databaseWriteExecutor.execute(() -> {
            for (Questions questions : qDao.GetAllAnswers()) {
                if (questions.getQ_no().equals("1")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {

                        ACIB_FAV_Q.setVisibility(View.VISIBLE);
                        ACIB_REMOVE_FAV_Q.setVisibility(View.INVISIBLE);
                        ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                    });


                }
                alsAnswersList.set((Integer.parseInt(questions.getQ_no()) - 1), questions.getAnswer());
                aliSelectedQues.add(Integer.parseInt(questions.getQ_no()));
            }

            // Log.d(TAG, "SetFav: alsAnswersList after: " + alsAnswersList);

        });
        ACIB_FAV_Q.setOnClickListener(v -> {


            Log.d(TAG, "SetFav:  FAV: " + qrva.GetPosition());
            Log.d(TAG, "SetFav:  aliSelectedQues: " + aliSelectedQues);
            Log.d(TAG, "SetFav:  alsAnswersList: " + alsAnswersList);
            if (!alsAnswersList.get(qrva.GetPosition() - 1).equals("N/A")) {

                // Log.d(TAG, "SetFav: qrvaMain Fixed Pos: " + qrva.GetPosition());
                databaseWriteExecutor.execute(() -> qDao.RemoveAnswer(String.valueOf(qrva.GetPosition())));

                ACIB_FAV_Q.setVisibility(View.INVISIBLE);
                ACIB_REMOVE_FAV_Q.setVisibility(View.VISIBLE);
                ACIB_REMOVE_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                aliSelectedQues.remove((Integer) qrva.GetPosition());
                firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()));

            } else {
                Toast.makeText(mContext, "Please give an answer to select this question.", Toast.LENGTH_SHORT).show();
            }
        });

        ACIB_REMOVE_FAV_Q.setOnClickListener(v -> {

            Log.d(TAG, "SetFav: REMOVE FAV: " + qrva.GetPosition());
            Log.d(TAG, "SetFav:  aliSelectedQues: " + aliSelectedQues);
            Log.d(TAG, "SetFav:  alsAnswersList: " + alsAnswersList);
            if (!alsAnswersList.get(qrva.GetPosition() - 1).equals("N/A")) {
                if (aliSelectedQues.size() < 11) {
                    databaseWriteExecutor.execute(() -> qDao.InsertNewAnswer(new Questions(String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1))));

                    ACIB_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                    if (!aliSelectedQues.contains(qrva.GetPosition()))
                        aliSelectedQues.add(qrva.GetPosition());

                    firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1));
                } else {
                    Toast.makeText(mContext, "You can only answer a maximum of 10 questions", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(mContext, "Please give an answer to select this question.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ShowQuestionSelection() {


        qrva = new QuestionsRecyclerViewAdapter(alsQuesList, aliSelectedQues, alsAnswersList, null, CurrentItem, false, mContext, new QuestionsRecyclerViewAdapter.OnNumberSelected() {
            @Override
            public void onItemClick(int SelectedPos, int Position, TextView TV) {
                Log.d(TAG, "onItemClick: SelectedPos " + SelectedPos);
                Log.d(TAG, "onItemClick: alsAnswersList: " + alsAnswersList);
                Log.d(TAG, "onItemClick: aliSelectedQues:" + aliSelectedQues);

                CSV_QUESTION_Q.scrollToPosition(SelectedPos - 1);
                if (aliSelectedQues.contains(SelectedPos)) {
                    Log.d(TAG, "onItemClick: SelectedPos true");
                    ACIB_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                } else {

                    Log.d(TAG, "onItemClick: SelectedPos false");
                    ACIB_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                }

                qrva.notifyItemRangeChanged(0, 20, SelectedPos);
               /*
                TV.setTextColor(Color.parseColor("#FFFFFF"));
                qrva.notifyItemChanged(fixedPos - 1, SelectedPos);
                if (aliSelectedQues.contains(SelectedPos)) {
                    ACIB_FAV_Q.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FCBF29")));
                    ACIB_FAV_Q.setTag("selected");
                    ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                } else {
                    ACIB_FAV_Q.setImageTintList(ColorStateList.valueOf(Color.parseColor("#D1D1D1")));
                    ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                    ACIB_FAV_Q.setTag("unselected");
                }
                for (int i = (alsQuesList.size() - 1); i > (alsQuesList.size() - fixedPos); i--) {
                    String ques = alsQuesList.get(alsQuesList.size() - 1);
                    String options = alsOptionsList.get(alsQuesList.size() - 1);
                    String ans = alsAnswersList.get(alsQuesList.size() - 1);
                    alsQuesList.remove(alsQuesList.size() - 1);
                    alsQuesList.add(0, ques);
                    alsOptionsList.remove(alsQuesList.size() - 1);
                    alsOptionsList.add(0, options);
                    alsAnswersList.remove(alsQuesList.size() - 1);
                    alsAnswersList.add(0, ans);
                }

                Log.d(TAG, "onItemClick: alsQuesList middle : " + alsQuesList);
                for (int i = 1; i < SelectedPos; i++) {
                    String ques = alsQuesList.get(0);
                    String options = alsOptionsList.get(0);
                    String ans = alsAnswersList.get(0);
                    alsQuesList.remove(0);
                    alsQuesList.add(ques);
                    alsOptionsList.remove(0);
                    alsOptionsList.add(options);
                    alsAnswersList.remove(0);
                    alsAnswersList.add(ans);
                }
                Log.d(TAG, "onItemClick: alsQuesList after: " + alsQuesList);
                QuestionsFragment.this.qrvaMain = new QuestionsRecyclerViewAdapter(alsQuesList, alsOptionsList, aliSelectedQues, alsAnswersList, ACIB_FAV_Q, CurrentItem, true, mContext, null);
                RV_QUESTIONS_Q.setLayoutManager(swipeableLayoutManager);
                RV_QUESTIONS_Q.setAdapter(QuestionsFragment.this.qrvaMain);
                QuestionsFragment.this.qrvaMain.notifyDataSetChanged();
                RV_QUESTIONS_Q.setNestedScrollingEnabled(true);


                CurrentItem = SelectedPos;
                qrva.notifyItemChanged(fixedPos, CurrentItem);
                qrva.notifyItemChanged(SelectedPos, CurrentItem);*/
            }
        }, null);

        ZoomRecyclerLayout mLayoutManager = new ZoomRecyclerLayout(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_NUMBERS_Q.setHasFixedSize(true);
        RV_NUMBERS_Q.setLayoutManager(mLayoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(RV_NUMBERS_Q);

        RV_NUMBERS_Q.setAdapter(qrva);
        qrva.notifyItemRangeInserted(0, 20);
    }

    private void DisplayQuestions() {
//        SwipeableTouchHelperCallback swipeableTouchHelperCallback = new SwipeableTouchHelperCallback(new OnItemSwiped() {
//            @Override
//            public void onItemSwiped() {
//                String ques = alsQuesList.get(0);
//                alsQuesList.remove(0);
//                alsQuesList.add(ques);
//                qrvaMain.ShiftTopAnswerToLast();
//                qrvaMain.notifyItemRemoved(0);
//                qrvaMain.notifyItemInserted(alsQuesList.size());
//
//            }
//
//            @Override
//            public void onItemSwipedLeft() {
//                Log.d(TAG, "onItemSwipedLeft: LEFT SWIPED");
//
//                if (ACIB_FAV_Q.getTag().equals("selected")) {
//                    firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrvaMain.GetFixedPosition()), null);
//                    databaseWriteExecutor.execute(() -> qDao.RemoveAnswer(String.valueOf(qrvaMain.GetFixedPosition())));
//                    aliSelectedQues.remove((Integer) qrvaMain.GetFixedPosition());
//                }
//            }
//
//            @Override
//            public void onItemSwipedRight() {
//                Log.d(TAG, "onItemSwipedRight: RIGHT SWIPED");
//                if (!alsAnswersList.get(0).equals("N/A") || !TextUtils.isEmpty(alsAnswersList.get(0))) {
//                    if (ACIB_FAV_Q.getTag().equals("unselected")) {
//                        if (aliSelectedQues.size() < 11) {
//                            databaseWriteExecutor.execute(() -> qDao.InsertNewAnswer(new Questions(String.valueOf(qrvaMain.GetFixedPosition()), alsAnswersList.get(0))));
//                            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrvaMain.GetFixedPosition()), alsAnswersList.get(0));
//                            aliSelectedQues.add(qrvaMain.GetFixedPosition());
//                        } else {
//                            Toast.makeText(mContext, "You can only answer a maximum of 10 questions", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//            }
//        }) {
//            @Override
//            public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
//                return ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeableTouchHelperCallback);
//
//        itemTouchHelper.attachToRecyclerView(RV_QUESTIONS_Q);


        alsQuesList.add("1*What are your some of your dream destinations?-oN/A");
        alsQuesList.add("2*What are your favourite restaurants in your locality?-oN/A");
        alsQuesList.add("3*What is your idea of a fun night?-oN/A");
        alsQuesList.add("4*Do you agree with splitting the bill on the first date?-oAbsolutely2Never3Whoever asked out should pay4No Opinion");
        alsQuesList.add("5*What superpower do you wish for?-oN/A");
        alsQuesList.add("6*What are some pros of dating me?-oN/A");
        alsQuesList.add("7*Are you comfortable with giving/receiving expensive gifts?-oAlways2Sometimes3Only on occasions4Never");
        alsQuesList.add("8*What is your idea of a perfect first date?-oN/A");
        alsQuesList.add("9*What is something that you can't tolerate?-oN/A");
        alsQuesList.add("10*What are your three wishes, in case you find a genie?-oN/A");
        alsQuesList.add("11*Can you have a long distance distance relationship?-oYes, I can work with it2Maybe, I am not sure3Not right away4No, I cannot");
        alsQuesList.add("12*What is the one thing that you can't live without?-oN/A");
        alsQuesList.add("13*Something that I will definitely want my partner to try-oN/A");
        alsQuesList.add("14*How do you feel about marriage?-oI want to get married soon2After a fair amount of time3Not in the near future4Never");
        alsQuesList.add("15*One thing that scares you the most?-oN/A");
        alsQuesList.add("16*What is the one thing that you do daily?-oN/A");
        alsQuesList.add("17*Do you engage in social work?-oN/A");
        alsQuesList.add("18*You need swipe right, if....-oN/A");
        alsQuesList.add("19*How possessive are you in a relationship?-oI have a problem2 A bit possessive3It depends4I do not care much");
        alsQuesList.add("20*Can you find time for both your carrier and relationship?-oN/A");

        CardStackLayoutManager cardStackLayoutManager = new CardStackLayoutManager(mContext, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

                //  Log.d(TAG, "onCardRewound: dragging dir: " + direction.name());
            }

            @Override
            public void onCardSwiped(Direction direction) {


                Log.d(TAG, "SetFav: qrva position: " + qrva.GetPosition());

                int pos = qrva.GetPosition();
                if (qrva.GetPosition() == 20) {
                    pos = 0;
                }
                CSV_QUESTION_Q.scrollToPosition(pos);
                if (aliSelectedQues.contains(pos + 1)) {
                    Log.d(TAG, "onItemClick: SelectedPos true");
                    ACIB_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                } else {

                    Log.d(TAG, "onItemClick: SelectedPos false");
                    ACIB_FAV_Q.setVisibility(View.INVISIBLE);
                    ACIB_REMOVE_FAV_Q.setVisibility(View.VISIBLE);
                    ACIB_REMOVE_FAV_Q.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                }

                qrva.notifyItemRangeChanged(0, 20, pos + 1);

               /* if (!alsAnswersList.get(qrva.GetPosition() - 1).equals("N/A")) {
                    if (direction.name().equals("Right")) {
                        if (aliSelectedQues.size() < 11) {
                            databaseWriteExecutor.execute(() -> qDao.InsertNewAnswer(new Questions(String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1))));
                            if (!aliSelectedQues.contains(qrva.GetPosition()))
                                aliSelectedQues.add(qrva.GetPosition());
                            firebaseMethods.Set5ChildrenValue(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()), alsAnswersList.get(qrva.GetPosition() - 1));
                        } else {
                            Toast.makeText(mContext, "You can only answer a maximum of 10 questions", Toast.LENGTH_SHORT).show();
                        }

                    } else if (direction.name().equals("Left")) {
                        if (ACIB_FAV_Q.getTag().equals("selected")) {
                            //
                            databaseWriteExecutor.execute(() -> qDao.RemoveAnswer(String.valueOf(qrva.GetPosition())));
                            aliSelectedQues.remove((Integer) qrva.GetPosition());
                            firebaseMethods.Remove5Value(mContext.getString(R.string.dbname_user_card), mAuth.getCurrentUser().getUid(), mContext.getString(R.string.field_questions), String.valueOf(qrva.GetPosition()), null);

                        }

                    }
                } else {
                    Toast.makeText(mContext, "Please give an answer to select this question.", Toast.LENGTH_SHORT).show();

                }
               */


            }

            @Override
            public void onCardRewound() {
                //Log.d(TAG, "onCardRewound: rewound");
            }

            @Override
            public void onCardCanceled() {

                //  Log.d(TAG, "onCardRewound: canceled");
            }

            @Override
            public void onCardAppeared(View view, int position) {

                //  Log.d(TAG, "onCardRewound: appered pos: " + position);
            }

            @Override
            public void onCardDisappeared(View view, int position) {

                // Log.d(TAG, "onCardRewound: disappered pos: " + position);
                sPos = position;

            }
        }) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };


        QuestionsRecyclerViewAdapter qrvaMain = new QuestionsRecyclerViewAdapter(alsQuesList, aliSelectedQues, alsAnswersList, ACIB_FAV_Q, 1, true, mContext, null, this);


        cardStackLayoutManager.setDirections(Direction.HORIZONTAL);
        cardStackLayoutManager.setStackFrom(StackFrom.Bottom);
        cardStackLayoutManager.setMaxDegree(45);

        cardStackLayoutManager.setVisibleCount(1);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        CSV_QUESTION_Q.setLayoutManager(cardStackLayoutManager);
        CSV_QUESTION_Q.setAdapter(qrvaMain);
//        RV_QUESTIONS_Q.setLayoutManager(swipeableLayoutManager);
//        RV_QUESTIONS_Q.setAdapter(qrvaMain);
//        qrvaMain.notifyDataSetChanged();
//        RV_QUESTIONS_Q.setNestedScrollingEnabled(true);
    }

    @Override
    public void onItemClick(int Position) {

        ET_Dialog.show();
//        if (Options.equals("N/A")) {
//
//
//            holder.ET.setVisibility(View.VISIBLE);
//            holder.TV_CHARACTER_NO.setVisibility(View.VISIBLE);
//            holder.ATV_OPT_1.setVisibility(View.GONE);
//            holder.ATV_OPT_2.setVisibility(View.GONE);
//            holder.ATV_OPT_3.setVisibility(View.GONE);
//            holder.ATV_OPT_4.setVisibility(View.GONE);
//            boolean isTyped = true;
//            Log.d(TAG, "onBindViewHolder: alsAnswerList pos:" + alsAnswerList.get(pos));
//            if (!alsAnswerList.get(pos).equals("N/A")) {
//                holder.ET.setText(alsAnswerList.get(pos));
//                isTyped = false;
//            } else {
//                holder.ET.setText("");
//            }
//            holder.TV_CHARACTER_NO.setText(holder.ET.length() + "/150");
//            boolean finalIsTyped = isTyped;
//            holder.ET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//        } else {
//
//            holder.ET.setVisibility(View.GONE);
//            holder.TV_CHARACTER_NO.setVisibility(View.GONE);
//            //  Log.d(TAG, "onBindViewHolder: Options 2: " + Options);
//
//
//        }
    }
}
