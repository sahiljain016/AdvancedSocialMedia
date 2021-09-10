package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.R;

import java.util.ArrayList;

public class CoursesRecyclerViewAdapter extends RecyclerView.Adapter<CoursesRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "CoursesRecyclerViewAdapter";
    private final String mCourseSelected;
    private int ITEM_POSITION_TITLE = 1;
    private int ITEM_POSITION_CONTENTS = 2;

    //Variables
    private ArrayList<String> mCourseList;
    public static ArrayList<String> CourseNameList;
    private ArrayList<Integer> CourseNumbers = new ArrayList<>();
    private Context mContext;
    private String mSelectedColor = String.valueOf(Color.WHITE), CourseName;
    private boolean isCourseSelected;
    private TyperTextView mCollegeHeading;
    private EditText mFullName;
    private OnCoursesClickListener MyOnCoursesClickListener;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnCoursesClickListener mOnCoursesClickListener;
        public TextView mCourseName;
        public ConstraintLayout constraintLayout;
        public ImageView TickMe;

        public MainFeedViewHolder(@NonNull View itemView, OnCoursesClickListener onCoursesClickListener) {
            super(itemView);
            mOnCoursesClickListener = onCoursesClickListener;
            mCourseName = itemView.findViewById(R.id.ItemName);
            constraintLayout = itemView.findViewById(R.id.CL_select_course);
            TickMe = itemView.findViewById(R.id.iv_Tick_Courses);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnCoursesClickListener.onItemClick(getAdapterPosition(), TickMe);
        }
    }

    public interface OnCoursesClickListener {
        void onItemClick(int position, ImageView TickMe);
    }

    public CoursesRecyclerViewAdapter(String CourseSelect, ArrayList<String> CourseList, Context context,
                                      String SelectedColor, TyperTextView typerTextView, EditText FullName, OnCoursesClickListener onCoursesClickListener) {
        mCourseList = CourseList;
        mContext = context;
        mCollegeHeading = typerTextView;
        mFullName = FullName;
        mCourseSelected = CourseSelect;
        mSelectedColor = SelectedColor;
        CourseNameList = new ArrayList<>();
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
        holder.setIsRecyclable(false);

        if (holder.getItemViewType() == ITEM_POSITION_TITLE) {
            holder.mCourseName.setText("Select your Course..");
            holder.mCourseName.setTextSize(24);
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.mCourseName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            if ((position % 2) == 0) {
                holder.constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.constraintLayout.setBackgroundColor(Color.parseColor("#75D5CFCF"));
            }
            holder.mCourseName.setText(position + ".  " + mCourseList.get(position - 1));
            if (!TextUtils.isEmpty(mCourseSelected)) {
                if (mCourseList.get(position - 1).equals(mCourseSelected)) {
                    holder.TickMe.setVisibility(View.VISIBLE);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return (mCourseList.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return ITEM_POSITION_TITLE;
        } else {
            return ITEM_POSITION_CONTENTS;
        }
    }
}

