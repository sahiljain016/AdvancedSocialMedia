package com.gic.memorableplaces.FilterFriends;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.CustomLibs.HTextView.TyperTextView;
import com.gic.memorableplaces.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class SideMenuFragment extends Fragment implements yalantis.com.sidemenu.util.ViewAnimator.ViewAnimatorListener {
    private static final String TAG = "LikeFragment";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ViewAnimator viewAnimator;
    private int res = R.drawable.ic_filter_age;
    private LinearLayout linearLayout;
    private Toolbar toolbar;
    private TyperTextView title;
    private View rootView ,view;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.other_user_card, container, false);
        Log.d(TAG, "onCreateView: LikeFragment");
        mContext =getActivity();
        toolbar = view.findViewById(R.id.toolbar);
        title = view.findViewById(R.id.TITLE_TV);
        rootView =  view.findViewById(R.id.content_frame);

//        Bundle bundle = new Bundle();
//        bundle.putString(mContext.getString(R.string.field_age),mContext.getString(R.string.age));
        ContentFragment contentFragment = ContentFragment.newInstance(R.drawable.ic_filter_music,R.color.home_color_1,R.color.home_color_3,"#FFB3CE",mContext);
       // contentFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, contentFragment)
                .commit();
        drawerLayout = view.findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = view.findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        setActionBar();
        createMenuList();
viewAnimator = new yalantis.com.sidemenu.util.ViewAnimator<>(((FriendsFilterActivity)getActivity()), list, contentFragment, drawerLayout, this);


        //to open menu you have to override ActionBarDrawerToggle method

        return view;
    }
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(ContentFragment.CLOSE, R.drawable.ic_profile_popup_close);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(ContentFragment.AGE, R.drawable.ic_filter_age);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(ContentFragment.COURSE, R.drawable.ic_course_icon);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(ContentFragment.COLLEGE_YEAR, R.drawable.ic_my_college);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ContentFragment.GENDER, R.drawable.ic_filter_gender);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(ContentFragment.ZODIAC, R.drawable.ic_filter_zodiac_scorpion);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(ContentFragment.HOBBIES, R.drawable.ic_filter_hobbies);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(ContentFragment.GAME, R.drawable.ic_filter_video_games);
        list.add(menuItem7);
        SlideMenuItem menuItem8 = new SlideMenuItem(ContentFragment.MUSIC, R.drawable.ic_filter_music);
        list.add(menuItem8);
        SlideMenuItem menuItem9 = new SlideMenuItem(ContentFragment.MOVIE, R.drawable.ic_filter_movie);
        list.add(menuItem9);
        SlideMenuItem menuItem10 = new SlideMenuItem(ContentFragment.BOOK, R.drawable.ic_filter_book);
        list.add(menuItem10);
        SlideMenuItem menuItem11 = new SlideMenuItem(ContentFragment.SOCIETY, R.drawable.ic_filter_society);
        list.add(menuItem11);
        SlideMenuItem menuItem12 = new SlideMenuItem(ContentFragment.PRONOUN, R.drawable.ic_filter_gender);
        list.add(menuItem12);
    }

    private void setActionBar() {
        ((FriendsFilterActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((FriendsFilterActivity) requireActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        Objects.requireNonNull(((FriendsFilterActivity) requireActivity()).getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(((FriendsFilterActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                ((FriendsFilterActivity) getActivity()),                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        requireActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition,int LEFT_TV_COLOR, int RIGHT_TV_COLOR,String RL_COLOR) {
       // this.res = this.res == R.drawable.ic_music ? R.drawable.ic_movie : R.drawable.ic_music;
        Log.d(TAG, "replaceFragment: position: " + this.res);
        int finalRadius = Math.max(rootView.getWidth(), rootView.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(rootView, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(1000);

        //view.findViewById(R.id.content_overlay).setBackground(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
//        Bundle bundle = new Bundle();
//        bundle.putString(Key,Value);
        ContentFragment contentFragment = ContentFragment.newInstance(this.res, LEFT_TV_COLOR,  RIGHT_TV_COLOR, RL_COLOR,mContext);
       // contentFragment.setArguments(bundle);
        (requireActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return contentFragment;
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        String key = "key",value = null,RLColor = null;
        int LeftTvColor = 0,RightTVColor = 0;
        String name = slideMenuItem.getName();
        if (ContentFragment.CLOSE.equals(name)) {
            LeftTvColor = R.color.home_color_1;
            RightTVColor = R.color.home_color_3;
            RLColor = "#FFB3CE";
            title.animateText("AGE");
        } else if (ContentFragment.AGE.equals(name)) {
            LeftTvColor = R.color.home_color_1;
            RightTVColor = R.color.home_color_3;
            RLColor = "#FFB3CE";
            title.animateText("AGE");
        } else if (ContentFragment.GENDER.equals(name)) {
            LeftTvColor = R.color.home_color_2;
            RightTVColor = R.color.gender_colour_right;
            RLColor = "#E996FF";
            title.animateText("GENDER");
        }
        //Log.d(TAG, "onSwitch: key & value :" + key +"    "+ value);
        return replaceFragment(screenShotable, position, LeftTvColor,RightTVColor,RLColor);

    }

    @Override
    public void disableHomeButton() {
        ((FriendsFilterActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(false);

    }

    @Override
    public void enableHomeButton() {
        ((FriendsFilterActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}
