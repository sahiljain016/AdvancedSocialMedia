package com.gic.memorableplaces.SignUp;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;


public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUp Activity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_sign_up);
        mContext = SignUpActivity.this;


        // PrepareCardFragment fragment = new PrepareCardFragment();
        SignUpFragment fragment = new SignUpFragment();
        if (getIntent().hasExtra("status")) {
            if (getIntent().getStringExtra("status").equals("not_done")) {
                Bundle bundle = new Bundle();
                bundle.putString("status", "not_done");

                bundle.putString(mContext.getString(R.string.field_username),getIntent().getStringExtra(mContext.getString(R.string.field_username)));
                bundle.putString(mContext.getString(R.string.field_description),getIntent().getStringExtra(mContext.getString(R.string.field_description)));
                bundle.putString(mContext.getString(R.string.field_course),getIntent().getStringExtra(mContext.getString(R.string.field_course)));
                bundle.putString(mContext.getString(R.string.field_display_name),getIntent().getStringExtra(mContext.getString(R.string.field_display_name)));
                fragment.setArguments(bundle);
            }
        }
        FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
        Transaction.replace(R.id.FrameLayoutCard, fragment);
        Transaction.addToBackStack(mContext.getString(R.string.sign_up_fragment));
        Transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
        firebaseMethods.SetOnlineStatus(false);
    }

    @Override
    public void onBackPressed() {

    }
}