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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.Adapters.CoursesRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class CourseAndFullNameCardFragment extends Fragment implements CoursesRecyclerViewAdapter.OnCoursesClickListener {
    private static final String TAG = "CourseAndFullNameCardFragment";
    private AnimatedRecyclerView rCourses;
    private RecyclerView.Adapter mResultAdapter;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private CoursesRecyclerViewAdapter.OnCoursesClickListener onCoursesClickListener;

    private String sUsername, sUserDescription, CourseSelected, CourseSelectedNumber;
    private StringBuilder sbCollegeHeading1,sbCollegeHeading2,sbCollegeHeading3,sbCollegeHeading;
    private DataSnapshot MethodSnapshot;
    public static String sCardColor = String.valueOf(Color.WHITE);
    private boolean BEFORE_TEXT_COLLECTED = false;
    private Context mContext;
    private ArrayList<String> mCourseList;
    private ArrayList<Integer> mUsernameList;
    private ArrayList<String> CourseNameList;
    private HashMap<String, String> hmCourses;

    private TextView mUsername, mCourseNames;
    private TyperTextView mCollege_Heading;
    private EditText mFullName;
    private ImageButton mColorPicker;
    private RelativeLayout card_RL;
    //private LVCircularJump Loading_Courses;
    private TextView mNextCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_fullname_card, container, false);
        Log.d(TAG, "onCreateView: PrepareCardFragment");

        rCourses = view.findViewById(R.id.course_recycler_view_card);
        mUsername = view.findViewById(R.id.card_username);
        mFullName = view.findViewById(R.id.card_full_name);
        mColorPicker = view.findViewById(R.id.Card_colorPicker);
        card_RL = view.findViewById(R.id.RL_CardView_card);
        mCollege_Heading = view.findViewById(R.id.college_title_Card);
        mCourseNames = view.findViewById(R.id.ItemName);

        onCoursesClickListener = this;
        mNextCard = view.findViewById(R.id.next_card);
        sbCollegeHeading = new StringBuilder();
        sbCollegeHeading1 = new StringBuilder();
        sbCollegeHeading2 = new StringBuilder();
        sbCollegeHeading3 = new StringBuilder();
        sbCollegeHeading2.append("Hans Raj College");


        mCollege_Heading.setCharIncrease(1);
        mCollege_Heading.setTyperSpeed(2);
        mCollege_Heading.animateText("Hansraj College");
//        Loading_Courses = view.findViewById(R.id.Loading_Courses);
//
//        Loading_Courses.startAnim(700);


        mCourseList = new ArrayList<>();
        mUsernameList = new ArrayList<>();
        CourseNameList = new ArrayList<>();
        hmCourses = new HashMap<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseMethods = new FirebaseMethods(mContext);


        if (getArguments() != null) {
            sUsername = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username)));

        }

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

        BuildLists();
        mUsername.setText(sUsername);
        GetDetailsFromDatabase();
        getCoursesList();

        mNextCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mFullName.getText().toString())) {
                    mFullName.setError("Please enter your full name.");
                }
                if (/*CourseNameList.isEmpty() &&*/ TextUtils.isEmpty(CourseSelected)) {
                    Toast.makeText(mContext, "Please select the course that you are pursuing in Hansraj College", Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(mFullName.getText().toString()) /*&& (!CourseNameList.isEmpty()*/ && !TextUtils.isEmpty(CourseSelected)/*)*/) {
                    SetFullNameAndCourse();
                    Bundle bundle = new Bundle();
                    bundle.putString(mContext.getString(R.string.field_description), sUserDescription);
                    requireFragmentManager().beginTransaction().remove(CourseAndFullNameCardFragment.this).commit();
                    CardInformationFragment fragment = new CardInformationFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
                    Transaction.addToBackStack(mContext.getString(R.string.Card_information_fragment));
                    Transaction.commit();

                }
            }
        });


        mFullName.addTextChangedListener(new TextWatcher() {
            String beforeText = "";

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
                Log.d(TAG, "afterTextChanged: Course Selected +" + CourseSelected);
//                if(!TextUtils.isEmpty(CourseSelected)){
//                    sbCollegeHeading3.delete(0,sbCollegeHeading3.length());
//                    sbCollegeHeading3.append("pursuing ").append("<U>").append(CourseSelected).append("</U>.");
//                }
                if (!TextUtils.isEmpty(s.toString())) {

//                    mCollege_Heading.setText("");
                    //sUserDescription = "I am " + "<B>" + "<I>" + s.toString() + "</I>" + "</B>" + " from " + "<U>" + beforeText + "</U>";
                    sbCollegeHeading1.delete(0,sbCollegeHeading1.length());
                    sbCollegeHeading1.append("I am <B><I>").append(s.toString()).append("</I></B> from ");
                    BuildString();
                    mCollege_Heading.setText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    mCollege_Heading.animateText(sbCollegeHeading2.toString());
                }
            }
        });
        mColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog builder = new Dialog(mContext);
                builder.setContentView(R.layout.layout_color_picker);
                builder.show();

                final Button CardColor = builder.findViewById(R.id.close_dialog);
                Button TextColor = builder.findViewById(R.id.ACB_MAKE_GRADIENT);

                final ColorPickerView colorPickerView = builder.findViewById(R.id.CPV_COLOR_WHEEL);

                colorPickerView.setEnabledAlpha(true);
                colorPickerView.setEnabledBrightness(true);
                colorPickerView.subscribe(new ColorObserver() {
                    @Override
                    public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                        Log.d(TAG, "onColor: color  " + color);
                        sCardColor = String.valueOf(color);
                    }
                });

                CardColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Color " + sCardColor);
                        card_RL.setBackgroundColor(Integer.parseInt(sCardColor));
                        builder.dismiss();
                    }
                });
                TextColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: Color " + sCardColor);
///                        mResultAdapter.notifyDataSetChanged();
                        mCollege_Heading.setTextColor(Integer.parseInt(sCardColor));
                        mUsername.setTextColor(Integer.parseInt(sCardColor));
                        mFullName.setTextColor(Integer.parseInt(sCardColor));
                        mFullName.setHintTextColor(Integer.parseInt(sCardColor));
                        builder.dismiss();
                    }
                });
            }
        });


        return view;
    }
private void BuildString(){
        sbCollegeHeading.delete(0,sbCollegeHeading.length());
        sbCollegeHeading.append(sbCollegeHeading1.toString()).append(sbCollegeHeading2.toString()).append(sbCollegeHeading3.toString());
    }
    private void BuildLists() {
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
        for (int i = 0; i < mCourseList.size(); i++) {
            hmCourses.put(mCourseList.get(i), "course_name_" + (i + 1));
        }
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void TwoLineQueryDatabase(final Runnable runnable, String Field1, String Field2) {


        Query query = myRef.child(Field1)
                .child(Field2);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MethodSnapshot = snapshot;
                runnable.run();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetDetailsFromDatabase() {

        Runnable FullNameRunnable = new Runnable() {
            @Override
            public void run() {
                for (DataSnapshot snapshot : MethodSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.getKey(), mContext.getString(R.string.field_display_name))) {
                        if (!TextUtils.isEmpty(snapshot.getValue().toString())) {
                            mFullName.setText(snapshot.getValue().toString());
                        }
                    }
                    if (Objects.equals(snapshot.getKey(), mContext.getString(R.string.field_description))) {
                        mCollege_Heading.setText(snapshot.getValue().toString());
                    }
                }
            }
        };
        Runnable CourseRunnable = new Runnable() {
            @Override
            public void run() {
                for (DataSnapshot snapshot : MethodSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.getKey(), mContext.getString(R.string.field_course))) {
                        CourseSelected = snapshot.getValue().toString();
                        CourseSelectedNumber = hmCourses.get(CourseSelected);
                        sbCollegeHeading3.delete(0,sbCollegeHeading3.length());
                        sbCollegeHeading3.append(" pursuing <U>").append(CourseSelected).append("</U>.");
                        if (!hmCourses.isEmpty() && !TextUtils.isEmpty(CourseSelected)) {
                            myRef.child(mContext.getString(R.string.dbname_filter_data))
                                    .child(mContext.getString(R.string.field_course))
                                    .child(hmCourses.get(CourseSelected))
                                    .child("members")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .removeValue();//CoursesRecyclerViewAdapter.CourseNameList.add(snapshot.getValue().toString());
                        }
                    }
                    if (Objects.equals(snapshot.getKey(), mContext.getString(R.string.field_username))) {
                        mUsername.setText(snapshot.getValue().toString());//CoursesRecyclerViewAdapter.CourseNameList.add(snapshot.getValue().toString());
                    }
                }
            }
        };


        TwoLineQueryDatabase(CourseRunnable, mContext.getString(R.string.dbname_users)
                , mAuth.getCurrentUser().getUid());
        TwoLineQueryDatabase(FullNameRunnable, mContext.getString(R.string.dbname_user_account_settings)
                , mAuth.getCurrentUser().getUid());
    }

    private void SetFullNameAndCourse() {
        if (/*!CourseNameList.isEmpty() */!TextUtils.isEmpty(CourseSelected)) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field_course))
                    /*.setValue(CourseNameList.get(0));*/
                    .setValue(CourseSelected);
//            if (!CourseSelected.equals(CourseNameList.get(0))) {
//                String UID = myRef.push().getKey();
//                myRef.child(mContext.getString(R.string.dbname_filter_data))
//                        .child(mContext.getString(R.string.field_course))
//                        .child(UID)
//                        .child(mContext.getString(R.string.course))
//                        .setValue(CourseNameList.get(0));
            Log.d(TAG, "SetFullNameAndCourse: CourseSelected Number: " + CourseSelectedNumber);
            myRef.child(mContext.getString(R.string.dbname_filter_data))
                    .child(mContext.getString(R.string.field_course))
                    .child(CourseSelectedNumber)
                    .child("members")
                    .child(mAuth.getCurrentUser().getUid())
                    .setValue(mContext.getString(R.string.field_user_id));

//                myRef.child(mContext.getString(R.string.dbname_filter_data))
//                        .child(mContext.getString(R.string.field_course))
//                        .child(CourseSelected)
//                        .child(mAuth.getCurrentUser().getUid())
//                        .removeValue();
            //}
        }
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_display_name))
                .setValue(mFullName.getText().toString());
        myRef.child(mContext.getString(R.string.dbname_user_common_details))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_display_name))
                .setValue(mFullName.getText().toString());
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_description))
                .setValue(sUserDescription);
//        myRef.child(mContext.getString(R.string.dbname_user_card))
//                .child(mAuth.getCurrentUser().getUid())
//                .child(mContext.getString(R.string.field_card_bg_color))
//                .setValue(card_RL.getSolidColor());
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_card_bg_color))
                .setValue(card_RL.getSolidColor());

    }


    private void getCoursesList() {


        Query query = myRef.child(requireActivity().getString(R.string.dbname_misc))
                .child(mContext.getString(R.string.courses_hansraj));
        Log.d(TAG, "getCoursesList: enetred query");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: enteres On data change");


                if (!snapshot.exists()) {

                    for (int i = 0; i < mCourseList.size(); i++) {

                        Log.d(TAG, String.format("onDataChange: mCourseList for loop %s", mCourseList));
//                        count++;
                        myRef.child(requireActivity().getString(R.string.dbname_misc))
                                .child(mContext.getString(R.string.courses_hansraj))
                                .child("course_name_" + (i + 1))
                                .setValue(mCourseList.get(i));
                        myRef.child(requireActivity().getString(R.string.dbname_filter_data))
                                .child(mContext.getString(R.string.field_course))
                                .child("course_name_" + (i + 1))
                                .child(mContext.getString(R.string.field_course))
                                .setValue(mCourseList.get(i));

                    }
                }
//                else {
//                    mCourseList.clear();
//
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//
//                        mCourseList.add(requireNonNull(dataSnapshot.getValue()).toString());
//
//                    }
//                    //  Log.d(TAG, new StringBuilder().append("onDataChange: mCourseList ").append(mCourseList).toString());
//                }


//                Handler handler = new Handler(Looper.getMainLooper());
//
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                Log.d(TAG, String.format("run: mCourseList %s", mCourseList));
                mResultAdapter = new CoursesRecyclerViewAdapter(CourseSelected, mCourseList, getActivity(), sCardColor, mCollege_Heading, mFullName, onCoursesClickListener);
                rCourses.setItemAnimator(new DefaultItemAnimator());
                rCourses.setAdapter(mResultAdapter);
                mResultAdapter.notifyDataSetChanged();
                rCourses.scheduleLayoutAnimation();
//                Loading_Courses.stopAnim();
//                Loading_Courses.setVisibility(View.GONE);
                // mRecyclerView.smoothScrollToPosition(mChatList.size());
//                    }
//                }, 1000);
//                mRecyclerView.smoothScrollToPosition(mChatList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onItemClick(int position, ImageView TickMe) {
        if (position != 0) {
           /* CourseNameList.clear();
            CourseNameList.add(mCourseList.get(position - 1));*/
            if (TextUtils.isEmpty(CourseSelected)) {
                CourseSelected = mCourseList.get(position - 1);
                CourseSelectedNumber = hmCourses.get(CourseSelected);
                TickMe.setVisibility(View.VISIBLE);
                final Dialog Dialog = new Dialog(mContext);
                requireNonNull(Dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                final TextView CourseSelectedTV = new TextView(mContext);
                CourseSelectedTV.setTextColor(Color.BLACK);
                CourseSelectedTV.setPadding(10, 10, 10, 10);
                CourseSelectedTV.setTypeface(null, Typeface.BOLD);
                CourseSelectedTV.setBackgroundColor(Color.parseColor("#FFFFFF"));
                CourseSelectedTV.setTextSize(20);
                CourseSelectedTV.setText("Awesome! " + /*CourseNameList.get(0)*/ CourseSelected + " is a great choice.");
                Dialog.setContentView(CourseSelectedTV);
                Dialog.show();
                android.os.Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialog.dismiss();
                    }
                }, 3000);
//                int startName = mCollege_Heading.getText().toString().indexOf(mFullName.getText().toString());
//                int EndName = startName + mFullName.getText().length();
//                String BeforeFullName = mCollege_Heading.getText().toString().substring(0, startName);
//                String FullName = mCollege_Heading.getText().toString().substring(startName, EndName);

                sbCollegeHeading3.delete(0,sbCollegeHeading3.length());
                sbCollegeHeading3.append(" pursuing <U>").append(CourseSelected).append("</U>.");
//
                BuildString();
                mCollege_Heading.animateText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                Log.d(TAG, String.format("onClick: Course Selected %s", CourseSelected));
            } else {
                if (TickMe.getVisibility() == View.VISIBLE) {
                    CourseSelected = "";
                    CourseSelectedNumber = "";
                    TickMe.setVisibility(View.GONE);
                    sbCollegeHeading3.delete(0,sbCollegeHeading3.length());
                    BuildString();
                    mCollege_Heading.animateText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    Toast.makeText(mContext, "Please unselected the previously selected course!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
