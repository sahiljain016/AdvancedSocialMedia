package com.gic.memorableplaces.SignUp;

import static java.util.Objects.requireNonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.gic.memorableplaces.Adapters.CoursesRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseAndFullNameCardFragment extends Fragment implements CoursesRecyclerViewAdapter.OnCoursesClickListener {
    private static final String TAG = "CourseAndFullNameCardFragment";

    private AnimatedRecyclerView arvCourses;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private CoursesRecyclerViewAdapter.OnCoursesClickListener onCoursesClickListener;

    private String sUsername, sFullName, sAutoDesp, sCourseName, CourseSelectedNumber;
    private boolean isAutoDespEnabled;
    private StringBuilder sbCollegeHeading1, sbCollegeHeading2, sbCollegeHeading3, sbCollegeHeading;

    private Context mContext;
    private ArrayList<String> mCourseList;

    private TyperTextView TTV_AUTO_DESP;
    private EditText ET_FULLNAME;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_full_name, container, false);
        Log.d(TAG, "onCreateView: PrepareCardFragment");

        arvCourses = view.findViewById(R.id.ARV_COURSES_CFN);
        TextView TV_USERNAME = view.findViewById(R.id.ATV_USERNAME_CFN);
        ET_FULLNAME = view.findViewById(R.id.ET_FULLNAME_CFN);
        TTV_AUTO_DESP = view.findViewById(R.id.TTV_AUTO_DESP_CFN);
        //private LVCircularJump Loading_Courses;
        RelativeLayout RL_BACK_BUTTON = view.findViewById(R.id.RL_BACK_BUTTON_CFN);
        RelativeLayout RL_NEXT_BUTTON = view.findViewById(R.id.RL_NEXT_BUTTON_CFN);

        onCoursesClickListener = this;

        sbCollegeHeading = new StringBuilder();
        sbCollegeHeading1 = new StringBuilder();
        sbCollegeHeading2 = new StringBuilder();
        sbCollegeHeading3 = new StringBuilder();
        sbCollegeHeading2.append("Hans Raj College");


        TTV_AUTO_DESP.setCharIncrease(1);
        TTV_AUTO_DESP.setTyperSpeed(2);
        TTV_AUTO_DESP.animateText("Courses");
//        Loading_Courses = view.findViewById(R.id.Loading_Courses);
//
//        Loading_Courses.startAnim(700);


        mCourseList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        if (getArguments() != null) {
            sUsername = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username)));
            sFullName = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_display_name)));
            sAutoDesp = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_description)));
            sCourseName = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_course)));
            isAutoDespEnabled = getArguments().getBoolean("auto_desp");
        }


        TV_USERNAME.setText(sUsername);
        ET_FULLNAME.setText(sFullName);
        if (!TextUtils.isEmpty(sAutoDesp))
            TTV_AUTO_DESP.animateText(sAutoDesp);

        Query query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_page_number));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().toString().equals(mContext.getString(R.string.card_completed)) || !snapshot.exists()) {
                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field_page_number))
                            .setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.d(TAG, "onCreateView: UserID " + sUsername);
        SetCourseList();

        RL_NEXT_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ET_FULLNAME.getText().toString())) {
                    ET_FULLNAME.setError("Please enter your full name.");
                }
                if (/*CourseNameList.isEmpty() &&*/ TextUtils.isEmpty(sCourseName)) {
                    Toast.makeText(mContext, "Please select the course that you are pursuing in Hansraj College", Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(ET_FULLNAME.getText().toString()) && !TextUtils.isEmpty(sCourseName)/*)*/) {
                    SetFullNameAndCourse();
                    Bundle bundle = new Bundle();
                    bundle.putString(mContext.getString(R.string.field_description), sAutoDesp);
                    requireFragmentManager().beginTransaction().remove(CourseAndFullNameCardFragment.this).commit();
                    FillDetailsFragment fragment = new FillDetailsFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Transaction.replace(R.id.FrameLayoutCard, fragment);
                    Transaction.addToBackStack(mContext.getString(R.string.Card_information_fragment));
                    Transaction.commit();

                }
            }
        });

        RL_BACK_BUTTON.setOnClickListener(v -> {
            getParentFragmentManager().popBackStackImmediate(mContext.getString(R.string.course_and_full_name_Card_fragment), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });


        if (isAutoDespEnabled) {
            ET_FULLNAME.addTextChangedListener(new TextWatcher() {
                final String beforeText = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Log.d(TAG, "afterTextChanged: snfdnfid");
                    //int start = mCollege_Heading.getText().toString().indexOf("Hansraj");
                    //beforeText = mCollege_Heading.getText().toString().substring(start, mCollege_Heading.getText().length());
                    //Log.d(TAG, "afterTextChanged: start "  + start);
                    Log.d(TAG, "afterTextChanged: Course Selected +" + sCourseName);
//                if(!TextUtils.isEmpty(CourseSelected)){
//                    sbCollegeHeading3.delete(0,sbCollegeHeading3.length());
//                    sbCollegeHeading3.append("pursuing ").append("<U>").append(CourseSelected).append("</U>.");
//                }
                    if (!TextUtils.isEmpty(s.toString())) {

//                    mCollege_Heading.setText("");
                        //sUserDescription = "I am " + "<B>" + "<I>" + s.toString() + "</I>" + "</B>" + " from " + "<U>" + beforeText + "</U>";
                        sbCollegeHeading1.delete(0, sbCollegeHeading1.length());
                        sbCollegeHeading1.append("I am <B><I>").append(s.toString()).append("</I></B> from ");
                        BuildString();
                        TTV_AUTO_DESP.setText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        TTV_AUTO_DESP.animateText(sbCollegeHeading2.toString());
                    }
                }

            });
        }

        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void BuildString() {
        sbCollegeHeading.delete(0, sbCollegeHeading.length());
        sbCollegeHeading.append(sbCollegeHeading1.toString()).append(sbCollegeHeading2.toString()).append(sbCollegeHeading3.toString());
    }


    private void SetFullNameAndCourse() {
        if (!TextUtils.isEmpty(sCourseName)) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field_course))
                    .setValue(sCourseName);

            Log.d(TAG, "SetFullNameAndCourse: CourseSelected Number: " + CourseSelectedNumber);
            if (!TextUtils.isEmpty(CourseSelectedNumber)) {
                myRef.child(mContext.getString(R.string.dbname_filter_data))
                        .child(mContext.getString(R.string.field_course))
                        .child(CourseSelectedNumber)
                        .child("members")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(mContext.getString(R.string.field_user_id));
            }


        }

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_display_name))
                .setValue(ET_FULLNAME.getText().toString());
        myRef.child(mContext.getString(R.string.dbname_user_common_details))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_display_name))
                .setValue(ET_FULLNAME.getText().toString());
        if (isAutoDespEnabled) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field_card_bio))
                    .setValue(sbCollegeHeading.toString());
        }

    }


    private void SetCourseList() {
        mCourseList.clear();
        mCourseList.add("B.A. (Hons.) Economics");
        mCourseList.add("B.A. (Hons.) English");
        mCourseList.add("B.A. (Hons.) Hindi");
        mCourseList.add("B.A. (Hons.) History");
        mCourseList.add("B.A. (Hons.) Philosophy");
        mCourseList.add("B.A. (Hons.) Sanskrit");
        mCourseList.add("B.Com (Hons.)");
        mCourseList.add("B.Sc (Hons.) Anthropology");
        mCourseList.add("B.Sc (Hons.) Botany");
        mCourseList.add("B.Sc (Hons.) Chemistry");
        mCourseList.add("B.Sc (Hons.) Computer Science");
        mCourseList.add("B.Sc (Hons.) Electronics");
        mCourseList.add("B.Sc (Hons.) Geology");
        mCourseList.add("B.Sc (Hons.) Mathematics");
        mCourseList.add("B.Sc (Hons.) Physics");
        mCourseList.add("B.Sc (Hons.) Zoology");
        mCourseList.add("B.Sc (Prog.) Life Science");
        mCourseList.add("B.Sc (Prog.) Physical Science with Chemistry");
        mCourseList.add("B.Sc (Prog.) Physical Science with Computer Science");
        mCourseList.add("B.A Programme (Sanskrit + History)");
        mCourseList.add("B.A Programme (Commerce + Economics)");
        mCourseList.add("B.A Programme (Economics + History)");
        mCourseList.add("B.A Programme (History + Philosophy)");
        mCourseList.add("B.A Programme (Physical Education + History)");

        Log.d(TAG, String.format("run: mCourseList %s", mCourseList));
        CoursesRecyclerViewAdapter aCourses = new CoursesRecyclerViewAdapter(sCourseName, mCourseList, getActivity(), onCoursesClickListener);
        arvCourses.setItemAnimator(new DefaultItemAnimator());
        arvCourses.setAdapter(aCourses);
        aCourses.notifyItemRangeInserted(0, mCourseList.size());
        arvCourses.scheduleLayoutAnimation();


    }

    @Override
    public void onItemClick(int position, ImageView TickMe) {
        if (position != 0) {
           /* CourseNameList.clear();
            CourseNameList.add(mCourseList.get(position - 1));*/
            if (TextUtils.isEmpty(sCourseName)) {
                sCourseName = mCourseList.get(position - 1);
                CourseSelectedNumber = "course_" + (position + 1);
                TickMe.setVisibility(View.VISIBLE);
                final Dialog Dialog = new Dialog(mContext);
                requireNonNull(Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                final TextView CourseSelectedTV = new TextView(mContext);
                CourseSelectedTV.setTextColor(Color.BLACK);
                CourseSelectedTV.setPadding(10, 10, 10, 10);
                CourseSelectedTV.setTypeface(null, Typeface.BOLD);
                CourseSelectedTV.setBackgroundColor(Color.parseColor("#FFFFFF"));
                CourseSelectedTV.setTextSize(20);
                CourseSelectedTV.setText("Awesome! " + /*CourseNameList.get(0)*/ sCourseName + " is a great choice.");
                Dialog.setContentView(CourseSelectedTV);
                Dialog.show();
                android.os.Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(Dialog::dismiss, 3000);
//                int startName = mCollege_Heading.getText().toString().indexOf(mFullName.getText().toString());
//                int EndName = startName + mFullName.getText().length();
//                String BeforeFullName = mCollege_Heading.getText().toString().substring(0, startName);
//                String FullName = mCollege_Heading.getText().toString().substring(startName, EndName);

                if (isAutoDespEnabled) {
                    sbCollegeHeading3.delete(0, sbCollegeHeading3.length());
                    sbCollegeHeading3.append(" pursuing <U>").append(sCourseName).append("</U>.");
//
                    BuildString();
                    TTV_AUTO_DESP.animateText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                }
                Log.d(TAG, String.format("onClick: Course Selected %s", sCourseName));
            } else {
                if (TickMe.getVisibility() == View.VISIBLE) {
                    sCourseName = "";
                    CourseSelectedNumber = "";
                    TickMe.setVisibility(View.GONE);
                    if (isAutoDespEnabled) {
                        sbCollegeHeading3.delete(0, sbCollegeHeading3.length());
                        BuildString();
                        TTV_AUTO_DESP.animateText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                    }
                } else {
                    Toast.makeText(mContext, "Please unselected the previously selected course!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
