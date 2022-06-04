package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.grantland.widget.AutofitTextView;

public class CardDetailsRecyclerViewAdapter extends RecyclerView.Adapter<CardDetailsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";
    //Variables
    private final MatchFilterDetails mfd;
    private final FilterDetails fd;
    private final FilterPrivacyDetails fpd;

    private final ArrayList<ArrayList<String>> alsAllDetails;

    private final Context mContext;
    private final DatabaseReference myRef;
    private final FirebaseAuth mAuth;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {


        public ImageView IV_MAIN_LEFT, IV_TICK_LEFT,  IV_LOCK_LEFT;
        public AutofitTextView TV_TICK_MESSAGE_LEFT;
        public TextView TV_CONTENT_LEFT, TV_LOCK_LEFT;
        public ConstraintLayout CL_CONTENT, CL_PRIVACY_CONTENT;


        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            IV_MAIN_LEFT = itemView.findViewById(R.id.IV_ICON_LEFT);
            IV_TICK_LEFT = itemView.findViewById(R.id.IV_TICK_LEFT);
            TV_CONTENT_LEFT = itemView.findViewById(R.id.TV_CONTENT_LEFT);
            TV_TICK_MESSAGE_LEFT = itemView.findViewById(R.id.TV_TICK_MESSAGE_LEFT);
            IV_LOCK_LEFT = itemView.findViewById(R.id.IV_LOCK_LEFT);
            TV_LOCK_LEFT = itemView.findViewById(R.id.TV_LOCK_LEFT);
            CL_CONTENT = itemView.findViewById(R.id.CL_CONTENT_DDF);
            CL_PRIVACY_CONTENT = itemView.findViewById(R.id.CL_PRIVACY_CONTENT_DDF);
        }

    }


    public CardDetailsRecyclerViewAdapter(ArrayList<ArrayList<String>> Final, FilterDetails fd, MatchFilterDetails mfd, FilterPrivacyDetails fpd,
                                          Context context) {

        //hmFinal = Final;
        this.fd = fd;
        this.mfd = mfd;
        this.fpd = fpd;
        mContext = context;
        alsAllDetails = Final;
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public MainFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_details_filter, parent, false);
        return new MainFeedViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull final MainFeedViewHolder holder, final int position) {
        holder.setIsRecyclable(false);


       // Log.d(TAG, String.format("onBindViewHolder: alsAllDetails Position: %s", alsAllDetails.get(position)));
      //  Log.d(TAG, String.format("onBindViewHolder: pos: %s", position));

        if (fd != null && mfd != null) {
            if (alsAllDetails.get(position).size() > 0) {
                boolean isSimilar = false;
                StringBuilder stringBuilder = null;

                ArrayList<String> alsDetails = alsAllDetails.get(position);

                if (alsDetails.get(1).equals("true")) {


                    holder.CL_PRIVACY_CONTENT.setVisibility(View.VISIBLE);
                    holder.CL_CONTENT.setVisibility(View.GONE);
                    holder.IV_LOCK_LEFT.setImageResource(Integer.parseInt(alsDetails.get(3)));
                    holder.TV_LOCK_LEFT.setText(alsDetails.get(2));
                } else {
                    holder.IV_MAIN_LEFT.setImageResource(Integer.parseInt(alsDetails.get(3)));

                    holder.TV_CONTENT_LEFT.setText(alsDetails.get(2));
                    if (Integer.parseInt(alsDetails.get(0)) != 4) {
                        isSimilar = CheckForSimilarity(alsDetails.get(4), alsDetails.get(5), alsDetails.get(6), alsDetails.get(7), Integer.parseInt(alsDetails.get(0)));
                        if (isSimilar) {
                            holder.IV_TICK_LEFT.setImageResource(R.drawable.ic_green_tick_round);
                            holder.TV_TICK_MESSAGE_LEFT.setText("Details match with your ideal profile!");
                        } else {
                            holder.IV_TICK_LEFT.setImageResource(R.drawable.ic_cross_red);
                            holder.TV_TICK_MESSAGE_LEFT.setText("Details do not match!");
                        }
                    }

                    holder.TV_CONTENT_LEFT.setOnClickListener(v -> {
                        final Balloon balloon = new Balloon.Builder(mContext)
                                .setArrowSize(10)
                                .setArrowOrientation(ArrowOrientation.TOP)
                                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                                .setArrowPosition(0.5f)
                                .setWidth(BalloonSizeSpec.WRAP)
                                .setHeight(BalloonSizeSpec.WRAP)
                                .setTextSize(18f)
                                .setPadding(10)
                                .setCornerRadius(4f)
                                .setAlpha(0.9f)
                                .setText(alsDetails.get(2))
                                .setTextColor(Color.WHITE)
                                .setIconDrawable(ContextCompat.getDrawable(mContext, Integer.parseInt(alsDetails.get(3))))
                                .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                                .build();
                        balloon.showAlignTop(v);
                    });
                }
            }

          //  Log.d(TAG, "onBindViewHolder: position " + position);

        }


    }

    @Override
    public int getItemCount() {
        return alsAllDetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void BuildView(int position, MainFeedViewHolder holder, ArrayList<String> alsDetails, boolean isLeft) {
      //  Log.d(TAG, "BuildView: alsDetails: " + alsDetails);


    }

    private boolean CheckForSimilarity(final String Field1, final String Field2, String MatchResult1, String MatchResult2, final int TypeOfField) {
        // 1 = (Age,Birthdate); (Gender, Pronouns);
        //2 = (CR,OP); (Games,Top5);
        // 3 = hobbies,
        //4 = books,movie,music,societies
        // 5 = college year,zodiac sign
        //Log.d(TAG, String.format("CheckForSimilarity: hmMyValues: %s", hmMyValues));
        //Log.d(TAG, String.format("CheckForSimilarity: hmFinal:  %s", hmFinal));
        boolean isSimilar = false;
        if (TypeOfField == 0) {
            int minAge = Integer.parseInt(MatchResult1.substring(0, MatchResult1.indexOf("-")));
            int maxAge = Integer.parseInt(MatchResult1.substring(MatchResult1.indexOf("-") + 1));
            Log.d(TAG, "CheckForSimilarity: minAge  = " + minAge);
            Log.d(TAG, "CheckForSimilarity: maxAge  = " + maxAge);
            long age = Long.parseLong(Field1);
            if ((age > minAge && age < maxAge) || (age == minAge || age == maxAge)) {
                isSimilar = true;
            }
        } else if (TypeOfField == 1) {
            ArrayList<String> alsTemp;
            Gson gson = new Gson();
            alsTemp = gson.fromJson(MatchResult1, new TypeToken<List<String>>() {
            }.getType());
            if (alsTemp.contains(Field1)) {
                isSimilar = true;
            }
        } else if (TypeOfField == 2) {
            ArrayList<String> alsMatchTemp, alsTemp;
            Gson gson = new Gson();
            alsTemp = gson.fromJson(Field1, new TypeToken<List<String>>() {
            }.getType());
            alsMatchTemp = gson.fromJson(MatchResult1, new TypeToken<List<String>>() {
            }.getType());
            if (GetCommonElements(alsTemp, alsMatchTemp) > 3) {
                isSimilar = true;
            }
        } else if (TypeOfField == 3) {
            ArrayList<String> alsMatchTemp, alsTemp;
            Gson gson = new Gson();
            alsTemp = gson.fromJson(Field1, new TypeToken<List<String>>() {
            }.getType());
            alsMatchTemp = gson.fromJson(MatchResult1, new TypeToken<List<String>>() {
            }.getType());
            if ((GetCommonElements(alsTemp, alsMatchTemp) > 3) || Field2.equals(MatchResult2)) {
                isSimilar = true;
            }
        }

        return isSimilar;
    }

    private <T> int GetCommonElements(
            java.util.Collection<T> a,
            java.util.Collection<T> b
    ) {
        if (a == null && b == null) return 0;
        if (a != null && a.size() == 0) return 0;
        if (b != null && b.size() == 0) return 0;

        Set<T> set = a instanceof HashSet ? (HashSet<T>) a : new HashSet<>(a);
        return (int) b.stream().filter(set::contains).count();
    }


}

