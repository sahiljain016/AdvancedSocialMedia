package com.gic.memorableplaces.Share;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gic.memorableplaces.utils.CheckPermissions;
import com.gic.memorableplaces.utils.DirectoryScanner;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.Platform;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.google.android.material.tabs.TabLayout;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.Permissions;
import com.gic.memorableplaces.Adapters.SectionViewPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "Add Activity";
    private ViewPager mViewPager;
    TabLayout tabLayout;
    //constants
    private static final int Activity_num = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST_CODE = 1;
    Context mContext;
    private final int requestCode = 3;
    private final String[] permissionList = {
            Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = ShareActivity.this;

        if (CheckPermissionsArray(Permissions.PERMISSIONS)) {
            //View Pager For Fragments
            SetUpViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
        if (Build.VERSION.SDK_INT <= 29 && !CheckPermissions.hasPermissions(mContext, permissionList)) {

            //Toast.makeText(mContext, "Please give the required permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, permissionList, requestCode);
        } else {
            //Loading all image and video directories
            new DirectoryScanner().execute(Environment.getExternalStorageDirectory().getAbsolutePath());

            Log.d(TAG, "Scanning Files");
        }


        initImageConfig();

    }


    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();

    }

    /**
     * verifying array of permissions
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions array");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST_CODE
        );

    }

    /**
     * Checking all permissions in the array
     *
     * @param permissions
     * @return
     */
    public Boolean CheckPermissionsArray(String[] permissions) {
        Log.d(TAG, "CheckPermissionsArray: checking permissions array");
        for (String check : permissions) {
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * checking permissions one by one if it has been verified
     *
     * @param permission
     * @return
     */
    public Boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking single permission");

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: permission denied by user" + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: permission grated by user" + permission);
            return true;
        }
    }

    private void SetUpViewPager() {
//        Log.d(TAG, "ONCREATE STARTED");
        SectionViewPagerAdapter adapter = new SectionViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.AddFragment(new GalleryFragment()); //index 0
        adapter.AddFragment(new CameraFragment()); //index 1

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabBottomShare);

        tabLayout.setupWithViewPager(mViewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.gallery));
//        Log.d(TAG, "SetUpViewPager: " + tabLayout.getTabAt(0).getClass());
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getString(R.string.camera));


    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!Platform.hasAndroidR()) {
            if (requestCode == 3) {
                if (CheckPermissions.hasPermissions(mContext, permissionList)) {
                    //Loading all image and video directories
                    new DirectoryScanner().execute(Environment.getExternalStorageDirectory().getAbsolutePath());

                    Log.d(TAG, "Scanning Files");
                } else {
                    finish();
                }
            }
        }
    }

    private void initImageConfig() {
        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(ShareActivity.this, R.drawable.ic_default_image_share));

    }

    /* //RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
    public void setUpBottomNav(){
        Log.d(TAG, "Bottom nav view Share clicked");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigationMenu);

        NavigationViewHelper.enableNavigation(ShareActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }*/
    @Override
    protected void onPause() {
        super.onPause();
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