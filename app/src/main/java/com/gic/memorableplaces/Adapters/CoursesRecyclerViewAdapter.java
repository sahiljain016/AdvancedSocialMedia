package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;

import java.util.ArrayList;

public class CoursesRecyclerViewAdapter extends RecyclerView.Adapter<CoursesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "CoursesRecyclerViewAdapter";

    //Variables
    private Context mContext;
    private final String sCourseSelected;

    private ArrayList<String> alsCourseList;

    private OnCoursesClickListener MyOnCoursesClickListener;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnCoursesClickListener mOnCoursesClickListener;
        public TextView TV_COURSE;
        public ConstraintLayout CL_SC;
        public ImageView IV_SELECT_ICON;

        public MainFeedViewHolder(@NonNull View itemView, OnCoursesClickListener onCoursesClickListener) {
            super(itemView);
            mOnCoursesClickListener = onCoursesClickListener;
            TV_COURSE = itemView.findViewById(R.id.TV_COURSE_NAME);
            CL_SC = itemView.findViewById(R.id.CL_select_course);
            IV_SELECT_ICON = itemView.findViewById(R.id.IV_SELECTED_COURSE_ICON);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnCoursesClickListener.onItemClick(getAdapterPosition(), IV_SELECT_ICON);
        }
    }

    public interface OnCoursesClickListener {
        void onItemClick(int position, ImageView TickMe);
    }

    public CoursesRecyclerViewAdapter(String CourseSelect, ArrayList<String> CourseList, Context context,
                                      OnCoursesClickListener onCoursesClickListener) {
        alsCourseList = CourseList;
        mContext = context;
        sCourseSelected = CourseSelect;
        MyOnCoursesClickListener = onCoursesClickListener;
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_course_card, parent, false);
        return new MainFeedViewHolder(v, MyOnCoursesClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        int pos = holder.getBindingAdapterPosition();
        if (pos == 0) {
            holder.TV_COURSE.setText("Select your Course..");
            holder.TV_COURSE.setTextSize(24);
            holder.CL_SC.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.TV_COURSE.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            holder.TV_COURSE.setTextSize(18);
            if ((position % 2) == 0) {
                holder.CL_SC.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.CL_SC.setBackgroundColor(Color.parseColor("#CBCBCB"));
            }
            holder.TV_COURSE.setText(position + ".  " + alsCourseList.get(position - 1));
            if (!TextUtils.isEmpty(sCourseSelected)) {
                if (alsCourseList.get(position - 1).equals(sCourseSelected)) {
                    holder.IV_SELECT_ICON.setVisibility(View.VISIBLE);
                }else{
                    holder.IV_SELECT_ICON.setVisibility(View.GONE);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return (alsCourseList.size() + 1);
    }


}

