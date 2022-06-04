package com.gic.memorableplaces.SignUp;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.gic.memorableplaces.Adapters.CoursesRecyclerViewAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.Home.GetStartedFragment;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.UserDetailsDatabase;
import com.gic.memorableplaces.interfaces.UserDetailsDao;
import com.gic.memorableplaces.utils.MiscTools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import me.grantland.widget.AutofitTextView;

public class CourseAndFullNameCardFragment extends Fragment implements CoursesRecyclerViewAdapter.OnCoursesClickListener {
    private static final String TAG = "CourseAndFullNameCardFragment";
    private int REQUEST_LOCATION = 99;

    private AnimatedRecyclerView arvCourses;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String provider;

    private CoursesRecyclerViewAdapter.OnCoursesClickListener onCoursesClickListener;

    private String CourseSelectedNumber;
    private StringBuilder sbCollegeHeading1, sbCollegeHeading2, sbCollegeHeading3, sbCollegeHeading;

    private Context mContext;
    private ArrayList<String> mCourseList;
    private Dialog dialog;
    private MyLocationListener mylistener;

    private User user;
    private UserDetailsDatabase UD_DETAILS;
    private UserDetailsDao userDetailsDao;
    private ExecutorService databaseWriteExecutor;

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
        user = new User();
        UD_DETAILS = UserDetailsDatabase.getDatabase(mContext);
        databaseWriteExecutor = UD_DETAILS.databaseWriteExecutor;
        userDetailsDao = UD_DETAILS.userDetailsDao();

        RelativeLayout RL_BACK_BUTTON = view.findViewById(R.id.RL_BACK_BUTTON_CFN);
        RelativeLayout RL_NEXT_BUTTON = view.findViewById(R.id.RL_NEXT_BUTTON_CFN);

        onCoursesClickListener = this;

        sbCollegeHeading = new StringBuilder();
        sbCollegeHeading1 = new StringBuilder();
        sbCollegeHeading2 = new StringBuilder();
        sbCollegeHeading3 = new StringBuilder();
        sbCollegeHeading2.append("Hans Raj College");

        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_GS);
        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/Abril_fatface.ttf");
        ATV_TITLE.setTypeface(tf, Typeface.NORMAL);

        mCourseList = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        Handler handler = new Handler();
        databaseWriteExecutor.execute(() -> {
            user = userDetailsDao.GetAllDetails(mAuth.getCurrentUser().getUid()).get(0);
            handler.post(() -> {
                Log.d(TAG, "onCreateView: user: " + user.getAuto_desp());
                if (user.getAuto_desp().equals("N/A") || TextUtils.isEmpty(user.getAuto_desp())) {
                    TTV_AUTO_DESP.setCharIncrease(1);
                    TTV_AUTO_DESP.setTyperSpeed(2);
                    TTV_AUTO_DESP.animateText("Courses");
                } else {
                    TTV_AUTO_DESP.setCharIncrease(1);
                    TTV_AUTO_DESP.setTyperSpeed(2);
                    TTV_AUTO_DESP.animateText(Html.fromHtml(user.getAuto_desp(), Html.FROM_HTML_MODE_LEGACY));
                }
                if (!user.getDisplay_name().equals("N/A") || !TextUtils.isEmpty(user.getDisplay_name())) {
                    ET_FULLNAME.setText(user.getDisplay_name());
                }
                if (!user.getUsername().equals("N/A") || !TextUtils.isEmpty(user.getUsername())) {
                    TV_USERNAME.setText(user.getUsername());
                }
                SetCourseList();
                if (user.isAutoDespEnabled()) {
                    ET_FULLNAME.addTextChangedListener(new TextWatcher() {
                        final String beforeText = "";

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Log.d(TAG, "afterTextChanged: Course Selected +" + sCourseName);
                            if (!TextUtils.isEmpty(s.toString())) {
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
            });

        });



        RL_NEXT_BUTTON.setOnClickListener(v -> {
            if (TextUtils.isEmpty(ET_FULLNAME.getText().toString()) || ET_FULLNAME.getText().toString().equals("N/A")) {
                ET_FULLNAME.setError("Please enter your full name.");
            }
            if (/*CourseNameList.isEmpty() &&*/ TextUtils.isEmpty(user.getCourse()) || user.getCourse().equals("N/A")) {
                Toast.makeText(mContext, "Please select the course that you are pursuing in Hansraj College", Toast.LENGTH_SHORT).show();
            }
            if (!TextUtils.isEmpty(ET_FULLNAME.getText().toString()) && !user.getCourse().equals("N/A") && !ET_FULLNAME.getText().toString().equals("N/A") && !TextUtils.isEmpty(user.getCourse())/*)*/) {
                double latitude = Double.parseDouble(user.getLocation().substring(0, user.getLocation().indexOf(",")));
                double longitude = Double.parseDouble(user.getLocation().substring(user.getLocation().indexOf(",") + 1));

                if (latitude == 0 && longitude == 0) {
                    if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]
                                {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                    }
                    dialog = MiscTools.InflateDialog(mContext, R.layout.dialog_prov_loc);
                    AppCompatButton ACB_LOC = dialog.findViewById(R.id.ACB_LOC_D);

                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    ACB_LOC.setOnClickListener(v1 -> {
                        GetLocation();
                    });
                } else {
                    SetFullNameCourseLoc();

                    user.setDisplay_name(ET_FULLNAME.getText().toString());
                    Log.d(TAG, "onCreateView: sCourseName: " + user.getCourse());
                    Log.d(TAG, "onCreateView: sFullName: " + user.getDisplay_name());
                    Log.d(TAG, "onCreateView: sAutoDesp: " + user.getAuto_desp());
                    databaseWriteExecutor.execute(() -> {
                        userDetailsDao.UpdateCourseDespFullNameLoc(user.getCourse(), user.getAuto_desp(), user.getDisplay_name(), user.getLocation(), mAuth.getCurrentUser().getUid());
                        requireFragmentManager().beginTransaction().remove(CourseAndFullNameCardFragment.this).commit();
                        FillDetailsFragment fragment = new FillDetailsFragment();
                        FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        Transaction.replace(R.id.FrameLayoutCard, fragment);
                        Transaction.addToBackStack(mContext.getString(R.string.fragment_fill_details));
                        Transaction.commit();
                    });
                }

            }
        });

        RL_BACK_BUTTON.setOnClickListener(v -> {

            /*Bundle bundle = new Bundle();
            bundle.putString(mContext.getString(R.string.field_description), sAutoDesp);
            bundle.putString(mContext.getString(R.string.field_username), sUsername);
            bundle.putString(mContext.getString(R.string.field_display_name), sFullName);
            bundle.putString(mContext.getString(R.string.field_course), sCourseName);
            bundle.putBoolean("auto_desp", isAutoDespEnabled);*/
            requireFragmentManager().beginTransaction().remove(CourseAndFullNameCardFragment.this).commit();
            GetStartedFragment fragment = new GetStartedFragment();
            // fragment.setArguments(bundle);
            FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FrameLayoutCard, fragment);
            Transaction.addToBackStack(mContext.getString(R.string.get_started_fragment));
            Transaction.commit();
        });


        return view;
    }


    private void GetLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


        // user defines the criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //default
        criteria.setCostAllowed(false);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            provider = locationManager.getBestProvider(criteria, false);
            final Location location = locationManager.getLastKnownLocation(provider);

            mylistener = new MyLocationListener();
            if (location != null) {
                mylistener.onLocationChanged(location);
            } else {
                // leads to the settings because there is no last known location
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                //Using 12 seconds timer till it gets location
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Obtaining Location ...");
                alertDialog.setMessage("00:12");
                alertDialog.show();

                new CountDownTimer(12000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        alertDialog.dismiss();
                    }
                }.start();
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // this will beautomatically asked to be implemented
                ActivityCompat.requestPermissions(requireActivity(), new String[]
                        {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
            // location updates: at least 1 meter and 200millsecs change
            locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lon, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreateView: lat lon: " + lat + "/" + lon);
                Log.d(TAG, "onCreateView: addresses: " + addresses);
                //set text of xml file
            } else {
                Toast.makeText(mContext, "Please open your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]
                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void BuildString() {
        sbCollegeHeading.delete(0, sbCollegeHeading.length());
        sbCollegeHeading.append(sbCollegeHeading1.toString()).append(sbCollegeHeading2.toString()).append(sbCollegeHeading3.toString());
        user.setAuto_desp(sbCollegeHeading.toString());
    }


    private void SetFullNameCourseLoc() {
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_course))
                .setValue(user.getCourse());

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_location))
                .setValue(user.getLocation());

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_auto_desp))
                .setValue(user.getAuto_desp());

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_display_name))
                .setValue(user.getDisplay_name());

        myRef.child(mContext.getString(R.string.dbname_filter_data))
                .child(mContext.getString(R.string.field_course))
                .child("course_" + mCourseList.indexOf(user.getCourse()))
                .child(user.getUser_id())
                .setValue("user_id");
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
        CoursesRecyclerViewAdapter aCourses = new CoursesRecyclerViewAdapter(user.getCourse(), mCourseList, getActivity(), onCoursesClickListener);
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
            //  Log.d(TAG, "onItemClick: sCourseName: " + sCourseName);
            if (TextUtils.isEmpty(user.getCourse()) || user.getCourse().equals("N/A")) {
                user.setCourse(mCourseList.get(position - 1));
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
                CourseSelectedTV.setText("Awesome! " + /*CourseNameList.get(0)*/ user.getCourse() + " is a great choice.");
                Dialog.setContentView(CourseSelectedTV);
                Dialog.show();
                android.os.Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(Dialog::dismiss, 3000);
//                int startName = mCollege_Heading.getText().toString().indexOf(mFullName.getText().toString());
//                int EndName = startName + mFullName.getText().length();
//                String BeforeFullName = mCollege_Heading.getText().toString().substring(0, startName);
//                String FullName = mCollege_Heading.getText().toString().substring(startName, EndName);

                if (user.isAutoDespEnabled()) {
                    sbCollegeHeading3.delete(0, sbCollegeHeading3.length());
                    sbCollegeHeading3.append(" pursuing <U>").append(user.getCourse()).append("</U>.");
//
                    BuildString();
                    TTV_AUTO_DESP.animateText(Html.fromHtml(sbCollegeHeading.toString(), Html.FROM_HTML_MODE_LEGACY));
                }
                Log.d(TAG, String.format("onClick: Course Selected %s", user.getCourse()));
            } else {
                if (TickMe.getVisibility() == View.VISIBLE) {
                    user.setCourse("N/A");
                    CourseSelectedNumber = "";
                    TickMe.setVisibility(View.GONE);
                    if (user.isAutoDespEnabled()) {
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

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: lat long: " + location.getLatitude() + location.getLongitude());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(mAuth.getCurrentUser().getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()));

            user.setLocation(String.valueOf(location.getLatitude() + "," + location.getLongitude()));
            SetFullNameCourseLoc();

            user.setDisplay_name(ET_FULLNAME.getText().toString());
            Log.d(TAG, "onCreateView: sCourseName: " + user.getCourse());
            Log.d(TAG, "onCreateView: sFullName: " + user.getDisplay_name());
            Log.d(TAG, "onCreateView: sAutoDesp: " + user.getAuto_desp());
            geoFire.getLocation(mAuth.getCurrentUser().getUid(), new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    if (location != null) {
                        Log.d(TAG, "onLocationResult: lat long: " + location.latitude + location.longitude);
                    } else {
                        Log.d(TAG, "onLocationResult: key: " + key);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("There was an error getting the GeoFire location: " + databaseError);
                }
            });
            databaseWriteExecutor.execute(() -> {
                dialog.cancel();
                userDetailsDao.UpdateCourseDespFullNameLoc(user.getCourse(), user.getAuto_desp(), user.getDisplay_name(), user.getLocation(), mAuth.getCurrentUser().getUid());
                requireFragmentManager().beginTransaction().remove(CourseAndFullNameCardFragment.this).commit();
                FillDetailsFragment fragment = new FillDetailsFragment();
                FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Transaction.replace(R.id.FrameLayoutCard, fragment);
                Transaction.addToBackStack(mContext.getString(R.string.fragment_fill_details));
                Transaction.commit();
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: status: " + provider);

        }

        @Override
        public void onProviderEnabled(String provider) {


            Log.d(TAG, "onProviderEnabled: status: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

            Log.d(TAG, "onProviderDisabled: status: " + provider);
        }
    }

}
