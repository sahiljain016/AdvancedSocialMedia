package com.gic.memorableplaces.Home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.SignUp.PrepareCardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFrag extends Fragment {
    private static final String TAG = "HomeFrag";
    private static final int Activity_num = 0;
    public HomeActivity activity;
    private Context mContext = ((HomeActivity) getActivity());
    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    BottomNavigationView bottomNavigationView;

    ViewPager viewPager;
    TabLayout tabLayout;
    RelativeLayout RL_TOP;
    CardView CV_TOP;
    private ImageButton Messages, cam;
    private ImageView RL_TOP_BG;
//    private AppCompatButton FFButton, SocialFeedButton;
    private Runnable runnable;
    private android.os.Handler handler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        Log.d(TAG, "onCreateView: HomeFrag");

        //viewPager = view.findViewById(R.id.container);
        // tabLayout = view.findViewById(R.id.tabs);
        Messages = view.findViewById(R.id.Message_Icon);
        cam = view.findViewById(R.id.Camera_Home_frag);
        //FFButton = view.findViewById(R.id.Search_Friends_Button);
        //SocialFeedButton = view.findViewById(R.id.Social_Feed_Button);
        RL_TOP = view.findViewById(R.id.RL_TOP);
//        CV_TOP = view.findViewById(R.id.Top_CV);
//        RL_TOP_BG = view.findViewById(R.id.RL_TOP_BG);

        SocialFeedFragment fragment = new SocialFeedFragment();
        FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Transaction.replace(R.id.container, fragment);
        Transaction.addToBackStack(getActivity().getString(R.string.social_feed_fragment));
        Transaction.commit();

//        final Animation BounceAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
//        handler = new Handler(Looper.getMainLooper());
//        RL_TOP_BG.setImageResource(R.drawable.hansraj_front);
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (RL_TOP.getVisibility() == View.VISIBLE) {
//                    RL_TOP_BG.animate().alpha(0).setDuration(1000).setStartDelay(750).start();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_home_2);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//
//                        }
//                    }, 1750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.animate().alpha(0).setDuration(1000).start();
//                        }
//                    }, 3750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_home_3);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//
//                        }
//                    }, 5250);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.animate().alpha(0).setDuration(1000).start();
//                        }
//                    }, 7750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_home_4);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//
//                        }
//                    }, 9750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.animate().alpha(0).setDuration(1000).start();
//                        }
//                    }, 11750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_home_5);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//
//                        }
//                    }, 13750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.animate().alpha(0).setDuration(1000).start();
//                        }
//                    }, 15750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_home_6);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//
//                        }
//                    }, 17750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.animate().alpha(0).setDuration(1000).start();
//                        }
//                    }, 19750);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RL_TOP_BG.setImageResource(R.drawable.hansraj_front);
//                            RL_TOP_BG.animate().alpha(1).setDuration(1000).start();
//                            handler.postDelayed(runnable, 2000);
//
//                        }
//                    }, 21750);
//
//                }
//
//            }
//        };


//        SocialFeedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SocialFeedButton.setBackgroundResource(R.drawable.custom_button_home_coloured);
//                FFButton.setBackgroundResource(R.drawable.custom_button_home);
//
//                SocialFeedButton.startAnimation(BounceAnim);
//                SocialFeedButton.setPressed(true);
//
//                SocialFeedFragment fragment = new SocialFeedFragment();
//                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.container, fragment);
//                Transaction.addToBackStack(getActivity().getString(R.string.social_feed_fragment));
//                Transaction.commit();
//
//            }
//        });
//        FFButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FFButton.setBackgroundResource(R.drawable.custom_button_home_coloured);
//                SocialFeedButton.setBackgroundResource(R.drawable.custom_button_home);
//                Animation myAnimUP = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up2);
//                Animation myAnimDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
//                CV_TOP.startAnimation(myAnimUP);
//                //HomeActivity.CL_BOTTOM_NAV.startAnimation(myAnimDown);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        CV_TOP.setVisibility(View.GONE);
//                     //   HomeActivity. CL_BOTTOM_NAV.setVisibility(View.GONE);
//                    }
//                },1500);
//
//                FFButton.startAnimation(BounceAnim);
//                FFButton.setPressed(true);
//                FriendsFilterFragment fragment = new FriendsFilterFragment();
//                FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                Transaction.replace(R.id.container, fragment);
//                Transaction.addToBackStack(getActivity().getString(R.string.friends_filter_fragment));
//                Transaction.commit();
//            }
//        });

        Messages.setOnClickListener(v -> {

//                 Intent intent = new Intent(getActivity(), MessageActivity.class);
//                 startActivity(intent);
//                 Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

            PrepareCardFragment fragment1 = new PrepareCardFragment();
            FragmentTransaction Transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
            Transaction1.replace(R.id.container, fragment1);
            Transaction1.addToBackStack(getActivity().getString(R.string.prepare_card_fragment));
            Transaction1.commit();
        });


        //HomeActivity homeActivity = new HomeActivity();
        //bottomNavigationView = (homeActivity).findViewById(R.id.BottomNavigationMenu);

        //init();
        // RELATED TO UNIVERSAL IMAGE LOADER FIND METHOD BELOW
        // initImageLoader();
        //Bottom navigation view
        // setUpBottomNav();
        // View pager
        // SetUpViewPager();
        //setUpBottomNav();
        // FIREBASE AUTHENTICATION
        //setupFirebaseAuth();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // runnable.run();
//        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // handler.removeCallbacks(runnable);
    }

    //RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
    public void setUpBottomNav() {
        Log.d(TAG, "Bottom nav view clicked");

//        NavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }

    //RESPONSIBLE FOR ADDING THE THREE FRAGMENTS TO THE LIST BY ADAPTER
//    private void SetUpViewPager() {
//        Log.d(TAG, "ONCREATE STARTED");
//        //SectionViewPagerAdapter adapter = new SectionViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        SectionStateViewPagerAdapter sectionStateViewPagerAdapter = new SectionStateViewPagerAdapter(getChildFragmentManager());
//
//        sectionStateViewPagerAdapter.AddFragments(new FriendsFilterActivity(), getActivity().getString(R.string.friends_filter_fragment)); //index 0
//        sectionStateViewPagerAdapter.AddFragments(new SocialFeedFragment(), getActivity().getString(R.string.social_feed_fragment)); //index 1
//        sectionStateViewPagerAdapter.AddFragments(new StudentDashboardFragment(), getActivity().getString(R.string.student_dashboard_fragment)); //index 2
//        viewPager.setAdapter(sectionStateViewPagerAdapter);
//
//        tabLayout.setupWithViewPager(viewPager);
//        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("News Wall");
//        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Social Feed");
//        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("Student Dashboard");
//        viewPager.setCurrentItem(1, false);
//    }


//    private void init() {
//        // removing toolbar elevation
//        String[] titles = new String[]{"Movies", "Events", "Tickets"};
//        viewPager.setAdapter(new ViewPagerFragmentAdapter(Objects.requireNonNull(getActivity())));
//        tabLayout.setUp(viewPager);
//        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("News Wall");
//        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Social Feed");
//        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("BSC IT ONLY");
//        viewPager.setCurrentItem(1,false);
//        // attaching tab mediator
////        new TabLayoutMediator(tabLayout, viewPager,
////                (TabLayoutMediator.TabConfigurationStrategy)
////                        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("News WAll"));
//    }
//
//private static class ViewPagerFragmentAdapter extends FragmentStateAdapter {
//
//    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        switch (position) {
//            case 0:
//                return new NewsWallFragment();
//            case 1:
//                return new SocialFeedFragment();
//            case 2:
//                return new ClassroomBSCITFragment();
//        }
//        return new SocialFeedFragment();
//    }
//
//    @Override
//    public int getItemCount() {
//        return 3;
//    }
//}
//    private void initImageLoader() {
//
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
//        if()
//        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(getActivity(),R.drawable.default_user_profile));
//    }


//       /*
//    ------------------------------------ Firebase ---------------------------------------------
//     */
//
//    /**
//     * checks to see if the @param 'user' is logged in
//     * @param user
//     */
//    private void checkCurrentUser(FirebaseUser user){
//        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");
//
//        if(user == null){
//            Intent intent = new Intent(getActivity(), LogInActivity.class);
//            startActivity(intent);
//        }
//    }
//
//    /**
//     * Setup the firebase auth object
//     */
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                //check if the user is logged in
//                checkCurrentUser(user);
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//        checkCurrentUser(mAuth.getCurrentUser());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
}
