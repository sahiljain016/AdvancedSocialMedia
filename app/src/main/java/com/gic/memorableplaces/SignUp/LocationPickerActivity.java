package com.gic.memorableplaces.SignUp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.NavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LocationPickerActivity extends AppCompatActivity {
private static final String TAG ="Heart Activity";
private static final int Activity_num = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Bottom Navigation View
//         Uri path = MediaStore.Video.VideoColumns.;
//        Log.d(TAG, "onCreate: "+ path);
        //setUpBottomNav();
    }
    //RESPONSIBLE FOR SETTING UP THE BOTTOM NAVIGATION VIEW
    public void setUpBottomNav(){
        Log.d(TAG, "Bottom nav view Heart clicked");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigationMenu);

        NavigationViewHelper.enableNavigation(LocationPickerActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }
}