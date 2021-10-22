package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.Adapters.ResultMMBRecyclerViewAdapter;
import com.gic.memorableplaces.Adapters.SelectedMMBRVAdapter;
import com.gic.memorableplaces.CustomLibs.AnimatedRecyclerView.AnimatedRecyclerView;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout;

public class MusicMoviesBookFragment extends Fragment implements ResultMMBRecyclerViewAdapter.OnMMBResultsClickListener {
    private static final String TAG = "NewMusicMoviesBookFragment";
    private long last_text_edit = 0;
    private Context mContext;
    private boolean isLocked = false, isSwitched = false, isSameDetail = false;
    private String sType, sName, QUERY_TYPE = "movie", MOVIE_NAME_KEYWORD = "title";


    private SelectedMMBRVAdapter smrva;
    private ResultMMBRecyclerViewAdapter.OnMMBResultsClickListener omrcl;
    private ArrayList<String> alsSelectedList, alsMatchSelectedList, alsLeftResultsList, alsRightResultsList;

    private ImageView IV_PRIVACY, IV_NO_RESULT;
    private ImageView IV_TICK;
    private AutofitTextView ATV_SHOW_SELECTED;
    private MotionLayout ML, ML_2;
    private EditText ET_SEARCH;
    private TextView TV_NO_RESULT;
    private RecyclerView RV_SELECTED;
    private GifImageView GIV_LOADING;
    private ResultMMBRecyclerViewAdapter rmrva;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mmb, container, false);
        mContext = getActivity();
        alsSelectedList = new ArrayList<>(5);
        alsMatchSelectedList = new ArrayList<>(5);
        alsLeftResultsList = new ArrayList<>();
        alsRightResultsList = new ArrayList<>();
        omrcl = this;
        ML = view.findViewById(R.id.ML_MAIN_MMB);
        ML_2 = view.findViewById(R.id.ML_2_MMB);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_MMB);
        RV_SELECTED = view.findViewById(R.id.RV_SELECTED_MMB);
        ET_SEARCH = view.findViewById(R.id.ET_SEARCH_MMB);
        ATV_SHOW_SELECTED = view.findViewById(R.id.ATV_VIEW_MMB);
        ImageView IV_QUES = view.findViewById(R.id.IV_QUES_MMB);
        GIV_LOADING = view.findViewById(R.id.GIV_LOADING_MMB);
        IV_NO_RESULT = view.findViewById(R.id.IV_NO_RESULT_MMB);
        TV_NO_RESULT = view.findViewById(R.id.TV_NO_RESULTS_MMB);


        if (getArguments() != null) {
            if (getArguments().containsKey(mContext.getString(R.string.field_music))) {
                alsSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_music));
                alsMatchSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_match_music));
                sType = mContext.getString(R.string.field_music);
                sName = "track";
                ET_SEARCH.setHint("Enter song name.....");
            } else if (getArguments().containsKey(mContext.getString(R.string.field_movie))) {
                alsSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_movie));
                alsMatchSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_match_movie));
                sType = mContext.getString(R.string.field_movie);
                sName = "movie";
                ET_SEARCH.setHint("Enter movie/Tv Show name.....");
            } else if (getArguments().containsKey(mContext.getString(R.string.field_books))) {
                alsSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_books));
                alsMatchSelectedList = getArguments().getStringArrayList(mContext.getString(R.string.field_match_books));
                sType = mContext.getString(R.string.field_books);
                sName = "book";
                ET_SEARCH.setHint("Enter book name.....");
            }

            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));

        }

        TV_NO_RESULT.setText("Looks Empty?\nSearch a " + sName + " to add to your list or click view to see previously added " + sName + "s");

        ZoomRecyclerLayout mLayoutManager = new ZoomRecyclerLayout(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_SELECTED.setLayoutManager(mLayoutManager);
        Log.d(TAG, "SetBackButton: alsSelectedList: " + alsSelectedList);
        Log.d(TAG, "SetBackButton: alsMatchSelectList: " + alsMatchSelectedList);
        if (alsSelectedList.contains("N/A")) {
            alsSelectedList.clear();
            SetSelectedRecyclerView();
        } else {
            GIV_LOADING.setVisibility(View.VISIBLE);
            ML_2.setTransition(R.id.TRANS_MMB);
            ML_2.transitionToEnd();
            IV_NO_RESULT.animate().alpha(0).setDuration(1000).start();
            TV_NO_RESULT.animate().alpha(0).setDuration(1000).start();
            if (sType.equals(mContext.getString(R.string.field_music)))
                for (int i = 0; i < alsSelectedList.size(); i++)
                    GetMusicFromID(alsSelectedList.get(i), true, (i + 1) == alsSelectedList.size(), i);
            else if (sType.equals(mContext.getString(R.string.field_movie)))
                for (int i = 0; i < alsSelectedList.size(); i++) {
                    String query = alsSelectedList.get(i);
                    GetMovieFromID(query.substring(0, query.indexOf("$4$")), query.substring(query.indexOf("$4$") + 3), true, (i + 1) == alsSelectedList.size(), i);
                }
            else if (sType.equals(mContext.getString(R.string.field_books)))
                for (int i = 0; i < alsSelectedList.size(); i++)
                    GetBooksFromID(alsSelectedList.get(i), true, (i + 1) == alsSelectedList.size(), i);

        }
        if (alsMatchSelectedList.contains("N/A")) {
            alsMatchSelectedList.clear();
        } else {
            if (sType.equals(mContext.getString(R.string.field_music)))
                for (int i = 0; i < alsMatchSelectedList.size(); i++)
                    GetMusicFromID(alsMatchSelectedList.get(i), false, (i + 1) == alsMatchSelectedList.size(), i);
            else if (sType.equals(mContext.getString(R.string.field_movie)))
                for (int i = 0; i < alsMatchSelectedList.size(); i++) {
                    String query = alsMatchSelectedList.get(i);
                    GetMovieFromID(query.substring(0, query.indexOf("$4$")), query.substring(query.indexOf("$4$") + 3), false, (i + 1) == alsMatchSelectedList.size(), i);
                }
            else if (sType.equals(mContext.getString(R.string.field_books)))
                for (int i = 0; i < alsMatchSelectedList.size(); i++)
                    GetBooksFromID(alsMatchSelectedList.get(i), false, (i + 1) == alsMatchSelectedList.size(), i);
        }


        SetTypeFaces(view);
        SetPrivacyDialog();
        if (sType.equals(mContext.getString(R.string.field_music)))
            GetMusicListFromApi(view);
        else if (sType.equals(mContext.getString(R.string.field_movie)))
            GetMoviesFromAPI(view);
        else if (sType.equals(mContext.getString(R.string.field_books)))
            GetBooksFromApi(view);
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        SameDetailBox(view);
        SwitchInput(view);
        SetBackButton(view);


        ATV_SHOW_SELECTED.setOnClickListener(v -> {
            ML_2.setTransition(R.id.TRANS_MMB);

            if (ML_2.getProgress() == 0.0)
                ML_2.transitionToEnd();
            else
                ML_2.transitionToStart();

            if (isSwitched) {
                Log.d(TAG, "onCreateView: alsSelectedList: " + alsSelectedList);
                if (alsMatchSelectedList.isEmpty()) {
                    IV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                    TV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                } else {

                    IV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                    TV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                }
            } else {
                if (alsSelectedList.isEmpty()) {
                    IV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                    TV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                } else {

                    IV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                    TV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                }
            }

        });

        IV_QUES.setOnClickListener(v -> {
            if (isSwitched) {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 " + sName + "s, they will be added to the profile of your ideal match.", 0, v);

            } else {
                MiscTools.InflateBalloonTooltip(mContext, "Please enter up to 5 " + sName + "s that you like, so that other users can use them as a filter.", 0, v);

            }
        });


        return view;
    }

    private void SetSelectedRecyclerView() {
        if (isSwitched) {
            smrva = new SelectedMMBRVAdapter(alsMatchSelectedList, mContext,
                    sType, position -> {
                alsMatchSelectedList.remove(position);
                smrva.notifyItemRemoved(position);
            });
            RV_SELECTED.setAdapter(smrva);
            smrva.notifyItemRangeInserted(0, alsMatchSelectedList.size());
        } else {

            smrva = new SelectedMMBRVAdapter(alsSelectedList, mContext,
                    sType, position -> {
                alsSelectedList.remove(position);
                smrva.notifyItemRemoved(position);
            });
            RV_SELECTED.setAdapter(smrva);
            smrva.notifyItemRangeInserted(0, alsSelectedList.size());
        }
        RV_SELECTED.scheduleLayoutAnimation();
    }

    private void SetTypeFaces(View view) {
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_MMB);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");
        Typeface cap = Typeface.createFromAsset(mContext.getAssets(), "fonts/Capriola.ttf");

        ATV_TITLE.setText(sType);
        ATV_TITLE.setTypeface(tf);
        ATV_SHOW_SELECTED.setTypeface(cap);
    }

    private void SameDetailBox(View view) {

        ImageView IV_BOX = view.findViewById(R.id.IV_BOX_MMB);
        IV_TICK = view.findViewById(R.id.IV_TICK_MMB);

        IV_BOX.setOnClickListener(v1 -> {

            if (isSameDetail) {
                IV_TICK.setVisibility(View.GONE);
                isSameDetail = false;
                int count = alsMatchSelectedList.size();
                alsMatchSelectedList.clear();
                smrva.notifyItemRangeRemoved(0, count);
            } else {
                IV_TICK.setVisibility(View.VISIBLE);
                isSameDetail = true;
                alsMatchSelectedList.clear();
                alsMatchSelectedList.addAll(alsSelectedList);
                ML_2.setTransition(R.id.TRANS_MMB);
                ML_2.transitionToEnd();
                SetSelectedRecyclerView();
            }

        });


    }

    private void SwitchInput(View view) {
        ImageView IV_SWITCH_INPUT = view.findViewById(R.id.IV_SWITCH_MMB);
        View V_GRADIENT_2 = view.findViewById(R.id.V_GRADIENT_2_MMB);
        View V_GRADIENT = view.findViewById(R.id.V_GRADIENT_MMB);
        TextView TV_SELECTED = view.findViewById(R.id.TV_TITLE_SELCTED_MMB);
        TextView TV_RESULTS = view.findViewById(R.id.TV_TITLE_RESULTS_MMB);

        TV_SELECTED.setText("Selected " + sName + "s: ");
        IV_SWITCH_INPUT.setOnClickListener(v -> {

            ML.setTransition(R.id.TRANS_SWITCH_MMB);
            if (!isSwitched) {
                ML.setBackgroundColor(Color.parseColor("#212121"));
                V_GRADIENT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#212121")));
                V_GRADIENT.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#212121")));
                TV_RESULTS.setTextColor(Color.parseColor("#FFFFFF"));
                TV_SELECTED.setTextColor(Color.parseColor("#FFFFFF"));
                TV_NO_RESULT.setTextColor(Color.parseColor("#FFFFFF"));
                ML.transitionToEnd();
                ChangeTheme(R.style.OtherDetail, IV_PRIVACY);
                isSwitched = true;
                if (isSameDetail) {
                    IV_TICK.setVisibility(View.VISIBLE);
                } else {
                    IV_TICK.setVisibility(View.GONE);
                }

            } else {
                ML.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ML.transitionToStart();
                ChangeTheme(R.style.MyDetail, IV_PRIVACY);
                TV_RESULTS.setTextColor(Color.parseColor("#000000"));
                TV_SELECTED.setTextColor(Color.parseColor("#000000"));
                TV_NO_RESULT.setTextColor(Color.parseColor("#000000"));
                V_GRADIENT.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                V_GRADIENT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                isSwitched = false;
            }
            ML_2.setTransition(R.id.TRANS_MMB);
            ML_2.transitionToStart();
            SetSelectedRecyclerView();

        });


    }

    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_MMB);
        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> temp = new ArrayList<>(5);
            if (alsSelectedList.isEmpty()) {
                temp.add("N/A");
            } else {
                for (String string : alsSelectedList) {
                    temp.add(string.substring(string.indexOf("$3$") + 3));
                }
            }
            bundle.putStringArrayList(sType, temp);
            ArrayList<String> MatchTemp = new ArrayList<>(5);
            if (alsMatchSelectedList.isEmpty()) {
                MatchTemp.add("N/A");
            } else {
                for (String string : alsMatchSelectedList) {
                    MatchTemp.add(string.substring(string.indexOf("$3$") + 3));
                }
            }
            if (sType.equals(mContext.getString(R.string.field_music)))
                bundle.putStringArrayList(mContext.getString(R.string.field_match_music), MatchTemp);
            else if (sType.equals(mContext.getString(R.string.field_movie)))
                bundle.putStringArrayList(mContext.getString(R.string.field_match_movie), MatchTemp);
            else if (sType.equals(mContext.getString(R.string.field_books)))
                bundle.putStringArrayList(mContext.getString(R.string.field_match_books), MatchTemp);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(MusicMoviesBookFragment.this).commit();
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

    private void ShowSelected(ArrayList<String> als, String result, CardView CV) {
        if (als.size() < 5) {
            if (!als.contains(result)) {
                if (isSwitched) {
                    alsMatchSelectedList.add(result);
                    smrva.notifyItemInserted(alsMatchSelectedList.size() - 1);
                } else {
                    alsSelectedList.add(result);
                    smrva.notifyItemInserted(alsSelectedList.size() - 1);
                }
                CV.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bounce));
                Toast.makeText(mContext, sName + " added to your list!", Toast.LENGTH_SHORT).show();
            } else {
                makeMeShake(CV);
                Toast.makeText(mContext, "You have already selected this " + sName, Toast.LENGTH_SHORT).show();
            }
        } else {
            makeMeShake(CV);
//                    Snackbar.make(MainView, "You can only select a maximum of 5 Games!", Snackbar.LENGTH_SHORT);
            Toast.makeText(mContext, "You can only select a maximum of 5 " + sName + "s!", Toast.LENGTH_SHORT).show();

        }

    }

    private void makeMeShake(View view) {
        if (view != null) {
            Animation anim = new TranslateAnimation(-10, 10, 0, 0);
            anim.setDuration(100);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(5);
            view.startAnimation(anim);
        }
    }

    private void GetBooksFromApi(View view) {
        AnimatedRecyclerView RV_RESULTS = view.findViewById(R.id.ARV_RESULTS_MMB);
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                if (!TextUtils.isEmpty(ET_SEARCH.getText().toString())) {
                    try {
                        alsLeftResultsList.clear();
                        alsRightResultsList.clear();
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://www.googleapis.com/books/v1/volumes"
                                        + "?q="
                                        + ET_SEARCH.getText().toString()
                                        + "&key="
                                        + mContext.getString(R.string.The_GOOGLE_BOOKS_API_KEY))
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
                                    String result = jsonObject.getString("items");
                                    JSONArray resultArray = new JSONArray(result);
                                    alsLeftResultsList.clear();
                                    alsRightResultsList.clear();
                                    int limit;
                                    limit = Math.min(resultArray.length(), 10);
                                    for (int i = 0; i < limit; i++) {
                                        JSONObject insideArray = resultArray.getJSONObject(i);
                                        String volumeInfo = insideArray.getString("volumeInfo");

                                        JSONObject volumeInfoObject = new JSONObject(volumeInfo);
                                        StringBuilder stringBuilder = new StringBuilder();
                                        if (!TextUtils.isEmpty((insideArray.getString("id")))) {
                                            if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
                                                stringBuilder.append("N/A");
                                            } else {
                                                stringBuilder.append(volumeInfoObject.getString("title"));
                                            }
                                            stringBuilder.append("$1$");
                                            String authors = null;
                                            if (volumeInfoObject.has("authors")) {
                                                JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                                                authors = authorsArray.getString(0);
                                            } else {
                                                authors = "N/A";
                                            }
                                            if (TextUtils.isEmpty(authors)) {
                                                stringBuilder.append("N/A");
                                            } else {
                                                stringBuilder.append(authors);
                                            }

                                            stringBuilder.append("$2$");

                                            if (volumeInfoObject.has("imageLinks")) {
                                                String ImageLink = volumeInfoObject.getString("imageLinks");
                                                JSONObject imageLinksObject = new JSONObject(ImageLink);
                                                if (TextUtils.isEmpty(imageLinksObject.getString("thumbnail"))) {
                                                    stringBuilder.append("N/A");
                                                } else {
                                                    stringBuilder.append(imageLinksObject.getString("thumbnail"));

                                                }
                                            } else {
                                                stringBuilder.append("N/A");
                                            }
                                            stringBuilder.append("$3$");
                                            stringBuilder.append(insideArray.getString("id"));
                                        }

                                        if ((i % 2) == 0)
                                            alsLeftResultsList.add(stringBuilder.toString());
                                        else
                                            alsRightResultsList.add(stringBuilder.toString());
                                    }
                                    handler.post(() -> {
                                        GIV_LOADING.setVisibility(View.GONE);
                                        rmrva = new ResultMMBRecyclerViewAdapter(mContext, alsLeftResultsList, alsRightResultsList,
                                                mContext.getString(R.string.field_books), omrcl);
                                        RV_RESULTS.setAdapter(rmrva);
                                        rmrva.notifyItemRangeInserted(0, alsLeftResultsList.size());
                                        RV_RESULTS.scheduleLayoutAnimation();

                                    });


                                } catch (Exception e) {

                                    handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                                    e.printStackTrace();
                                }
                                Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                            }
                        });
                    } catch (Exception e) {

                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                }
            }
        };
        ET_SEARCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged: ");
                //lvCircularZoom.setVisibility(View.VISIBLE);
                // lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                if (alsLeftResultsList.size() > 0) {
                    int size = alsLeftResultsList.size();
                    alsRightResultsList.clear();
                    alsLeftResultsList.clear();
                    rmrva.notifyItemRangeRemoved(0, size);
                }

                ML_2.setTransition(R.id.TRANS_MMB);
                if (ML_2.getProgress() == 1.0)
                    ML_2.transitionToStart();

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    GIV_LOADING.setVisibility(View.VISIBLE);
                    TV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                } else {

                    GIV_LOADING.setVisibility(View.GONE);
                    TV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                }
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void GetBooksFromID(String query, boolean isSelected, boolean isLast, int pos) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/books/v1/volumes/"
                            + query)
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
                        JSONObject volumeInfoObject = new JSONObject(jsonObject.getString("volumeInfo"));
                        StringBuilder stringBuilder = new StringBuilder();
                        if (jsonObject.has("id")) {
                            if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
                                stringBuilder.append("N/A");
                            } else {
                                stringBuilder.append(volumeInfoObject.getString("title"));
                            }
                            stringBuilder.append("$1$");
                            String authors = null;
                            if (volumeInfoObject.has("authors")) {
                                JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");
                                authors = authorsArray.getString(0);
                            } else {
                                authors = "N/A";
                            }
                            if (TextUtils.isEmpty(authors)) {
                                stringBuilder.append("N/A");
                            } else {
                                stringBuilder.append(authors);
                            }

                            stringBuilder.append("$2$");

                            if (volumeInfoObject.has("imageLinks")) {
                                String ImageLink = volumeInfoObject.getString("imageLinks");
                                JSONObject imageLinksObject = new JSONObject(ImageLink);
                                if (TextUtils.isEmpty(imageLinksObject.getString("thumbnail"))) {
                                    stringBuilder.append("N/A");
                                } else {
                                    stringBuilder.append(imageLinksObject.getString("thumbnail"));

                                }
                            } else {
                                stringBuilder.append("N/A");
                            }
                            stringBuilder.append("$3$");
                            stringBuilder.append(jsonObject.getString("id"));
                        }

                        if (isSelected) {
                            alsSelectedList.set(pos, stringBuilder.toString());
                        } else {
                            alsMatchSelectedList.set(pos, stringBuilder.toString());
                        }

                        if (isSelected && isLast) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {

                                Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
                                Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);
                                GIV_LOADING.setVisibility(View.GONE);
                                SetSelectedRecyclerView();
                            });
                        }


                    } catch (Exception e) {

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                    Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
        } catch (Exception e) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }

    private void GetMoviesFromAPI(View view) {
        AppCompatButton ACB_MOVIE_TYPE = view.findViewById(R.id.ACB_MOVIE_TYPE);
        AnimatedRecyclerView RV_RESULTS = view.findViewById(R.id.ARV_RESULTS_MMB);

        ACB_MOVIE_TYPE.setVisibility(View.VISIBLE);
        ACB_MOVIE_TYPE.setOnClickListener((View.OnClickListener) v -> {
            if (ACB_MOVIE_TYPE.getTag().equals("movie")) {
                QUERY_TYPE = "tv";
                MOVIE_NAME_KEYWORD = "name";
                ACB_MOVIE_TYPE.setTag("tv");
                ACB_MOVIE_TYPE.setText("Search Movies");
            } else {
                QUERY_TYPE = "movie";
                MOVIE_NAME_KEYWORD = "title";
                ACB_MOVIE_TYPE.setTag("movie");
                ACB_MOVIE_TYPE.setText("Search TV Show");
            }
        });
        Log.d(TAG, "onClick: shaked");
        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    if (!TextUtils.isEmpty(ET_SEARCH.getText().toString())) {
                        try {
                            alsLeftResultsList.clear();
                            alsRightResultsList.clear();
                            OkHttpClient client = new OkHttpClient();

                            Request request = new Request.Builder()
                                    .url("https://api.themoviedb.org/3/search/"
                                            + QUERY_TYPE
                                            + "?api_key="
                                            + mContext.getString(R.string.The_Movie_DB_API_KEY)
                                            + "&query="
                                            + ET_SEARCH.getText().toString()
                                            + "&page=1"
                                            + "&include_adult=true")
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
                                        String result = jsonObject.getString("results");
                                        JSONArray resultArray = new JSONArray(result);
                                        alsLeftResultsList.clear();
                                        alsRightResultsList.clear();
                                        int limit = 0;
                                        limit = Math.min(resultArray.length(), 10);
                                        for (int i = 0; i < limit; i++) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            JSONObject insideArray = resultArray.getJSONObject(i);
                                            if (insideArray.has("id")) {

                                                if (TextUtils.isEmpty(insideArray.getString(MOVIE_NAME_KEYWORD))) {
                                                    stringBuilder.append("N/A");
                                                } else {
                                                    stringBuilder.append(insideArray.getString(MOVIE_NAME_KEYWORD));

                                                }
                                                stringBuilder.append("$1$");
                                                if (TextUtils.isEmpty(insideArray.getString("overview"))) {
                                                    stringBuilder.append("N/A");
                                                } else {
                                                    stringBuilder.append(insideArray.getString("overview"));
                                                }

                                                stringBuilder.append("$2$");
                                                if (!insideArray.has("poster_path")) {
                                                    stringBuilder.append("N/A");
                                                } else {
                                                    stringBuilder.append(mContext.getString(R.string.MOVIE_COVER_CONSTANT_URL)).append(insideArray.getString("poster_path"));
                                                }
                                                stringBuilder.append("$3$");
                                                stringBuilder.append(insideArray.getString("id"));
                                                stringBuilder.append("$4$");
                                                stringBuilder.append(QUERY_TYPE);
                                                // Log.d(TAG, "onResponse: overview " + insideArray.getString("overview"));
                                            }

                                            if ((i % 2) == 0)
                                                alsLeftResultsList.add(stringBuilder.toString());
                                            else
                                                alsRightResultsList.add(stringBuilder.toString());
                                        }
                                        handler.post(() -> {
                                            GIV_LOADING.setVisibility(View.GONE);
                                            rmrva = new ResultMMBRecyclerViewAdapter(mContext, alsLeftResultsList, alsRightResultsList,
                                                    mContext.getString(R.string.field_movie), omrcl);
                                            RV_RESULTS.setAdapter(rmrva);
                                            rmrva.notifyItemRangeInserted(0, alsLeftResultsList.size());
                                            RV_RESULTS.scheduleLayoutAnimation();

                                        });
                                        response.close();
                                    } catch (Exception e) {

                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                                        e.printStackTrace();
                                    }
                                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                                }
                            });
                            // }
//                            }, 1000);


                        } catch (Exception e) {

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        ET_SEARCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged: ");
                //lvCircularZoom.setVisibility(View.VISIBLE);
                // lvCircularZoom.startAnim(500);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                if (alsLeftResultsList.size() > 0) {
                    int size = alsLeftResultsList.size();
                    alsRightResultsList.clear();
                    alsLeftResultsList.clear();
                    rmrva.notifyItemRangeRemoved(0, size);
                }

                ML_2.setTransition(R.id.TRANS_MMB);
                if (ML_2.getProgress() == 1.0)
                    ML_2.transitionToStart();

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    GIV_LOADING.setVisibility(View.VISIBLE);
                    TV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                } else {

                    GIV_LOADING.setVisibility(View.GONE);
                    TV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                }

                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });
    }

    private void GetMovieFromID(String query, String type, boolean isSelected, boolean isLast, int pos) {
        try {
            Log.d(TAG, "GetMovieFromID: type" + type);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/"
                            + type + "/"
                            + query
                            + "?api_key="
                            + mContext.getString(R.string.The_Movie_DB_API_KEY)
                            + "&page=1"
                            + "&include_adult=true")
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
                        StringBuilder stringBuilder = new StringBuilder();
                        if (jsonObject.has("id")) {


                            if (type.equals("movie")) {
                                if (!jsonObject.has("title")) {
                                    stringBuilder.append("N/A");
                                } else {
                                    stringBuilder.append(jsonObject.getString("title"));

                                }
                            } else if (type.equals("tv")) {
                                if (!jsonObject.has("name")) {
                                    stringBuilder.append("N/A");
                                } else {
                                    stringBuilder.append(jsonObject.getString("name"));

                                }
                            }

                            stringBuilder.append("$1$");

                            if (!jsonObject.has("overview")) {
                                stringBuilder.append("N/A");
                            } else {
                                stringBuilder.append(jsonObject.getString("overview"));
                            }
                            stringBuilder.append("$2$");

                            if (!jsonObject.has("poster_path")) {
                                stringBuilder.append("N/A");
                            } else {
                                stringBuilder.append(mContext.getString(R.string.MOVIE_COVER_CONSTANT_URL)).append(jsonObject.getString("poster_path"));
                            }
                            stringBuilder.append("$3$");
                            stringBuilder.append(jsonObject.getString("id"));
                            stringBuilder.append("$4$");
                            stringBuilder.append(type);

                            if (isSelected) {
                                alsSelectedList.set(pos, stringBuilder.toString());
                            } else {
                                alsMatchSelectedList.set(pos, stringBuilder.toString());
                            }
                        }


                        if (isSelected && isLast) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
                                Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);
                                GIV_LOADING.setVisibility(View.GONE);
                                SetSelectedRecyclerView();
                                MOVIE_NAME_KEYWORD = "title";
                            });
                        }
                        response.close();
                    } catch (Exception e) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
            // }
//                            }, 1000);


        } catch (Exception e) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
            e.printStackTrace();
        }
    }

    private void GetMusicListFromApi(View view) {
        AnimatedRecyclerView RV_RESULTS = view.findViewById(R.id.ARV_RESULTS_MMB);

        final long delay = 1000; // 1 seconds after user stops typing

        final Handler handler = new Handler();

        final Runnable input_finish_checker = () -> {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                if (!TextUtils.isEmpty(ET_SEARCH.getText().toString())) {
                    try {
                        alsLeftResultsList.clear();
                        alsRightResultsList.clear();
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://deezerdevs-deezer.p.rapidapi.com/search?q=" + ET_SEARCH.getText().toString())
                                .get()
                                .addHeader("x-rapidapi-key", mContext.getString(R.string.Rapid_MUSIC_API_DEEZER))
                                .addHeader("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com")
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
                                final String data = Objects.requireNonNull(response.body()).string();
                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(data);
                                    String se = jsonObject.getString("data");
                                    JSONArray arr = new JSONArray(se);

                                    alsLeftResultsList.clear();
                                    alsRightResultsList.clear();
                                    int limit;
                                    limit = Math.min(arr.length(), 10);
                                    for (int i = 0; i < limit; i++) {
                                        JSONObject jsonPart = arr.getJSONObject(i);
                                        StringBuilder stringBuilder = new StringBuilder();

                                        if (!TextUtils.isEmpty(jsonPart.getString("id"))) {
                                            if (TextUtils.isEmpty(jsonPart.getString("title"))) {
                                                stringBuilder.append("N/A");
                                            } else {
                                                stringBuilder.append(jsonPart.getString("title"));
                                            }
                                            stringBuilder.append("$1$");
                                            JSONObject jsonObjectNew = new JSONObject(jsonPart.getString("artist"));
                                            if (TextUtils.isEmpty(jsonObjectNew.getString("name"))) {
                                                stringBuilder.append("N/A");
                                            } else {
                                                stringBuilder.append(jsonObjectNew.getString("name"));
                                            }
                                            stringBuilder.append("$2$");
                                            JSONObject jsonPart3 = new JSONObject(jsonPart.getString("album"));
                                            if (TextUtils.isEmpty(jsonPart3.getString("cover_medium"))) {
                                                stringBuilder.append("N/A");
                                            } else {
                                                stringBuilder.append(jsonPart3.getString("cover_medium"));
                                            }
                                            stringBuilder.append("$3$");
                                            stringBuilder.append(jsonPart.getString("id"));
                                        }

                                        if ((i % 2) == 0)
                                            alsLeftResultsList.add(stringBuilder.toString());
                                        else
                                            alsRightResultsList.add(stringBuilder.toString());
                                    }
                                    handler.post(() -> {
                                        GIV_LOADING.setVisibility(View.GONE);
                                        rmrva = new ResultMMBRecyclerViewAdapter(mContext, alsLeftResultsList, alsRightResultsList,
                                                mContext.getString(R.string.field_music), omrcl);
                                        RV_RESULTS.setAdapter(rmrva);
                                        rmrva.notifyItemRangeInserted(0, alsLeftResultsList.size());
                                        RV_RESULTS.scheduleLayoutAnimation();

                                    });
                                    response.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                                    Toast.makeText(mContext, "Error Received! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                                // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                            }
                        });
                    } catch (Exception e) {

                        handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                }
            }
        };
        ET_SEARCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                if (alsLeftResultsList.size() > 0) {
                    int size = alsLeftResultsList.size();
                    alsRightResultsList.clear();
                    alsLeftResultsList.clear();
                    rmrva.notifyItemRangeRemoved(0, size);
                }
                ML_2.setTransition(R.id.TRANS_MMB);
                if (ML_2.getProgress() == 1.0)
                    ML_2.transitionToStart();
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    GIV_LOADING.setVisibility(View.VISIBLE);
                    TV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(0).setDuration(500).start();
                } else {

                    GIV_LOADING.setVisibility(View.GONE);
                    TV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                    IV_NO_RESULT.animate().alpha(1).setDuration(500).start();
                }
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(input_finish_checker, delay);
            }
        });

//
    }


    private void GetMusicFromID(String query, boolean isSelected, boolean isLast, int pos) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://deezerdevs-deezer.p.rapidapi.com/track/" + query)
                .get()
                .addHeader("x-rapidapi-key", mContext.getString(R.string.Rapid_MUSIC_API_DEEZER))
                .addHeader("x-rapidapi-host", "deezerdevs-deezer.p.rapidapi.com")
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
                final String data = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(data);
                    StringBuilder stringBuilder = new StringBuilder();

                    if (!TextUtils.isEmpty(jsonObject.getString("id"))) {
                        if (TextUtils.isEmpty(jsonObject.getString("title"))) {
                            stringBuilder.append("N/A");
                        } else {
                            stringBuilder.append(jsonObject.getString("title"));
                        }
                        stringBuilder.append("$1$");
                        JSONObject jsonObjectNew = new JSONObject(jsonObject.getString("artist"));
                        if (TextUtils.isEmpty(jsonObjectNew.getString("name"))) {
                            stringBuilder.append("N/A");
                        } else {
                            stringBuilder.append(jsonObjectNew.getString("name"));
                        }
                        stringBuilder.append("$2$");
                        JSONObject jsonPart3 = new JSONObject(jsonObject.getString("album"));
                        if (TextUtils.isEmpty(jsonPart3.getString("cover_medium"))) {
                            stringBuilder.append("N/A");
                        } else {
                            stringBuilder.append(jsonPart3.getString("cover_medium"));
                        }
                        stringBuilder.append("$3$");
                        stringBuilder.append(jsonObject.getString("id"));
                        if (isSelected) {
                            alsSelectedList.set(pos, stringBuilder.toString());
                        } else {
                            alsMatchSelectedList.set(pos, stringBuilder.toString());
                        }
                    }

                    if (isSelected && isLast) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {

                            Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);
                            Log.d(TAG, "SetBackButton: a  alsMatchSelectList: " + alsMatchSelectedList);
                            GIV_LOADING.setVisibility(View.GONE);
                            SetSelectedRecyclerView();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> GIV_LOADING.setVisibility(View.GONE));
                    Toast.makeText(mContext, "Error Received! Please try again.", Toast.LENGTH_SHORT).show();
                }
                // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
            }
        });

    }


    @Override
    public void onItemClick(int position, String result, CardView CV) {
        ML_2.setTransition(R.id.TRANS_MMB);
        ML_2.transitionToStart();
        GIV_LOADING.setVisibility(View.GONE);

        if (isSwitched) {
            ShowSelected(alsMatchSelectedList, result, CV);
        } else {
            ShowSelected(alsSelectedList, result, CV);
        }

    }
}
