package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gic.memorableplaces.DataModels.Comments;
import com.gic.memorableplaces.Profile.CommentsActivity;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsListAdapter extends ArrayAdapter<Comments> {
    private static final String TAG = "CommentsListAdapter";

    private LayoutInflater mInflater;
    private boolean  isCaption;
    private int LayoutResource;
    private Context mContext;
    public ViewHolder holder;
    private long NoOfLikes;
    private String HtmlCaption;
    private Typeface face;
    public DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;
    private MiscTools miscTools = new MiscTools();


    public CommentsListAdapter(@NonNull Context context, int resource, @NonNull List<Comments> objects,
           boolean Caption) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        mAuth = FirebaseAuth.getInstance();
        isCaption = Caption;

        LayoutResource = resource;
        myRef = FirebaseDatabase.getInstance().getReference();
        firebaseMethods = new FirebaseMethods(mContext);
    }

    public static class ViewHolder {
        TextView comment, likes, reply, date_posted;
        CircleImageView profile_photo;
        ImageView white_like;
        ConstraintLayout constraintLayout;
        ImageView red_like;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if (convertView == null) {
            convertView = mInflater.inflate(LayoutResource, parent, false);
            holder = new ViewHolder();

            holder.comment = convertView.findViewById(R.id.comment);
            holder.likes = convertView.findViewById(R.id.comment_likes);
            holder.reply = convertView.findViewById(R.id.comment_reply);
            holder.date_posted = convertView.findViewById(R.id.comment_date_posted);
            holder.profile_photo = convertView.findViewById(R.id.comment_circleImageView);
            holder.white_like = convertView.findViewById(R.id.comment_like_image_view);
            holder.constraintLayout = convertView.findViewById(R.id.CL_Top_Comment);
            holder.red_like = convertView.findViewById(R.id.CommentLike);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setClickListener(holder.white_like, position, parent);
        setClickListener(holder.red_like, position, parent);
        //Log.d(TAG, "getView: photo " + commentsFragment.Photo);
//        if(commentsFragment.Photo){
//            commentsFragment.SetUpLike(mContext.getString(R.string.dbname_user_photos), Objects.requireNonNull(getItem(position)).getCommentID(),myRef);
//        }
//        else{
//           commentsFragment.SetUpLike(mContext.getString(R.string.dbname_user_videos), Objects.requireNonNull(getItem(position)).getCommentID(),myRef);
//        }
        Log.d(TAG, "getView: " + Objects.requireNonNull(getItem(position)).getCommentID());


        holder.constraintLayout.setBackgroundColor(Color.BLACK);
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(estimatedServerTimeMs);
                String TimeStamp = formatter.format(calendar.getTime());
                holder.date_posted.setText(miscTools.SetUpDateWidget(Objects.requireNonNull(getItem(position)).getDate_posted(), true,TimeStamp));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        //Log.d(TAG, "getView: getLikes " + Objects.requireNonNull(getItem(position)).getLikes());


        if (position != 0) {
            holder.constraintLayout.setBackgroundColor(Color.WHITE);

        }
        Log.d(TAG, "getView: Caption? " + isCaption);
        if (position == 0
                && Objects.requireNonNull(getItem(0)).getComment() != null
                && !Objects.requireNonNull(getItem(0)).getComment().isEmpty() && isCaption) {
            Log.d(TAG, "getView: isCaption " );
            HtmlCaption =
                    "<font color = '#FFFFFF' face = 'arial'>"
                            + "<B>"
                            + Objects.requireNonNull(getItem(position)).getUsername()
                            + "</B>"
                            + "</font>"
                            + " "
                            + Objects.requireNonNull(getItem(position)).getComment();
            face = setFontType(CommentsActivity.font);
            String fontColor = CommentsActivity.fontColor;
            Log.d(TAG, "getView: fontColor " + fontColor);
            holder.comment.setTextColor(CommentsActivity.setCaptionColor(fontColor));
            //holder.white_like.setImageResource(R.drawable.ic_white_heart_outline);
            holder.white_like.setVisibility(View.INVISIBLE);
            holder.likes.setVisibility(View.INVISIBLE);
            holder.reply.setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "getView: isNotCaption " );
            face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Arial.ttf");

            HtmlCaption = "<B>"
                    + Objects.requireNonNull(getItem(position)).getUsername()
                    + "</B>"
                    + " "
                    + Objects.requireNonNull(getItem(position)).getComment();
            if (position == 0) {
                Log.d(TAG, "getView: isNotCaption postion 0 " );
                holder.comment.setTextColor(Color.WHITE);
                holder.white_like.setImageResource(R.drawable.ic_white_heart_outline);
            } else {
                holder.comment.setTextColor(Color.BLACK);
            }

        }
        holder.comment.setTypeface(face);
        holder.comment.setText(Html.fromHtml(HtmlCaption, Html.FROM_HTML_MODE_LEGACY));

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Objects.requireNonNull(getItem(position)).getProfile_photo(), holder.profile_photo);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    private void setClickListener(View view, final int position, final ViewGroup parent){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this part is important, it lets ListView handle the clicks
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });
    }
    /**
     * settings the font received from firebase to the caption widget
     *
     * @param font
     */
    private Typeface setFontType(String font) {

        switch (font) {
            case "Arial":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Arial.ttf");
                break;
            case "akaDora":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/akaDora.ttf");
                break;
            case "Eutemia":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Eutemia.ttf");
                break;
            case "GREENPIL":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/GREENPIL.ttf");
                break;
            case "Grinched":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Grinched.ttf");
                break;
            case "Helvetica":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Helvetica.ttf");
                break;
            case "Libertango":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/Libertango.ttf");
                break;
            case "MetalMacabre":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/MetalMacabre.ttf");
                break;
            case "ParryHotter":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/ParryHotter.ttf");
                break;
            case "TheGodfather":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/TheGodfather_v2.ttf");
                break;
            case "waltograph":
                face = Typeface.createFromAsset(mContext.getAssets(), "fonts/waltograph42.ttf");
                break;
        }
        return face;
    }
}
