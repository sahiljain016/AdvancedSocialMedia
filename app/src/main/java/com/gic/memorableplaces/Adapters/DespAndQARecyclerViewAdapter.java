package com.gic.memorableplaces.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DespAndQARecyclerViewAdapter extends RecyclerView.Adapter<DespAndQARecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";

    //Variables
    private final ArrayList<String> alsDespList, alsQuesList;
    private final Context mContext;
    private final boolean isSmallText;
    private final String sUID, sMyPPLink, sMyUsername, sPPLink, sUsername;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private AnimatedRecyclerView ARV;
    private GifImageView Message_Sent_GIF;
    private AlertDialog MessageDialog;


    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {

        public TextView Desp_TV, DESP_TITLE;
        public AutofitTextView ATV_SERIAL_NO, ATV_SEND;
        public EditText ET_LINE;


        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);


            Desp_TV = itemView.findViewById(R.id.Desp_TV);
            ET_LINE = itemView.findViewById(R.id.ET_ML1);
            DESP_TITLE = itemView.findViewById(R.id.DESP_TITLE);
            ATV_SERIAL_NO = itemView.findViewById(R.id.ATV_SERIAL_NO);
            ATV_SEND = itemView.findViewById(R.id.IV_GO_TO_ANCHOR);


        }

    }

    public DespAndQARecyclerViewAdapter(ArrayList<String> DespList, ArrayList<String> QuesList, Context context, boolean smallText, String UID, String MyPPLink, String PPLink,
                                        String MyUsername, String Username, AnimatedRecyclerView rv,
                                        AlertDialog alertDialog, GifImageView gifImageView) {

        alsDespList = DespList;
        alsQuesList = QuesList;
        mContext = context;
        isSmallText = smallText;
        sUID = UID;
        sMyPPLink = MyPPLink;
        sPPLink = PPLink;
        sMyUsername = MyUsername;
        sUsername = Username;
        mAuth = FirebaseAuth.getInstance();
        ARV = rv;
        MessageDialog = alertDialog;
        Message_Sent_GIF = gifImageView;
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (isSmallText) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_single_text, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_desp_qa, parent, false);
        }
        return new MainFeedViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {


        holder.setIsRecyclable(false);

        if (isSmallText)
            holder.ATV_SERIAL_NO.setText((position + 1) + ".");


        if (isSmallText) {
            holder.ET_LINE.setText(alsDespList.get(position));
        } else {
            holder.Desp_TV.setText(Html.fromHtml(alsDespList.get(position), Html.FROM_HTML_MODE_COMPACT));
            holder.DESP_TITLE.setText(Html.fromHtml(alsQuesList.get(position), Html.FROM_HTML_MODE_COMPACT));
        }

        if (isSmallText) {
            holder.ATV_SEND.setOnClickListener(v -> {
                ARV.setVisibility(View.GONE);
                Message_Sent_GIF.setVisibility(View.VISIBLE);
                GifDrawable drawable = ((GifDrawable) Message_Sent_GIF.getDrawable());
                drawable.setLoopCount(1);
                drawable.addAnimationListener(i -> new Handler(Looper.getMainLooper()).postDelayed(() -> MessageDialog.dismiss(), 1000));
                Message_Sent_GIF.setImageDrawable(drawable);
                FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                firebaseMethods.addFollowOrMessageToFilterList(sUID, sUsername, sMyUsername, sMyPPLink, holder.ET_LINE.getText().toString()
                        , sPPLink, "Type1", mContext.getString(R.string.field_message_list_for_notifications),
                        mContext.getString(R.string.field_message_sent));


            });
        }
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: alsDespList.size() " + alsDespList.size());
        int count;
        if (isSmallText) {
            count = 5 - alsDespList.size();
        } else {
            count = 0;
        }
        return alsDespList.size() + count;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

