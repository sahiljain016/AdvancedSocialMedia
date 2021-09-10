package com.gic.memorableplaces.FilterFriends;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.R;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

import static com.gic.memorableplaces.FilterFriends.FilterFragment.mContext;

public class ContentFragment extends Fragment implements ScreenShotable {
    private static final String TAG = "ContentFragment";
    public static final String CLOSE = "Close";
    public static final String AGE = "Age";
    public static final String COURSE = "Course";
    public static final String COLLEGE_YEAR = "College_Year";
    public static final String GENDER = "Gender";
    public static final String ZODIAC = "Zodiac";
    public static final String BOOK = "Book";
    public static final String HOBBIES = "Hobbies";
    public static final String GAME = "Game";
    public static final String MUSIC = "Music";
    public static final String SOCIETY = "society";
    public static final String PRONOUN = "pronoun";
    public static final String MOVIE = "Movie";

    private View containerView;
    protected ImageView mImageView;
    protected int res;
    private Context mContext;
    private Bitmap bitmap;
    private TextView TV_LEFT, TV_RIGHT;
    private RelativeLayout DESP_RL;

    public static ContentFragment newInstance(int resId,int LEFT_TV_COLOR, int RIGHT_TV_COLOR,String RL_COLOR,Context mContext) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("LEFT_TV_COLOR",LEFT_TV_COLOR);
        bundle.putInt("RIGHT_TV_COLOR",RIGHT_TV_COLOR);
        bundle.putString("RL_COLOR",RL_COLOR);
        bundle.putInt(Integer.class.getName(), resId);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getArguments().getInt(Integer.class.getName());
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_other_card, container, false);
        TV_LEFT = rootView.findViewById(R.id.LeftTV_OC);
        //TV_RIGHT = rootView.findViewById(R.id.RightTV_OC);
        DESP_RL = rootView.findViewById(R.id.DESP_OC);

        if (getArguments() != null) {
                TV_LEFT.setBackgroundResource(getArguments().getInt(mContext.getString(R.string.LEFT_TV_COLOR)));
                TV_RIGHT.setBackgroundResource(getArguments().getInt(mContext.getString(R.string.RIGHT_TV_COLOR)));
                DESP_RL.setBackgroundColor(Color.parseColor(getArguments().getString(mContext.getString(R.string.RL_COLOR))));
        }

        return rootView;
    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}