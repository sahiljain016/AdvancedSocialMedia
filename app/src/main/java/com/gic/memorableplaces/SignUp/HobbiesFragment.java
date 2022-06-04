package com.gic.memorableplaces.SignUp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class HobbiesFragment extends Fragment {
    private static final String TAG = "HobbiesFragment";
    private static final int MAX_SELECTED_ITEMS = 5;
    private long last_text_edit = 0;

    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false, isDeleteShown = false;

    private ArrayList<String> alsSelectedList, alsMatchSelectedList, alsHobbies;
    private ArrayList<GlideUrl> albSelectedBitmap, albMatchBitmap;
    private Runnable runnable;

    private ImageView IV_PRIVACY;
    private ImageView IV_TICK;
    private ImageView IV_BOX;
    private ImageView IV_ADD_CUSTOM;
    private MotionLayout ML, ML_1, ML_2, ML_3, ML_4, ML_5;

    private GifImageView IV_1, IV_2, IV_3, IV_4, IV_5;
    private TextView TV_1, TV_2, TV_3, TV_4, TV_5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hobbies, container, false);
        mContext = getActivity();
        albMatchBitmap = new ArrayList<>(5);
        albSelectedBitmap = new ArrayList<>(5);
        alsSelectedList = new ArrayList<>(5);
        alsMatchSelectedList = new ArrayList<>(5);
        alsHobbies = new ArrayList<>();

//        alsSelectedList.add(0, "N/A");
//        alsMatchSelectedList.add(0, "N/A");
        ML = view.findViewById(R.id.ML_MAIN_H);

//        Log.d(TAG, "onCreateView: ml: " +  view.findViewById(R.id.ML_MAIN_H));
//        ML_HOBBIES_2.transitionToEnd();
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_H);
        ImageView IV_BG_H = view.findViewById(R.id.IV_BG_H);

        IV_1 = view.findViewById(R.id.IV_1_H);
        IV_2 = view.findViewById(R.id.IV_2_H);
        IV_3 = view.findViewById(R.id.IV_3_H);
        IV_4 = view.findViewById(R.id.IV_4_H);
        IV_5 = view.findViewById(R.id.IV_5_H);
        TV_1 = view.findViewById(R.id.TV_1_H);
        TV_2 = view.findViewById(R.id.TV_2_H);
        TV_3 = view.findViewById(R.id.TV_3_H);
        TV_4 = view.findViewById(R.id.TV_4_H);
        TV_5 = view.findViewById(R.id.TV_5_H);
        ML_1 = view.findViewById(R.id.ML_1_H);
        ML_2 = view.findViewById(R.id.ML_2_H);
        ML_3 = view.findViewById(R.id.ML_3_H);
        ML_4 = view.findViewById(R.id.ML_4_H);
        ML_5 = view.findViewById(R.id.ML_5_H);

        IV_ADD_CUSTOM = view.findViewById(R.id.IV_ADD_CUSTOM_H);

        if (getArguments() != null) {
            alsSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_hobbies)));
            alsMatchSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_match_hobbies))) ;

            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }
        //  alsSelectedList.set(0, "what");
        Log.d(TAG, "SetBackButton: alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: alsMatchSelectList: " + alsMatchSelectedList);
        for (int i = 0; i < 5; i++) {
            albSelectedBitmap.add(new GlideUrl("N/A"));
            albMatchBitmap.add(new GlideUrl("N/A"));
        }
        for (int i = alsSelectedList.size(); i < 5; i++) {
            alsSelectedList.add("N/A");
        }
        for (int i = alsMatchSelectedList.size(); i < 5; i++) {
            alsMatchSelectedList.add("N/A");
        }
        if (!alsSelectedList.get(0).equals("N/A")) {
            TV_1.setText(alsSelectedList.get(0));
            ML_1.setTransition(R.id.TRANS_1_H);
            ML_1.transitionToEnd();
            GetIconFromTextApi(alsSelectedList.get(0), IV_1, 0);
        }
        if (!alsSelectedList.get(1).equals("N/A")) {

            TV_2.setText(alsSelectedList.get(1));
            ML_2.setTransition(R.id.TRANS_2_H);
            ML_2.transitionToEnd();
            GetIconFromTextApi(alsSelectedList.get(1), IV_2, 1);
        }
        if (!alsSelectedList.get(2).equals("N/A")) {

            TV_3.setText(alsSelectedList.get(2));
            ML_3.setTransition(R.id.TRANS_3_H);
            ML_3.transitionToEnd();
            GetIconFromTextApi(alsSelectedList.get(2), IV_3, 2);
        }
        if (!alsSelectedList.get(3).equals("N/A")) {

            TV_4.setText(alsSelectedList.get(3));
            ML_4.setTransition(R.id.TRANS_4_H);
            ML_4.transitionToEnd();
            GetIconFromTextApi(alsSelectedList.get(3), IV_4, 3);
        }
        if (!alsSelectedList.get(4).equals("N/A")) {

            TV_5.setText(alsSelectedList.get(4));
            ML_5.setTransition(R.id.TRANS_5_H);
            ML_5.transitionToEnd();
            GetIconFromTextApi(alsSelectedList.get(4), IV_5, 4);
        }

        Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);

        SetTypeFaces(view);
        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        SameDetailBox(view);
        SwitchInput(view);
        SetBackButton(view);
        GetHobbiesFromAPI(view);
        IV_ADD_CUSTOM.setOnClickListener(v -> runnable.run());


        ML_1.setOnLongClickListener(v -> {
            DeleteSelectedHobby(TV_1.getText().toString(), 0);
            return false;
        });
        ML_2.setOnLongClickListener(v -> {
            DeleteSelectedHobby(TV_2.getText().toString(), 1);
            return false;
        });
        ML_3.setOnLongClickListener(v -> {
            DeleteSelectedHobby(TV_3.getText().toString(), 2);
            return false;
        });
        ML_4.setOnLongClickListener(v -> {
            DeleteSelectedHobby(TV_4.getText().toString(), 3);
            return false;
        });
        ML_5.setOnLongClickListener(v -> {
            DeleteSelectedHobby(TV_5.getText().toString(), 4);
            return false;
        });

        return view;
    }

    private void GetHobbiesFromAPI(View view) {
        AutoCompleteTextView ACTV = view.findViewById(R.id.ACTV_SEARCH_H);

        final long delay = 1000;
        final Handler handler = new Handler();

        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here

                if (!TextUtils.isEmpty(ACTV.getText().toString())) {

                    if (isSwitched)
                        InflateDropDown(ACTV, alsMatchSelectedList);
                    else
                        InflateDropDown(ACTV, alsSelectedList);
                } else {
                    if (IV_ADD_CUSTOM.getTag().equals("visible")) {
                        ML.transitionToStart();
                        IV_ADD_CUSTOM.setTag("gone");
                    }
                }
            }
        };
        try {
            alsHobbies.clear();
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://en.wikipedia.org/w/api.php?"
                            + "format=json"
                            + "&action=query"
                            + "&prop=links"
                            + "&titles=List%20of%20hobbies"
                            + "&pllimit=500")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // Do something when request failed
                    Log.d(TAG, "onFailure: error " + e.getMessage());
                    Log.d(TAG, "Request Failed.");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error : " + response);
                    } else {
                        Log.d(TAG, "Request Successful.");
                    }

                    // Read data in the worker thread
                    final String data = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        String query = jsonObject.getString("query");
                        JSONObject queryObject = new JSONObject(query);
                        String insideQuery = queryObject.getString("pages");
                        JSONObject pagesObject = new JSONObject(insideQuery);
                        String insidePages = pagesObject.getString("31257416");
                        JSONObject numberObject = new JSONObject(insidePages);
                        String insideNumberObjects = numberObject.getString("links");
                        JSONArray arr = new JSONArray(insideNumberObjects);
                        alsHobbies.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject jsonPart = arr.getJSONObject(i);
                            if (!alsHobbies.contains(jsonPart.getString("title"))) {
                                alsHobbies.add(jsonPart.getString("title"));
                            }
                            // Log.d(TAG, "onResponse: Track Name " + jsonPart.getString("title"));
                        }
                        Log.d(TAG, String.format("onResponse: HobbiesList %s", alsHobbies));
                        ACTV.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                                        android.R.layout.simple_dropdown_item_1line, alsHobbies);
                                ACTV.setThreshold(1);
                                ACTV.setAdapter(adapter);
                                ACTV.setOnItemClickListener((parent, view1, position, id) -> {
                                    String selection = ACTV.getText().toString().toLowerCase();

                                    if (IV_ADD_CUSTOM.getTag().equals("visible")) {
                                        ML.transitionToStart();
                                        IV_ADD_CUSTOM.setTag("gone");
                                    }
                                    // Log.d(TAG, "onItemClick: selection " + selection);
                                    if (isSwitched)
                                        ShowDeleteMethod(alsMatchSelectedList, selection);
                                    else
                                        ShowDeleteMethod(alsSelectedList, selection);
                                    handler.removeCallbacks(input_finish_checker);
                                });
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                last_text_edit = System.currentTimeMillis();
                                handler.postDelayed(input_finish_checker, delay);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InflateDropDown(AutoCompleteTextView ACTV, ArrayList<String> als) {

        if (!ACTV.isPopupShowing() && !TextUtils.isEmpty(ACTV.getText().toString())) {
            if (IV_ADD_CUSTOM.getTag().equals("gone")) {
                ML.setTransition(R.id.TRANS_CUSTOM_HOBBY);
                ML.transitionToEnd();
                IV_ADD_CUSTOM.setTag("visible");
            }
            runnable = () -> {
                if (als.contains("N/A")) {

                    String selection = ACTV.getText().toString().toLowerCase();
                    if (!als.contains(selection)) {
                        if (!isDeleteShown) {
                            final Dialog dialog = new Dialog(mContext);
                            dialog.setContentView(R.layout.dialog_selected_year);

                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            final ImageView SelectedBadge = dialog.findViewById(R.id.Selected_Badge);
                            final TextView BadgeText = dialog.findViewById(R.id.badgeText);

                            SelectedBadge.setImageResource(R.drawable.ic_tap);
                            BadgeText.setText("Long Tap on a hobby to delete it!");
                            dialog.show();
                            Handler handler1 = new Handler(Looper.getMainLooper());
                            handler1.postDelayed(dialog::dismiss, 3000);
                            isDeleteShown = true;
                        }
                        AddToList(selection);

                        ACTV.setText("");
                    } else {
                        Toast.makeText(mContext, "You have already typed this hobby!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "You have already selected a maximum of 5 Hobbies!", Toast.LENGTH_SHORT).show();
                }
            };
        }
    }

    private void ShowDeleteMethod(ArrayList<String> als, String selection) {
        Log.d(TAG, "ShowDeleteMethod: ");
        if (als.contains(selection)) {
            //Log.d(TAG, String.format("onItemClick: SelectedHobbies 1 %s", SelectedHobbies));
            Toast.makeText(mContext, "You have already chosen this Hobby!", Toast.LENGTH_SHORT).show();
        } else {
            if (als.contains("N/A")) {
                if (!isDeleteShown) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_selected_year);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final ImageView SelectedBadge = dialog.findViewById(R.id.Selected_Badge);
                    final TextView BadgeText = dialog.findViewById(R.id.badgeText);

                    SelectedBadge.setImageResource(R.drawable.ic_tap);
                    BadgeText.setText("Long Tap on a hobby to delete it!");
                    dialog.show();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(dialog::dismiss, 3000);
                    isDeleteShown = true;
                }

                AddToList(selection);
                // Log.d(TAG, String.format("onItemClick: SelectedHobbies 2 %s", SelectedHobbies));
            } else {
                // Log.d(TAG, String.format("onItemClick: SelectedHobbies 3 %s", SelectedHobbies));

                Toast.makeText(mContext, "You can select a maximum of 5 Hobbies only!", Toast.LENGTH_SHORT).show();
            }

        }
        //Log.d(TAG, String.format("onItemClick: SelectedHobbies 4 %s", SelectedHobbies));


    }


    private void AddToList(String selection) {
        int pos = 0;
        Log.d(TAG, "AddToList: alsm: " + alsMatchSelectedList);
        Log.d(TAG, "AddToList: alsl: " + alsSelectedList);
        if (isSwitched) {
            pos = alsMatchSelectedList.indexOf("N/A");
            alsMatchSelectedList.set(pos, selection);
        } else {
            pos = alsSelectedList.indexOf("N/A");
            alsSelectedList.set(pos, selection);
        }

        Log.d(TAG, "AddToList: after alsm: " + alsMatchSelectedList);
        Log.d(TAG, "AddToList: after alsl: " + alsSelectedList);
        if (pos == 0) {
            TV_1.setText(selection);
            GetIconFromTextApi(selection, IV_1, 0);
            ML_1.setTransition(R.id.TRANS_1_H);
            ML_1.transitionToEnd();

        } else if (pos == 1) {
            TV_2.setText(selection);
            GetIconFromTextApi(selection, IV_2, 1);

            ML_2.setTransition(R.id.TRANS_2_H);
            ML_2.transitionToEnd();
        } else if (pos == 2) {
            TV_3.setText(selection);
            GetIconFromTextApi(selection, IV_3, 2);

            ML_3.setTransition(R.id.TRANS_3_H);
            ML_3.transitionToEnd();
        } else if (pos == 3) {
            TV_4.setText(selection);
            GetIconFromTextApi(selection, IV_4, 3);

            ML_4.setTransition(R.id.TRANS_4_H);
            ML_4.transitionToEnd();
        } else if (pos == 4) {
            TV_5.setText(selection);
            GetIconFromTextApi(selection, IV_5, 4);

            ML_5.setTransition(R.id.TRANS_5_H);
            ML_5.transitionToEnd();
        }

    }

    private void SetTypeFaces(View view) {
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_H);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        //Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setTypeface(tf);
    }

    private void SameDetailBox(View view) {

        IV_BOX = view.findViewById(R.id.IV_BOX_H);
        IV_TICK = view.findViewById(R.id.IV_TICK_H);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                isSameDetail = true;
                alsMatchSelectedList.clear();
                albMatchBitmap.clear();
                Log.d(TAG, "SameDetailBox: albSelectedBitmap: " + albSelectedBitmap);
                for (int i = 0; i < alsSelectedList.size(); i++) {
                    alsMatchSelectedList.add(alsSelectedList.get(i));
                    albMatchBitmap.add(albSelectedBitmap.get(i));
                }
            }
            Log.d(TAG, "SameDetailBox: albMatchBitmap: " + albMatchBitmap);
            Log.d(TAG, "SameDetailBox: albSelectedBitmap: " + albSelectedBitmap);
            if (!alsMatchSelectedList.get(0).equals("N/A")) {
                TV_1.setText(alsMatchSelectedList.get(0));
                Glide.with(mContext)
                        .load(albMatchBitmap.get(0))
                        .into(IV_1);
                ML_1.setTransition(R.id.TRANS_1_H);
                ML_1.transitionToEnd();
            }
            if (!alsMatchSelectedList.get(1).equals("N/A")) {
                TV_2.setText(alsMatchSelectedList.get(1));
                TV_1.setText(alsMatchSelectedList.get(0));
                Glide.with(mContext)
                        .load(albMatchBitmap.get(1))
                        .into(IV_2);
                ML_2.setTransition(R.id.TRANS_2_H);
                ML_2.transitionToEnd();
            }
            if (!alsMatchSelectedList.get(2).equals("N/A")) {
                TV_3.setText(alsMatchSelectedList.get(2));
                Glide.with(mContext)
                        .load(albMatchBitmap.get(2))
                        .into(IV_3);
                ML_3.setTransition(R.id.TRANS_3_H);
                ML_3.transitionToEnd();
            }
            if (!alsMatchSelectedList.get(3).equals("N/A")) {
                TV_4.setText(alsMatchSelectedList.get(3));
                Glide.with(mContext)
                        .load(albMatchBitmap.get(3))
                        .into(IV_4);
                ML_4.setTransition(R.id.TRANS_4_H);
                ML_4.transitionToEnd();
            }
            if (!alsMatchSelectedList.get(4).equals("N/A")) {
                TV_5.setText(alsMatchSelectedList.get(4));
                Glide.with(mContext)
                        .load(albMatchBitmap.get(4))
                        .into(IV_5);
                ML_5.setTransition(R.id.TRANS_5_H);
                ML_5.transitionToEnd();
            }

        });


    }

    private void SwitchInput(View view) {
        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_H);
        View V_WHITE_BLUR = view.findViewById(R.id.V_WHITE_BLUR);
        TextView TV_NOTICE = view.findViewById(R.id.TV_SWITCH_NOTICE);
        TextView TV_SAME_DETAIL = view.findViewById(R.id.TV_SWITCH_SAME_DETAIL);
        IV_SWITCH_INPUT.setOnClickListener(v -> {

            ML_1.setTransition(R.id.TRANS_1_H);
            ML_1.transitionToStart();
            ML_2.setTransition(R.id.TRANS_2_H);
            ML_2.transitionToStart();
            ML_3.setTransition(R.id.TRANS_3_H);
            ML_3.transitionToStart();
            ML_4.setTransition(R.id.TRANS_4_H);
            ML_4.transitionToStart();
            ML_5.setTransition(R.id.TRANS_5_H);
            ML_5.transitionToStart();
            if (!isSwitched) {
                boolean temp = false;
                for (GlideUrl glideUrl : albMatchBitmap) {
                    if (!glideUrl.toString().equals("N/A")) {
                        temp = true;
                        break;
                    }
                }
                if (!temp) {
                    if (!alsMatchSelectedList.get(0).equals("N/A")) {
                        TV_1.setText(alsMatchSelectedList.get(0));
                        GetIconFromTextApi(alsMatchSelectedList.get(0), IV_1, 0);
                        ML_1.setTransition(R.id.TRANS_1_H);
                        ML_1.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(1).equals("N/A")) {
                        GetIconFromTextApi(alsMatchSelectedList.get(1), IV_2, 1);

                        TV_2.setText(alsMatchSelectedList.get(1));
                        ML_2.setTransition(R.id.TRANS_2_H);
                        ML_2.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(2).equals("N/A")) {
                        GetIconFromTextApi(alsMatchSelectedList.get(2), IV_3, 2);

                        TV_3.setText(alsMatchSelectedList.get(2));
                        ML_3.setTransition(R.id.TRANS_3_H);
                        ML_3.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(3).equals("N/A")) {
                        GetIconFromTextApi(alsMatchSelectedList.get(3), IV_4, 3);

                        TV_4.setText(alsMatchSelectedList.get(3));
                        ML_4.setTransition(R.id.TRANS_4_H);
                        ML_4.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(4).equals("N/A")) {
                        GetIconFromTextApi(alsMatchSelectedList.get(4), IV_5, 4);

                        TV_5.setText(alsMatchSelectedList.get(4));
                        ML_5.setTransition(R.id.TRANS_5_H);
                        ML_5.transitionToEnd();
                    }
                } else {
                    if (!alsMatchSelectedList.get(0).equals("N/A")) {
                        TV_1.setText(alsMatchSelectedList.get(0));
                        Glide.with(mContext)
                                .load(albMatchBitmap.get(0))
                                .into(IV_1);
                        ML_1.setTransition(R.id.TRANS_1_H);
                        ML_1.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(1).equals("N/A")) {
                        TV_2.setText(alsMatchSelectedList.get(1));
                        Glide.with(mContext)
                                .load(albMatchBitmap.get(1))
                                .into(IV_2);
                        ML_2.setTransition(R.id.TRANS_2_H);
                        ML_2.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(2).equals("N/A")) {
                        TV_3.setText(alsMatchSelectedList.get(2));
                        Glide.with(mContext)
                                .load(albMatchBitmap.get(2))
                                .into(IV_3);
                        ML_3.setTransition(R.id.TRANS_3_H);
                        ML_3.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(3).equals("N/A")) {
                        TV_4.setText(alsMatchSelectedList.get(3));
                        Glide.with(mContext)
                                .load(albMatchBitmap.get(3))
                                .into(IV_4);
                        ML_4.setTransition(R.id.TRANS_4_H);
                        ML_4.transitionToEnd();
                    }
                    if (!alsMatchSelectedList.get(4).equals("N/A")) {
                        TV_5.setText(alsMatchSelectedList.get(4));
                        Glide.with(mContext)
                                .load(albMatchBitmap.get(4))
                                .into(IV_5);
                        ML_5.setTransition(R.id.TRANS_5_H);
                        ML_5.transitionToEnd();
                    }
                }

                ML.setBackgroundColor(Color.parseColor("#212121"));
                V_WHITE_BLUR.setBackgroundResource(R.drawable.gradient_black);
                ML.setTransition(R.id.TRANS_HOBBIES_MAIN);
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                isSwitched = true;
                TV_NOTICE.setText("Please enter up to 5 hobbies, these will be added to the profile of your ideal match.");
                IV_BOX.setVisibility(View.VISIBLE);
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.VISIBLE);
                } else {
                    IV_TICK.setVisibility(View.GONE);
                }
                TV_SAME_DETAIL.setVisibility(View.VISIBLE);
                TV_NOTICE.setTextColor(Color.parseColor("#FFFFFF"));


            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                V_WHITE_BLUR.setBackgroundResource(R.drawable.gradient_white);
                ML.setTransition(R.id.TRANS_HOBBIES_MAIN);
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                isSwitched = false;
                TV_NOTICE.setText("Please enter up to 5 hobbies, people will be able to filter you based upon these hobbies.");
                IV_BOX.setVisibility(View.GONE);
                IV_TICK.setVisibility(View.GONE);
                TV_SAME_DETAIL.setVisibility(View.GONE);
                TV_NOTICE.setTextColor(Color.parseColor("#000000"));

                if (!alsSelectedList.get(0).equals("N/A")) {
                    TV_1.setText(alsSelectedList.get(0));
                    Glide.with(mContext)
                            .load(albMatchBitmap.get(0))
                            .into(IV_1);
                    ML_1.setTransition(R.id.TRANS_1_H);
                    ML_1.transitionToEnd();
                }
                if (!alsSelectedList.get(1).equals("N/A")) {
                    TV_2.setText(alsSelectedList.get(1));
                    Glide.with(mContext)
                            .load(albMatchBitmap.get(1))
                            .into(IV_2);
                    ML_2.setTransition(R.id.TRANS_2_H);
                    ML_2.transitionToEnd();
                }
                if (!alsSelectedList.get(2).equals("N/A")) {
                    TV_3.setText(alsSelectedList.get(2));
                    Glide.with(mContext)
                            .load(albMatchBitmap.get(2))
                            .into(IV_3);
                    ML_3.setTransition(R.id.TRANS_3_H);
                    ML_3.transitionToEnd();
                }
                if (!alsSelectedList.get(3).equals("N/A")) {
                    TV_4.setText(alsSelectedList.get(3));
                    Glide.with(mContext)
                            .load(albMatchBitmap.get(3))
                            .into(IV_4);
                    ML_4.setTransition(R.id.TRANS_4_H);
                    ML_4.transitionToEnd();
                }
                if (!alsSelectedList.get(4).equals("N/A")) {
                    TV_5.setText(alsSelectedList.get(4));
                    Glide.with(mContext)
                            .load(albMatchBitmap.get(4))
                            .into(IV_5);
                    ML_5.setTransition(R.id.TRANS_5_H);
                    ML_5.transitionToEnd();
                }
            }

        });


    }

    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_H);
        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            boolean isEmpty = true;
            ArrayList<String> Temp = new ArrayList<>(5);
            for (String string : alsSelectedList) {
                if (!string.equals("N/A")) {
                    Temp.add(string);
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                Temp.add("N/A");
            }
            isEmpty = true;
            ArrayList<String> Temp2 = new ArrayList<>(5);
            for (String string : alsMatchSelectedList) {
                if (!string.equals("N/A")) {
                    Temp2.add(string);
                    isEmpty = false;
                }
            }
            if (isEmpty) {
                Temp2.add("N/A");
            }
            bundle.putStringArrayList(mContext.getString(R.string.field_hobbies), Temp);
            bundle.putStringArrayList(mContext.getString(R.string.field_match_hobbies), Temp2);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(HobbiesFragment.this).commit();
        });
    }

    private void SetPrivacyDialog() {
        Dialog dialog = MiscTools.InflateDialog(mContext, R.layout.dialog_privacy_dialog);
        AppCompatButton ACB_LOCK = dialog.findViewById(R.id.ACB_LOCK_PD);
        AppCompatButton ACB_UNLOCK = dialog.findViewById(R.id.ACB_UNLOCK_PD);
        ImageView IV_PRIVACY_ICON = dialog.findViewById(R.id.IV_PRIVACY_ICON_PD);
        ChangeTheme(R.style.MyDetail, IV_PRIVACY_ICON);

        if (ML.getProgress() == 0.0)
            ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        else
            ChangeTheme(R.style.OtherDetail, IV_PRIVACY);

        ACB_LOCK.setOnClickListener(v -> {
            isLocked = true;
            if (ML.getProgress() == 0.0)
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
            else
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
            dialog.cancel();
        });
        ACB_UNLOCK.setOnClickListener(v -> {
            isLocked = false;
            if (ML.getProgress() == 0.0)
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
            else
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
            dialog.cancel();
        });

        IV_PRIVACY.setOnClickListener(v -> {
            dialog.show();
            if (isLocked) {
                ACB_LOCK.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7CB342")));
            } else {
                ACB_LOCK.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E65955")));
            }
            ChangeTheme(R.style.MyDetail, IV_PRIVACY_ICON);

        });

    }

    private void ChangeTheme(int Style, ImageView IV) {
        final ContextThemeWrapper wrapper = new ContextThemeWrapper(mContext, Style);

        Drawable drawable;
        if (isLocked)
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_locked_privacy, wrapper.getTheme());
        else
            drawable = VectorDrawableCompat.create(getResources(), R.drawable.ic_unlocked_privacy, wrapper.getTheme());


        IV.setImageDrawable(drawable);
    }

    /**
     * Method to delete the long particular hobby
     * displays selected hobby in dialog box
     * removes selected hobby from selected list
     * deletes the whole hobby node from the database
     * closes the dialog to triger on dismiss listener
     * which in turn sets the updated list in database
     */
    private void DeleteSelectedHobby(String hobby, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("I don't like " + hobby + ".")
                .setMessage("This will delete " + hobby + " from your selected hobbies.")
                .setPositiveButton("OK",
                        (dialog, whichButton) -> {

                            if (isSwitched) {
                                alsMatchSelectedList.set(pos, "N/A");
                            } else {
                                alsSelectedList.set(pos, "N/A");
                            }
                            Log.d(TAG, "DeleteSelectedHobby: alsSelectedLIst: " + alsSelectedList);
                            if (pos == 0) {
                                TV_1.setText("");
                                IV_1.setImageResource(0);
                                ML_1.setTransition(R.id.TRANS_1_H);
                                ML_1.transitionToStart();
                            } else if (pos == 1) {
                                TV_2.setText("");
                                IV_2.setImageResource(0);
                                ML_2.setTransition(R.id.TRANS_2_H);
                                ML_2.transitionToStart();
                            } else if (pos == 2) {
                                TV_3.setText("");
                                IV_3.setImageResource(0);
                                ML_3.setTransition(R.id.TRANS_3_H);
                                ML_3.transitionToStart();
                            } else if (pos == 3) {
                                TV_4.setText("");
                                IV_4.setImageResource(0);
                                ML_4.setTransition(R.id.TRANS_4_H);
                                ML_4.transitionToStart();
                            } else if (pos == 4) {
                                TV_5.setText("");
                                IV_5.setImageResource(0);
                                ML_5.setTransition(R.id.TRANS_5_H);
                                ML_5.transitionToStart();
                            }


                        }
                )
                .setNegativeButton("Cancel",
                        (dialog, whichButton) -> dialog.dismiss()
                )
                .create();
        builder.show();
    }

    private void GetIconFromTextApi(String query, ImageView IV, int pos) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer 9Snr7WOOkzFwyepB848K9NFRPZeRDlVomiypoiuOuHgSyLYgSyDAIhIOpXArdh43")
                .url("https://api.iconfinder.com/v4/icons/"
                        + "search?query="
                        + query
                        + "&count=1"
                        + "&style=filled-outline"
                        + "&premium=0"
                        + "&vector=1")
                .get()
                .build();

        Log.d(TAG, "onCreateView: request: " + request.url().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Do something when request failed
                Log.d(TAG, "onFailure: error " + e.getMessage());
                Log.d(TAG, "Request Failed.");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws
                    IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Error : " + response);
                } else {
                    Log.d(TAG, "Request Successful.");
                }

                // Read data in the worker thread
                final String data = response.body().string();
                Log.d(TAG, "onResponse: data: " + data);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(data);
                    JSONArray icon_array = jsonObject.getJSONArray("icons");
                    String icons = icon_array.getString(0);
                    JSONObject jsoIcons = new JSONObject(icons);
                    JSONArray vector_sizes = jsoIcons.getJSONArray("vector_sizes");

                    Log.d(TAG, "onResponse: url: " + vector_sizes.getString(0));
                    JSONObject formats;
//                    if (vector_sizes.length() > 2) {
//                        formats = new JSONObject(vector_sizes.getString(1));
//                    } else {
                    formats = new JSONObject(vector_sizes.getString(0));
                    //}
                    JSONArray jsonArray = formats.getJSONArray("formats");

                    String one = jsonArray.getString(0);
                    JSONObject jsonObject1 = new JSONObject(one);
                    Handler handler = new Handler(Looper.getMainLooper());
                    String url = jsonObject1.getString("download_url");
                    handler.post(() -> {


                        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                                .addHeader("accept", "application/json")
                                .addHeader("Authorization", "Bearer 9Snr7WOOkzFwyepB848K9NFRPZeRDlVomiypoiuOuHgSyLYgSyDAIhIOpXArdh43")
                                .build());

                        Glide.with(mContext)
                                .load(glideUrl)
                                .into(IV);

                        if (isSwitched) {
                            albMatchBitmap.set(pos, glideUrl);
                        } else {
                            albSelectedBitmap.set(pos, glideUrl);
                        }


                        //  Uri uri = Uri.parse(glideUrl.toStringUrl());

//                        Log.d(TAG, "onResponse: uri: " + uri);
//                        Request request1 = new Request.Builder()
//                                .addHeader("accept", "application/json")
//                                .addHeader("Authorization", "Bearer 9Snr7WOOkzFwyepB848K9NFRPZeRDlVomiypoiuOuHgSyLYgSyDAIhIOpXArdh43")
//                                .url(url)
//                                .build();
//
//                        GlideToVectorYou.init().with(mContext).load(Uri.parse(request1.toString()), IV_1);

                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
