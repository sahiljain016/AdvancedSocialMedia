package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.gic.memorableplaces.DataModels.UserAccountSettings;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.UniversalImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "SearchUserListAdapter";
    private Context mContext;
    private int layoutResource;
    private DatabaseReference reference;

    public SearchUserListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResource = resource;

        reference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.ItemName);
        final TextView FullName = convertView.findViewById(R.id.CreatorName);
        final CircleImageView profileImage = convertView.findViewById(R.id.CoverImageView);
        RelativeLayout relativeLayout = convertView.findViewById(R.id.base_RL_search_list);
        RelativeLayout relativeLayout1 = convertView.findViewById(R.id.RelLAyout2);
        Skeleton skeletonLayout  = SkeletonLayoutUtils.createSkeleton(relativeLayout);
        Skeleton skeletonLayout1  = SkeletonLayoutUtils.createSkeleton(relativeLayout1);
        skeletonLayout.setShowShimmer(true);
        skeletonLayout.setShimmerDurationInMillis(1000);
        skeletonLayout1.setShowShimmer(true);
        skeletonLayout1.setShimmerDurationInMillis(1000);
        skeletonLayout1.showSkeleton();
        skeletonLayout.showSkeleton();
        if (((position % 2) == 0) || position == 0) {
            relativeLayout.setBackgroundColor(Color.BLACK);
            userName.setTextColor(Color.WHITE);
            FullName.setTextColor(Color.WHITE);
            profileImage.setBorderColor(Color.WHITE);

        } else {
            relativeLayout.setBackgroundColor(Color.WHITE);
            userName.setTextColor(Color.BLACK);
            FullName.setTextColor(Color.BLACK);
            profileImage.setBorderColor(Color.BLACK);
        }
        userName.setText(getItem(position));

        Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_username))
                .equalTo(getItem(position));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Entered on Data Changed");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ImageLoader.getInstance().init(UniversalImageLoader.getConfig(mContext, R.drawable.default_user_profile));
                    UniversalImageLoader.setImage(Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getProfile_photo(), profileImage,
                            null, "");
                    FullName.setText(Objects.requireNonNull(ds.getValue(UserAccountSettings.class)).getDisplay_name());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        skeletonLayout.showOriginal();
        skeletonLayout1.showOriginal();
        return convertView;

    }
}
