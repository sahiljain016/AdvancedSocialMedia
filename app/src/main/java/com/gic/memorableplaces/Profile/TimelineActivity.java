package com.gic.memorableplaces.Profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.gic.memorableplaces.Adapters.SectionStateViewPagerAdapter;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "Timeline Activity";
    private static final int Activity_num = 4;
    private static final int NUM_OF_COLUMNS = 3;

    public ViewPager viewPager;
    public SectionStateViewPagerAdapter pagerAdapter;

    ImageButton imageButton;
    ImageView ProfilePhoto;

    public static Boolean Fragment_one;

    public Button editProfileButton;

    ProgressBar mProgressBar;

    private Context mContext = TimelineActivity.this;

   /* //RESPONSIBLE FOR SHOWING POP UP SETTINGS MENU
    public void ImageButtonMenuOnClick(View view){

    }
    //RESPONSIBLE FOR SHOWING EDIT PROFILE FRAGMENT
    public void EditProfilePopUpButton(View view){

        setViewPager(0);

        Log.d(TAG,"KHULAAAAA EDIT PRFILE");

    }
    //RESPONSIBLE FOR SHOWING lOG OUT FRAGMENT
    public void LogOutPopUpButton(View view){

        setViewPager(1);
        Log.d(TAG,"KHULAAAAA LOG OUT");
    }
    //RESPONSIBLE FOR SHOWING REPORT BUG FRAGMENT
    public void ReportBugPopUpButton(View view){

        setViewPager(2);
        Log.d(TAG,"KHULAAAAA REPORT BUG");
    }
    //RESPONSIBLE FOR SHOWING POP UP SETTINGS MENU
    public void ClosePopUpButton(View view){
        Log.d(TAG,"KHULAAAAA");

        relativeLayout.animate().translationYBy(1300).setDuration(1000);
        imageButton.setEnabled(true);
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile_activity);

        Fragment_one = true;

        //init();


        //imageButton = (ImageButton) findViewById(R.id.menuProfileIcon);
        /*// INITIALISATION OF WIDGETS
        InitializeAllWidgets();

        //POP UP MENU OCATION SETTINGS
        relativeLayout.setY(2000);
        relativeLayout.setVisibility(View.VISIBLE);



        //Bottom Navigation View
       setUpBottomNav();
       // TOP TOOLBAR
        setUpProfileToolbar();
        // FRAGMENTS
        setUpFragments();
        //PROFILE PHOTO ON TIMELINE
        setUpProfileImage();
        // TESSTING GRID SETUP
        TempGridSetUp();*/
    }

    public void init() {
        Log.d(TAG, "init: inflating: " + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = TimelineActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Frame_layout_profile, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


    /*
        //RESPONSIBLE FOR INITIALIZING OF WIDGETS
        public void InitializeAllWidgets(){



            editProfileButton = (Button) findViewById(R.id.EditProfileButton);



            ProfilePhoto = (ImageView) findViewById(R.id.profile_photo);

            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_profile);
        }
    // JUST FOR TESTING NOW

        private void TempGridSetUp(){
            ArrayList<String> imgURLs = new ArrayList<>();
            imgURLs.add("http://2.bp.blogspot.com/-Nc7P4e1mdAM/TkAVoMlrkaI/AAAAAAAACsw/pzJSmVBVI0A/s1600/best+hollywood+actresses1.jpg");
            imgURLs.add("https://cdn-0.pickytop.com/wp-content/uploads/2020/03/Beautiful-Hollywood-Actress-1-1.jpg");
            imgURLs.add("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/38541941-413446692511829-5258135049525526528-n-1553264482.jpg?crop=1xw:1xh;center,top&resize=768:*");
            imgURLs.add("https://qphs.fs.quoracdn.net/main-qimg-f7725da51cb81a03c0427cf3d2a83842");
            imgURLs.add("https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cg_face%2Cq_auto:good%2Cw_300/MTU1MTI3NDQ5MzI2MjAwNDE3/lucy-hale-launches-mark-spring-beauty-collection-at-the-london-hotel-on-april-8-2015-in-west-hollywood-california-photo-by-rob-loud_wireimage-square.jpg");
            imgURLs.add("https://scontent.fdel27-1.fna.fbcdn.net/v/t1.0-9/49148058_2190634814552709_5088370054764953600_n.jpg?_nc_cat=107&_nc_sid=09cbfe&_nc_ohc=e39QAkYWOgEAX_e7l65&_nc_ht=scontent.fdel27-1.fna&oh=26b2ab418537c6f4330110a24287e961&oe=5F789931");
            imgURLs.add("http://4.bp.blogspot.com/-zuUu4T2ETuw/Tf4f_EbPo6I/AAAAAAAAAB0/5vM6a1jwja4/s1600/kristen-stewart-wallpaper1.jpg");
            imgURLs.add("https://i0.wp.com/hronika.info/wp-content/uploads/2019/09/kameron-696x435.jpg");
            imgURLs.add("https://observatoriodocinema.uol.com.br/wp-content/uploads/2019/07/Courteney-Cox-Jennifer-Aniston-Lisa-Kudrow-as-Monica-Rachel-and-Phoebe-in-Friends-1.jpg");
            imgURLs.add("https://pyxis.nymag.com/v1/imgs/3b6/d67/84797c3613ee95604b9262ce0823c67a2e-21-selena-gomez.rsquare.w330.jpg");
            imgURLs.add("https://i.imgur.com/FV3jjJD.jpg");
            imgURLs.add("https://qphs.fs.quoracdn.net/main-qimg-6fc8984ea517d8d4d797292e0de254ca.webp");
            imgURLs.add("https://i.insider.com/55b29248371d22ce178b966c?width=2679");
          SetUpImageGrid(imgURLs);
        }
        //RESPONSIBLE FOR SETTINGS IMAGES IN GIDVIEW PROFILE

        public void SetUpImageGrid(ArrayList<String> imgURLs){
            GridView gridView = (GridView) findViewById(R.id.grid_view);

            int GridWidth  = getResources().getDisplayMetrics().widthPixels;
            int Imagewidth  = GridWidth/NUM_OF_COLUMNS;

            gridView.setColumnWidth(Imagewidth);
            GridImageAdapter adapter = new GridImageAdapter(mContext,R.layout.layout_gridview_images,"",imgURLs);
            gridView.setAdapter(adapter);
        }

        //RESPONSIBLE FOR SETTING PROFILE PHOTO
        public void setUpProfileImage(){
    Log.d(TAG, "profle is sETTINGS UPP!");
            String imgURL = "images.squarespace-cdn.com/content/v1/5b5ec35c372b96ca74e1d082/1550583202333-H87LVN3QYVV23NPD083M/ke17ZwdGBToddI8pDm48kGFcTF_ZTyo11mUQIiHxsAVZw-zPPgdn4jUwVcJE1ZvWhcwhEtWJXoshNdA9f1qD7Xj1nVWs2aaTtWBneO2WM-vfEGZhtFi5XTYJIylVVnv-9eMcrJGLHk-VxRvNc3JqMg/Emma-Watson-headshot-2016_300pxTall.jpg?format=300w";
            UniversalImageLoader.setImage(imgURL,ProfilePhoto,mProgressBar,"https://");

        }




        //RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
        public void setUpBottomNav(){
            Log.d(TAG, "Bottom nav view Profile clicked");

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigationMenu);

            NavigationViewHelper.enableNavigation(TimelineActivity.this, bottomNavigationView);
            Menu menu = bottomNavigationView.getMenu();
            MenuItem menuItem = menu.getItem(Activity_num);
            menuItem.setChecked(true);
        }
        //RESPONSIBLE FOR PROFILE TOP TOOLBAR
        public void setUpProfileToolbar(){
            final Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar);
            setSupportActionBar(toolbar);


        }

       public void setViewPager(int FragmentNumber){
            relativeLayout= (RelativeLayout) findViewById(R.id.popupRelLayout);
            relativeLayoutProfile = (RelativeLayout) findViewById(R.id.mainRelLayoutProfile) ;
            relativeLayoutProfile.setVisibility(View.GONE);
            viewPager = (ViewPager) findViewById(R.id.container);
            Log.d(TAG,"setViewpager succesfull");

            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(FragmentNumber);
        }

        public void setUpFragments(){
            pagerAdapter = new SectionStateViewPagerAdapter(getSupportFragmentManager());
            pagerAdapter.AddFragments(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //Fragment0
            pagerAdapter.AddFragments(new LogOutFragment(), getString(R.string.log_out_fragment)); //Fragment 1
            pagerAdapter.AddFragments(new ReportBugFragment(), getString(R.string.report_bug_fragment)); //Fragment 2
        }*/
    @Override
    public void onBackPressed() {


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: onPause");
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(true);
    }
}



