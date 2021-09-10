package com.gic.memorableplaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gic.memorableplaces.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;

import static com.gic.memorableplaces.FilterFriends.FriendsFilterActivity.hmMyValues;

public class CardDetailsRecyclerViewAdapter extends RecyclerView.Adapter<CardDetailsRecyclerViewAdapter.MainFeedViewHolder> {
    private static final String TAG = "FindFriendsRecyclerViewAdapter";
    //Variables
    private final HashMap<String, ArrayList<String>> hmFinal;
    private final ArrayList<String> alsLeftFields;
    private final ArrayList<String> alsRightFields;

    private final Context mContext;
    private final DatabaseReference myRef;
    private final FirebaseAuth mAuth;

    public static class MainFeedViewHolder extends RecyclerView.ViewHolder {


        public ImageView IV_MAIN_LEFT, IV_TICK_LEFT, IV_MAIN_RIGHT, IV_TICK_RIGHT, IV_LOCK_RIGHT, IV_LOCK_LEFT;
        public AutofitTextView TV_CONTENT_LEFT, TV_TICK_MESSAGE_LEFT, TV_CONTENT_RIGHT, TV_TICK_MESSAGE_RIGHT, TV_LOCK_RIGHT, TV_LOCK_LEFT;
        public RelativeLayout RL_LOCK_LEFT, RL_LOCK_RIGHT;


        public MainFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            IV_MAIN_LEFT = itemView.findViewById(R.id.IV_MAIN_LEFT);
            IV_TICK_LEFT = itemView.findViewById(R.id.IV_TICK_LEFT);
            IV_MAIN_RIGHT = itemView.findViewById(R.id.IV_MAIN_RIGHT);
            IV_TICK_RIGHT = itemView.findViewById(R.id.IV_TICK_RIGHT);
            TV_CONTENT_LEFT = itemView.findViewById(R.id.TV_CONTENT_LEFT);
            TV_TICK_MESSAGE_LEFT = itemView.findViewById(R.id.TV_TICK_MESSAGE_LEFT);
            TV_CONTENT_RIGHT = itemView.findViewById(R.id.TV_CONTENT_RIGHT);
            TV_TICK_MESSAGE_RIGHT = itemView.findViewById(R.id.TV_TICK_MESSAGE_RIGHT);
            IV_LOCK_RIGHT = itemView.findViewById(R.id.IV_LOCK_RIGHT);
            IV_LOCK_LEFT = itemView.findViewById(R.id.IV_LOCK_LEFT);
            TV_LOCK_RIGHT = itemView.findViewById(R.id.TV_LOCK_RIGHT);
            TV_LOCK_LEFT = itemView.findViewById(R.id.TV_LOCK_LEFT);
            RL_LOCK_LEFT = itemView.findViewById(R.id.RL_LOCK_LEFT);
            RL_LOCK_RIGHT = itemView.findViewById(R.id.RL_LOCK_RIGHT);
        }

    }


    public CardDetailsRecyclerViewAdapter(HashMap<String, ArrayList<String>> Final, ArrayList<String> LeftFieldsList, ArrayList<String> RightFieldsList,
                                          Context context) {

        hmFinal = Final;
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        alsLeftFields = LeftFieldsList;
        alsRightFields = RightFieldsList;

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


// 1 = (Age,Birthdate); (Gender, Pronouns);
        //2 = (CR,OP); (Games,Top5);
        // 3 = hobbies,
        //4 = books,movie,music,societies
        // 5 = college year,zodiac sign
        Log.d(TAG, String.format("onBindViewHolder: mUIDList: %s", hmFinal));
        Log.d(TAG, String.format("onBindViewHolder: alsLeftFields: %s", alsLeftFields));
        Log.d(TAG, String.format("onBindViewHolder: alsRightFields: %s", alsRightFields));
        if (!hmFinal.isEmpty()) {
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_age))) {
                BuildView(position, holder, "Age\nBirthDate", R.drawable.ic_age_filter, mContext.getString(R.string.field_age)
                        , mContext.getString(R.string.field_birth_date), 1, "Age: ",
                        "BirthDate: ", true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_age))) {
                BuildView(position, holder, "Age\nBirthDate", R.drawable.ic_age_filter, mContext.getString(R.string.field_age)
                        , mContext.getString(R.string.field_birth_date), 1, "Age: ",
                        "BirthDate: ", false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_gender))) {
                BuildView(position, holder, "Gender\nPronouns Preferred", R.drawable.ic_gender_symbol,
                        mContext.getString(R.string.field_gender), mContext.getString(R.string.field_pronouns), 1,
                        "Gender: ", "Pronouns Preferred: ", true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_gender))) {
                BuildView(position, holder, "Gender\nPronouns Preferred", R.drawable.ic_gender_symbol,
                        mContext.getString(R.string.field_gender), mContext.getString(R.string.field_pronouns), 1,
                        "Gender: ", "Pronouns Preferred:", false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_class_representative))) {
                BuildView(position, holder, "Class Rep\nOther Positions", R.drawable.ic_cr_card,
                        mContext.getString(R.string.field_class_representative), mContext.getString(R.string.field_other_posts),
                        2, "Class Rep:", "Other Positions:", true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_class_representative))) {
                BuildView(position, holder, "Class Rep\nOther Positions", R.drawable.ic_cr_card,
                        mContext.getString(R.string.field_class_representative), mContext.getString(R.string.field_other_posts),
                        2, "Class Rep: ", "Other Positions: ", false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_games))) {
                BuildView(position, holder, "Games\nTop Gaming Platforms", R.drawable.ic_controller,
                        mContext.getString(R.string.field_games), mContext.getString(R.string.Top5Platform), 2,
                        "Games: ", "Top Gaming Platforms:", true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_games))) {
                BuildView(position, holder, "Games\nTop Gaming Platforms ", R.drawable.ic_controller,
                        mContext.getString(R.string.field_games), mContext.getString(R.string.Top5Platform), 2,
                        "Games: ", "Top Gaming Platforms: ", false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_hobbies))) {
                BuildView(position, holder, "My Hobbies ", R.drawable.ic_hobbies, mContext.getString(R.string.field_hobbies),
                        "", 3, "My Hobbies: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_hobbies))) {
                BuildView(position, holder, "My Hobbies ", R.drawable.ic_hobbies, mContext.getString(R.string.field_hobbies),
                        "", 3, "My Hobbies: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_books))) {
                BuildView(position, holder, "My books ", R.drawable.ic_book, mContext.getString(R.string.field_books),
                        "", 4, "My Books:", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_books))) {
                BuildView(position, holder, "My books ", R.drawable.ic_book, mContext.getString(R.string.field_books),
                        "", 4, "My Books: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_movie))) {
                BuildView(position, holder, "My Movie", R.drawable.ic_movie, mContext.getString(R.string.field_movie),
                        "", 4, "My Movies: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_movie))) {
                BuildView(position, holder, "My Movie", R.drawable.ic_movie, mContext.getString(R.string.field_movie),
                        "", 4, "My Movies: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_music))) {
                BuildView(position, holder, "My Music", R.drawable.ic_music, mContext.getString(R.string.field_music), "",
                        4, "My Music: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_music))) {
                BuildView(position, holder, "My Music", R.drawable.ic_music, mContext.getString(R.string.field_music), "",
                        4, "My Music: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_society))) {
                BuildView(position, holder, "My Societies", R.drawable.ic_society_filter,
                        mContext.getString(R.string.field_society), "", 4,
                        "My Societies: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_society))) {
                BuildView(position, holder, "My Societies", R.drawable.ic_society_filter,
                        mContext.getString(R.string.field_society), "", 4,
                        "My Societies: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_college_year))) {
                BuildView(position, holder, "College Year", R.drawable.ic_university,
                        mContext.getString(R.string.field_college_year), "", 5,
                        "College Year: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_college_year))) {
                BuildView(position, holder, "College Year", R.drawable.ic_university,
                        mContext.getString(R.string.field_college_year), "", 5,
                        "College Year: ", null, false);

            }
            if (alsLeftFields.get(position).equals(mContext.getString(R.string.field_zodiac_sign))) {
                BuildView(position, holder, "Zodiac Sign", R.drawable.ic_zodiac_filter,
                        mContext.getString(R.string.field_zodiac_sign), "", 5,
                        "Zodiac Sign: ", null, true);

            } else if (alsRightFields.get(position).equals(mContext.getString(R.string.field_zodiac_sign))) {
                BuildView(position, holder, "Zodiac Sign", R.drawable.ic_zodiac_filter,
                        mContext.getString(R.string.field_zodiac_sign), "", 5,
                        "Zodiac Sign: ", null, false);

            }
            Log.d(TAG, "onBindViewHolder: position " + position);

        }


    }

    @Override
    public int getItemCount() {
        return alsLeftFields.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void BuildView(int position, MainFeedViewHolder holder, String LockValue, final int icon, String Field1, String Field2,
                           int TypeOfField, String Heading1, String Heading2, boolean isLeft) {
        Log.d(TAG, "BuildView: FIELD 1 AND FIELD 2: " + Field1 + "  " + Field2);


        boolean isSimilar = false;
        StringBuilder stringBuilder = null;
        if (isLeft) {
            Log.d(TAG, "BuildView: alsLeftFields postion: " + alsLeftFields + " - " +alsLeftFields.get(position));
            if (Objects.requireNonNull(hmFinal.get(alsLeftFields.get(position))).contains(mContext.getString(R.string.field_private))) {


                holder.RL_LOCK_LEFT.setVisibility(View.VISIBLE);
                holder.RL_LOCK_LEFT.setBackgroundColor(Color.parseColor("#BA000000"));
                holder.IV_LOCK_LEFT.setImageResource(icon);
                holder.TV_LOCK_LEFT.setText(LockValue);
            } else {
                holder.IV_MAIN_LEFT.setImageResource(icon);

                stringBuilder = BuildString(TypeOfField, Field1, Field2, Heading1, Heading2);

                holder.TV_CONTENT_LEFT.setText(stringBuilder.toString());
                isSimilar = CheckForSimilarity(Field1, Field2, TypeOfField);
                if (isSimilar) {
                    holder.IV_TICK_LEFT.setImageResource(R.drawable.ic_green_tick_round);
                    holder.TV_TICK_MESSAGE_LEFT.setText("Similar Details!");
                } else {
                    holder.IV_TICK_LEFT.setImageResource(R.drawable.ic_cross_red);
                    holder.TV_TICK_MESSAGE_LEFT.setText("Details do not match!");
                }

                final StringBuilder finalStringBuilder = stringBuilder;
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
                            .setText(finalStringBuilder.toString())
                            .setTextColor(Color.WHITE)
                            .setIconDrawable(ContextCompat.getDrawable(mContext, icon))
                            .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)

                            .build();
                    balloon.showAlignTop(v);
                });
            }
        } else {
            Log.d(TAG, "BuildView: alsRightFields postion: " + alsRightFields + " - " +alsRightFields.get(position));
            if (Objects.requireNonNull(hmFinal.get(alsRightFields.get(position))).contains(mContext.getString(R.string.field_private))) {
                holder.RL_LOCK_RIGHT.setVisibility(View.VISIBLE);
                holder.RL_LOCK_RIGHT.setBackgroundColor(Color.parseColor("#BA000000"));
                holder.IV_LOCK_RIGHT.setImageResource(icon);
                holder.TV_LOCK_RIGHT.setText(LockValue);
            } else {
                holder.IV_MAIN_RIGHT.setImageResource(icon);
                stringBuilder = BuildString(TypeOfField, Field1, Field2, Heading1, Heading2);
                holder.TV_CONTENT_RIGHT.setText(stringBuilder.toString());
                isSimilar = CheckForSimilarity(Field1, Field2, TypeOfField);
                if (isSimilar) {
                    holder.IV_TICK_RIGHT.setImageResource(R.drawable.ic_green_tick_round);
                    holder.TV_TICK_MESSAGE_RIGHT.setText("Similar Details!");
                } else {
                    holder.IV_TICK_RIGHT.setImageResource(R.drawable.ic_cross_red);
                    holder.TV_TICK_MESSAGE_RIGHT.setText("Details do not match!");
                }
            }
            final StringBuilder finalStringBuilder1 = stringBuilder;
            holder.TV_CONTENT_RIGHT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                            .setText(finalStringBuilder1.toString())
                            .setTextColor(Color.WHITE)
                            .setIconDrawable(ContextCompat.getDrawable(mContext, icon))
                            .setBackgroundColor(ContextCompat.getColor(mContext, R.color.black))
                            .setBalloonAnimation(BalloonAnimation.CIRCULAR)

                            .build();
                    balloon.showAlignTop(v);
                }
            });
        }


    }

    private StringBuilder BuildString(int TypeOfField, String Field, String Field2, String Heading1, String Heading2) {

// 1 = (Age,Birthdate); (Gender, Pronouns);
        //2 = (CR,OP); (Games,Top5);
        // 3 = hobbies,
        //4 = books,movie,music,societies
        // 5 = college year,zodiac sign
        StringBuilder stringBuilder = new StringBuilder();
        Log.d(TAG, "BuildString: FIELD1 AND FIELD2 : " + Field + " " + Field2);
        if (TypeOfField == 1) {
            Log.d(TAG, String.format("BuildString: Value 1%s", hmFinal.get(Field)));
            Log.d(TAG, String.format("BuildString: Value 1%s", hmFinal.get(Field2)));
            stringBuilder.append(Heading1).append(hmFinal.get(Field).get(0)).append("\n").append(Heading2).append(hmFinal.get(Field2).get(0));
        } else if (TypeOfField == 2) {
            stringBuilder.append(Heading1);
            int count = 1;
            for (String Values1 : hmFinal.get(Field)) {
                Log.d(TAG, "BuildString: Values1 2" + Values1);
                stringBuilder.append("\n").append(count).append(". ").append(Values1);
                count++;

            }
            stringBuilder.append("\n").append(Heading2);
            count = 1;
            for (String Values2 : hmFinal.get(Field2)) {
                Log.d(TAG, "BuildString: Values2 2" + Values2);
                stringBuilder.append("\n").append(count).append(". ").append(Values2);
                count++;

            }
        } else if (TypeOfField == 3 || TypeOfField == 4) {
            stringBuilder.append(Heading1);
            int count = 1;

            for (String Values1 : hmFinal.get(Field)) {
                Log.d(TAG, "BuildString: Values1 3,4" + Values1);
                stringBuilder.append("\n").append(count).append(". ").append(Values1);
                count++;
            }

        } else if (TypeOfField == 5) {
            Log.d(TAG, String.format("BuildString: Value 5: %s", hmFinal.get(Field)));
            stringBuilder.append(Heading1).append(hmFinal.get(Field).get(0));
        }
        return stringBuilder;
    }

    private boolean CheckForSimilarity(final String Field1, final String Field2, final int TypeOfField) {
        // 1 = (Age,Birthdate); (Gender, Pronouns);
        //2 = (CR,OP); (Games,Top5);
        // 3 = hobbies,
        //4 = books,movie,music,societies
        // 5 = college year,zodiac sign
        Log.d(TAG, String.format("CheckForSimilarity: hmMyValues: %s", hmMyValues));
        Log.d(TAG, String.format("CheckForSimilarity: hmFinal:  %s", hmFinal));
        boolean isSimilar = false;
        if (TypeOfField == 1 || TypeOfField == 2) {
            for (String Value : hmFinal.get(Field1)) {
                Log.d(TAG, "CheckForSimilarity: Value: 1 2 " + Value);
                if (hmMyValues.get(Field1).contains(Value)) {
                    isSimilar = true;
                    break;
                }
            }
            if (!isSimilar) {
                for (String Value : hmFinal.get(Field2)) {
                    Log.d(TAG, "CheckForSimilarity: Value2 for 1 2 " + Value);
                    if (hmMyValues.get(Field2).contains(Value)) {
                        isSimilar = true;
                        break;
                    }
                }
            }

        } else {
            for (String Value : hmFinal.get(Field1)) {
                Log.d(TAG, "CheckForSimilarity: Value else " + Value);
                if (hmMyValues.get(Field1).contains(Value)) {
                    Log.d(TAG, "CheckForSimilarity: hmValuee: " + hmFinal.get(Field1));
                    isSimilar = true;
                    break;
                }
            }
        }
        return isSimilar;
    }

    /* if (TypeOfField == 1 || TypeOfField == 2) {
            Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (TypeOfField == 1) {
                        for (DataSnapshot Value : snapshot.getChildren()) {
                            if (Value.getKey().equals(Field1)) {
                                if (!TextUtils.isEmpty(Value.getValue().toString()) || Value.getValue().toString().equals("N/A")) {
                                    isSimilar = false;
                                } else {
                                    isSimilar = ListToBeChecked1.contains(Value.getValue().toString());
                                }
                                return;
                            }
                        }

                    } else if (TypeOfField == 2) {
                        for (DataSnapshot Value : snapshot.getChildren()) {
                            if (Value.getKey().equals(Field1)) {
                                if (!TextUtils.isEmpty(Value.getValue().toString()) || Value.getValue().toString().equals("N/A")) {
                                    isSimilar = false;
                                } else {
                                    isSimilar = ListToBeChecked1.contains(Value.getValue().toString());
                                    return;
                                }
                            }
                            if (Value.getKey().equals(Field2)) {
                                if (!TextUtils.isEmpty(Value.getValue().toString()) || Value.getValue().toString().equals("N/A")) {
                                    isSimilar = false;
                                } else {
                                    isSimilar = ListToBeChecked2.contains(Value.getValue().toString());
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Query query = myRef.child(mContext.getString(R.string.dbname_user_card))
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mContext.getString(R.string.field))
                    .child(Field1);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (TypeOfField == 3) {
                        for (DataSnapshot Values : snapshot.getChildren()) {
                            if (ListToBeChecked1.contains(Values.getValue().toString())) {
                                isSimilar = true;
                                return;
                            } else {
                                isSimilar = false;
                            }
                        }
                    } else if (TypeOfField == 4) {
                        for (DataSnapshot Children : snapshot.getChildren()) {
                            for (DataSnapshot Values : Children.getChildren()) {
                                if (Values.getKey().contains("name")) {
                                    if (ListToBeChecked1.contains(Values.getValue().toString())) {
                                        isSimilar = true;
                                        return;
                                    } else {
                                        isSimilar = false;
                                    }
                                }
                            }
                        }
                    } else if (TypeOfField == 5) {
                        isSimilar = ListToBeChecked1.contains(snapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

}

