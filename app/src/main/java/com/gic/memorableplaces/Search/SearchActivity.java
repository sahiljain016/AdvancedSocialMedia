package com.gic.memorableplaces.Search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;

public class SearchActivity extends AppCompatActivity {
private static final String TAG = "Search Activity";
    private static final int Activity_num = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Bottom Navigation View
       // setUpBottomNav();

    }
//RESPONSIBLE FOR SETTING UP BOTTOM NAVIGATION VIEW
  /*  public void setUpBottomNav(){
        Log.d(TAG, "Bottom nav view clicked");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.BottomNavigationMenu);

        NavigationViewHelper.enableNavigation(SearchActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseMethods firebaseMethods = new FirebaseMethods(this);
        firebaseMethods.SetOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(this);
        firebaseMethods.SetOnlineStatus(true);
    }
}