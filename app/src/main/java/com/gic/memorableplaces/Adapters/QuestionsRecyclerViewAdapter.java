package com.gic.memorableplaces.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.ArrayList;
import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";

    //Variables
    private final OnNumberSelected myOnNumberSelected;
    private final OnAnswerChanged myOnAnswerChanged;
    private final Context mContext;
    private final ArrayList<String> alsQuesList, alsAnswerList;
    private final ArrayList<Integer> aliSelectedQues;
    private final boolean isQues;
    private Integer CurrentItem;
    private final AppCompatImageButton ACIB_FAV;
    private int Position;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnNumberSelected mOnNumberSelected;
        OnAnswerChanged mOnAnswerChanged;
        public TextView TV_QUES, TV_CHARACTER_NO, TV_QUES_NUMBER, TV_1;
  //      public AutofitTextView ATV_OPT_1, ATV_OPT_2, ATV_OPT_3, ATV_OPT_4;
        public RoundedProgressBar RPB;
        public CardView CV_BG;
        //public ScrollView SV;
        // public EditText ET;
        public ConstraintLayout CL;

        public MainFeedViewHolder(@NonNull View itemView, boolean isQues, OnNumberSelected OnNumberSelected, OnAnswerChanged onAnswerChanged) {
            super(itemView);
            if (isQues) {
                TV_QUES = itemView.findViewById(R.id.TV_QUES_Q);
                mOnAnswerChanged = onAnswerChanged;
                TV_CHARACTER_NO = itemView.findViewById(R.id.TV_CHARACTER_NO_Q);
//                ATV_OPT_1 = itemView.findViewById(R.id.ATV_OPTION_1_Q);
//                ATV_OPT_2 = itemView.findViewById(R.id.ATV_OPTION_2_Q);
//                ATV_OPT_3 = itemView.findViewById(R.id.ATV_OPTION_3_Q);
//                ATV_OPT_4 = itemView.findViewById(R.id.ATV_OPTION_4_Q);
                //ET = itemView.findViewById(R.id.ET_ANSWER_Q);
                CL = itemView.findViewById(R.id.CL_ANSWER_Q);
                TV_QUES_NUMBER = itemView.findViewById(R.id.TV_QUES_NUMBER_Q);
                RPB = itemView.findViewById(R.id.RPB_PROGRESS_Q);
                CV_BG = itemView.findViewById(R.id.CV_BG_Q);
               // SV = itemView.findViewById(R.id.SV_ANSWER_Q);
                //ET.setOnClickListener(this);
                CL.setOnClickListener(this);
            } else {
                mOnNumberSelected = OnNumberSelected;
                TV_1 = itemView.findViewById(R.id.TV_1);
            }

        }


        @Override
        public void onClick(View v) {
            mOnAnswerChanged.onItemClick(getBindingAdapterPosition());
        }
    }

    public interface OnNumberSelected {
        void onItemClick(int SelectedPos, int Position, TextView TV);
    }

    public interface OnAnswerChanged {
        void onItemClick(int Position);
    }

    public QuestionsRecyclerViewAdapter(ArrayList<String> QuesList, ArrayList<Integer> SelectedList, ArrayList<String> AnswerList,
                                        AppCompatImageButton ACIB, Integer CurrentItem, boolean isQues, Context context,
                                        OnNumberSelected onNumberSelected, OnAnswerChanged onAnswerChanged) {
        alsQuesList = QuesList;
        aliSelectedQues = SelectedList;
        alsAnswerList = AnswerList;
        this.CurrentItem = CurrentItem;
        ACIB_FAV = ACIB;
        mContext = context;
        myOnNumberSelected = onNumberSelected;
        myOnAnswerChanged = onAnswerChanged;
        this.isQues = isQues;


    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (isQues)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_questions, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_textview, parent, false);
        return new MainFeedViewHolder(v, isQues, myOnNumberSelected, myOnAnswerChanged);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MainFeedViewHolder holder, int position) {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull List<Object> payloads) {


        int pos = holder.getBindingAdapterPosition();
        //  Log.d(TAG, "onBindViewHolder: binder pos: " + pos);
        Position = pos + 1;
        if (isQues) {
            String Ques = alsQuesList.get(pos).substring((alsQuesList.get(pos).indexOf("*") + 1), alsQuesList.get(pos).indexOf("-o"));
            String Options = alsQuesList.get(pos).substring(alsQuesList.get(pos).indexOf("-o") + 2);

            // Log.d(TAG, "onBindViewHolder: Options: " + Options);

            holder.TV_QUES.setText(Ques);
            double SinglePerc = 100.00 / alsQuesList.size();
            //holder.RPB.setProgressPercentage(SinglePerc * Position, true);
            holder.RPB.setProgressPercentage(SinglePerc * (pos + 1), true);
            //  holder.TV_QUES_NUMBER.setText("Question " + String.valueOf(Position) + " of " + alsQuesList.size());
            holder.TV_QUES_NUMBER.setText("Question " + String.valueOf((pos + 1)) + " of " + alsQuesList.size());


            // Log.d(TAG, "onBindViewHolder: pos: " + pos);

        } else {
            if (!payloads.isEmpty()) {
                if (payloads.get(0) instanceof Integer) {
                    CurrentItem = (Integer) payloads.get(0);
                }
            } else {
                super.onBindViewHolder(holder, position, payloads);
            }
            holder.TV_1.setText(String.valueOf(pos + 1));


            if (Integer.parseInt(holder.TV_1.getText().toString()) == CurrentItem) {
                holder.TV_1.setTextColor(Color.parseColor("#FFFFFF"));
            } else {

                holder.TV_1.setTextColor(Color.parseColor("#D1D1D1"));
            }
            holder.TV_1.setOnClickListener(v -> {
                holder.mOnNumberSelected.onItemClick(Integer.parseInt(holder.TV_1.getText().toString()), pos, holder.TV_1);
            });
        }
    }


    public int GetFixedPosition() {
        return Position;
    }

    public int GetPosition() {
        return CurrentItem;

    }

    @Override
    public int getItemCount() {
        return alsQuesList.size();
    }

}

