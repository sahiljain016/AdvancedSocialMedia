package com.gic.memorableplaces.SignUp;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.MiscTools;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class PositionsFragment extends Fragment {
    private static final String TAG = "PositionsFragment";
    private Context mContext;
    private boolean isLocked = false;

    private ArrayList<String> alsSelectedList;

    private ImageView IV_PRIVACY;
    private EditText ET_TITLE_1, ET_ORG_1, ET_TITLE_2, ET_ORG_2, ET_TITLE_3, ET_ORG_3;
    private View V_DOT_1, V_DOT_2, V_DOT_3;
    private ImageFilterView IFV_NEXT, IFV_PREV;
    private RelativeLayout RL_BUTTON;
    private MotionLayout ML, ML_CARDS;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_titles_in_org, container, false);
        mContext = getActivity();

        alsSelectedList = new ArrayList<>(5);

        ML = view.findViewById(R.id.ML_MAIN_TIO);
        IV_PRIVACY = view.findViewById(R.id.IV_PRIVACY_LOCK_TIO);
        RL_BUTTON = view.findViewById(R.id.RL_BUTTON_TOI);
        ML_CARDS = view.findViewById(R.id.ML_CARDS_TIO);
        IFV_PREV = view.findViewById(R.id.IFV_PREV_CARD_TIO);
        IFV_NEXT = view.findViewById(R.id.IFV_NEXT_CARD_TIO);
        ET_TITLE_1 = view.findViewById(R.id.ET_TITLE_1_TIO);
        ET_ORG_1 = view.findViewById(R.id.ET_ORG_NAME_1_TIO);
        ET_TITLE_2 = view.findViewById(R.id.ET_TITLE_2_TIO);
        ET_ORG_2 = view.findViewById(R.id.ET_ORG_NAME_2_TIO);
        ET_TITLE_3 = view.findViewById(R.id.ET_TITLE_3_TIO);
        ET_ORG_3 = view.findViewById(R.id.ET_ORG_NAME_3_TIO);
        V_DOT_1 = view.findViewById(R.id.V_DOT_1_TIO);
        V_DOT_2 = view.findViewById(R.id.V_DOT_2_TIO);
        V_DOT_3 = view.findViewById(R.id.V_DOT_3_TIO);


        if (getArguments() != null) {
            alsSelectedList.addAll(getArguments().getStringArrayList(mContext.getString(R.string.field_titles_posts)));
            isLocked = getArguments().getBoolean(mContext.getString(R.string.field_is_private));
        }

        ShowSpotlights();
        if (!alsSelectedList.contains("N/A")) {
            if (alsSelectedList.size() >= 1) {
                String data = alsSelectedList.get(0);
                ET_TITLE_1.setText(data.substring(0, data.indexOf("$o*")));
                ET_ORG_1.setText(data.substring(data.indexOf("$o*") + 3));
            }
            if (alsSelectedList.size() >= 2) {
                String data = alsSelectedList.get(1);
                ET_TITLE_2.setText(data.substring(0, data.indexOf("$o*")));
                ET_ORG_2.setText(data.substring(data.indexOf("$o*") + 3));
                IFV_NEXT.animate().alpha(1).setDuration(500).start();
                V_DOT_2.animate().alpha(1).setDuration(500).start();
            }
            if (alsSelectedList.size() >= 3) {
                String data = alsSelectedList.get(2);
                ET_TITLE_3.setText(data.substring(0, data.indexOf("$o*")));
                ET_ORG_3.setText(data.substring(data.indexOf("$o*") + 3));
                V_DOT_3.animate().alpha(1).setDuration(500).start();
            }

        }

        Log.d(TAG, "SetBackButton: alsSelectedList: " + alsSelectedList);

        Log.d(TAG, "SetBackButton: a alsSelectedList: " + alsSelectedList);


        SetTypeFaces(view);
        SetPrivacyDialog();
        ChangeTheme(R.style.MyDetail, IV_PRIVACY);
        SetBackButton(view);
        SetCards();
        SetNav();


        return view;
    }

    private void ShowSpotlights() {
        SpotlightConfig spotlightConfig = new SpotlightConfig();

        spotlightConfig.setIntroAnimationDuration(400);
        spotlightConfig.setRevealAnimationEnabled(true);
        spotlightConfig.setPerformClick(true);
        spotlightConfig.setFadingTextDuration(400);
        spotlightConfig.setHeadingTvColor(Color.parseColor("#FFFFFF"));
        spotlightConfig.setHeadingTvSize(32);
        spotlightConfig.setSubHeadingTvColor(Color.parseColor("#DCDCDC"));
        spotlightConfig.setSubHeadingTvSize(16);
        spotlightConfig.setMaskColor(Color.parseColor("#dc000000"));
        spotlightConfig.setLineAnimationDuration(400);
        spotlightConfig.setLineAndArcColor(Color.parseColor("#FFFFD6A5"));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);

        PreferencesManager preferencesManager = new PreferencesManager(mContext);

        if (!preferencesManager.isDisplayed("PRIVACY")) {

            new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                    .headingTvText("Privacy Button")
                    .subHeadingTvText("This button can help you lock this detail on your profile. If Locked, it will only be visible to those who you swipe right.")
                    .target(IV_PRIVACY)
                    .usageId("PRIVACY")
                    .setListener(spotlightViewId -> {
                    }).show();

        }
    }

    private void SetNav() {
        IFV_PREV.setOnClickListener(v -> {
            if (alsSelectedList.size() == 2) {
                if (ML_CARDS.getTag().equals("2")) {
                    ML_CARDS.setTransition(R.id.TRANS_1CL_2RC_3RR);
                    ML_CARDS.transitionToStart();
                    ML_CARDS.setTag("1");
                    IFV_NEXT.animate().alpha(1).setDuration(500).start();
                    IFV_PREV.animate().alpha(0).setDuration(500).start();
                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                }
            } else if (alsSelectedList.size() == 3) {
                if (ML_CARDS.getTag().equals("2")) {
                    ML_CARDS.setTransition(R.id.TRANS_1CL_2RC_3RR);
                    ML_CARDS.transitionToStart();
                    ML_CARDS.setTag("1");
                    IFV_NEXT.animate().alpha(1).setDuration(500).start();
                    IFV_PREV.animate().alpha(0).setDuration(500).start();
                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                } else if (ML_CARDS.getTag().equals("3")) {
                    ML_CARDS.setTransition(R.id.TRANS_1LL_2CL_3RC);
                    ML_CARDS.transitionToStart();
                    ML_CARDS.setTag("2");
                    IFV_NEXT.animate().alpha(1).setDuration(500).start();
                    IFV_PREV.animate().alpha(1).setDuration(500).start();
                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                }
            }
        });

        IFV_NEXT.setOnClickListener(v -> {
            if (alsSelectedList.size() == 2) {
                if (ML_CARDS.getTag().equals("1")) {
                    ML_CARDS.setTransition(R.id.TRANS_1CL_2RC_3RR);
                    ML_CARDS.transitionToEnd();
                    ML_CARDS.setTag("2");
                    IFV_NEXT.animate().alpha(0).setDuration(500).start();
                    IFV_PREV.animate().alpha(1).setDuration(500).start();

                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));

                }
            } else if (alsSelectedList.size() == 3) {
                if (ML_CARDS.getTag().equals("1")) {
                    ML_CARDS.setTransition(R.id.TRANS_1CL_2RC_3RR);
                    ML_CARDS.transitionToEnd();
                    ML_CARDS.setTag("2");
                    IFV_NEXT.animate().alpha(1).setDuration(500).start();
                    IFV_PREV.animate().alpha(1).setDuration(500).start();

                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                } else if (ML_CARDS.getTag().equals("2")) {
                    ML_CARDS.setTransition(R.id.TRANS_1LL_2CL_3RC);
                    ML_CARDS.transitionToEnd();
                    ML_CARDS.setTag("3");
                    IFV_NEXT.animate().alpha(0).setDuration(500).start();
                    IFV_PREV.animate().alpha(1).setDuration(500).start();

                    V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                    V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));

                }
            }
        });
    }

    private void SetCards() {
        RL_BUTTON.setOnClickListener(v -> {
            Log.d(TAG, "SetCards: alsSelectedList size: " + alsSelectedList.size());
            if (alsSelectedList.size() < 3) {
                if (alsSelectedList.size() == 1) {
                    if (TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                        ET_TITLE_1.setError("Enter first title to add more");
                        ET_ORG_1.setError("Enter first organization to add more");
                    } else if (!TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                        ET_TITLE_1.setError("Enter first title to add more");
                    } else if (TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && !TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                        ET_ORG_1.setError("Enter first organization to add more");
                    } else {
                        if (ML_CARDS.getTag().equals("1")) {
                            ML_CARDS.setTransition(R.id.TRANS_1CL_2RC_3RR);
                            ML_CARDS.transitionToEnd();
                            ML_CARDS.setTag("2");
                            IFV_PREV.animate().alpha(1).setDuration(500).start();
                            alsSelectedList.add("N/A");
                            V_DOT_2.animate().alpha(1).setDuration(500).start();
                            V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                            V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                            V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                        }
                    }
                } else if (alsSelectedList.size() == 2) {


                    if (ML_CARDS.getTag().equals("1")) {
                        if (TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                            ET_TITLE_1.setError("Enter first title to add more");
                            ET_ORG_1.setError("Enter first organization to add more");

                        } else if (!TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                            ET_TITLE_1.setError("Enter first title to add more");

                        } else if (TextUtils.isEmpty(ET_TITLE_1.getText().toString()) && !TextUtils.isEmpty(ET_ORG_1.getText().toString())) {
                            ET_ORG_1.setError("Enter first organization to add more");

                        } else {
                            ML_CARDS.setTransition(R.id.TRANS_1CL_2RL_3RC);
                            ML_CARDS.transitionToEnd();
                            ML_CARDS.setTag("3");
                            IFV_NEXT.animate().alpha(0).setDuration(500).start();
                            IFV_PREV.animate().alpha(1).setDuration(500).start();
                            V_DOT_3.animate().alpha(1).setDuration(500).start();
                            V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                            V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                            V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                            alsSelectedList.add("N/A");
                        }

                    } else if (ML_CARDS.getTag().equals("2")) {
                        if (TextUtils.isEmpty(ET_TITLE_2.getText().toString()) && TextUtils.isEmpty(ET_ORG_2.getText().toString())) {
                            ET_TITLE_2.setError("Enter second title to add more");
                            ET_ORG_2.setError("Enter second organization to add more");

                        } else if (!TextUtils.isEmpty(ET_TITLE_2.getText().toString()) && TextUtils.isEmpty(ET_ORG_2.getText().toString())) {
                            ET_TITLE_2.setError("Enter second title to add more");
                        } else if (TextUtils.isEmpty(ET_TITLE_2.getText().toString()) && !TextUtils.isEmpty(ET_ORG_2.getText().toString())) {
                            ET_ORG_2.setError("Enter second organization to add more");
                        } else {
                            ML_CARDS.setTransition(R.id.TRANS_1LL_2CL_3RC);
                            ML_CARDS.transitionToEnd();
                            ML_CARDS.setTag("3");
                            IFV_NEXT.animate().alpha(0).setDuration(500).start();
                            IFV_PREV.animate().alpha(1).setDuration(500).start();
                            V_DOT_3.animate().alpha(1).setDuration(500).start();
                            V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                            V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
                            V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
                            alsSelectedList.add("N/A");
                        }
                    }
                }

            } else {
                Toast.makeText(mContext, "You can only enter a maximum of 3 titles!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetTypeFaces(View view) {
        AutofitTextView ATV_TITLE = view.findViewById(R.id.ATV_TITLE_TIO);
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Abril_fatface.ttf");

        ATV_TITLE.setTypeface(tf);
    }


    private void SetBackButton(View view) {
        ImageFilterView IV_BACK = view.findViewById(R.id.IFV_BACK_BUTTON_TIO);
        IV_BACK.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ArrayList<String> temp = new ArrayList<>(5);
            if (alsSelectedList.size() == 1) {
                if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_TITLE_1.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_ORG_1.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add(ET_TITLE_1.getText().toString() + "$o*" + ET_ORG_1.getText().toString());
                }
            } else if (alsSelectedList.size() == 2) {
                if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_TITLE_1.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_ORG_1.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add(ET_TITLE_1.getText().toString() + "$o*" + ET_ORG_1.getText().toString());
                }

                if (TextUtils.isEmpty(ET_ORG_2.getText().toString()) && TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_2.getText().toString()) && TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    ET_TITLE_2.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 2);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_2.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    ET_ORG_2.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 2);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_2.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    temp.add(ET_TITLE_2.getText().toString() + "$o*" + ET_ORG_2.getText().toString());
                }
            } else if (alsSelectedList.size() == 3) {
                if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_TITLE_1.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    ET_ORG_1.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 1);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_1.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_1.getText().toString())) {
                    temp.add(ET_TITLE_1.getText().toString() + "$o*" + ET_ORG_1.getText().toString());
                }

                if (TextUtils.isEmpty(ET_ORG_2.getText().toString()) && TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_2.getText().toString()) && TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    ET_TITLE_2.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 2);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_2.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    ET_ORG_2.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 2);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_2.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_2.getText().toString())) {
                    temp.add(ET_TITLE_2.getText().toString() + "$o*" + ET_ORG_2.getText().toString());
                }
                if (TextUtils.isEmpty(ET_ORG_3.getText().toString()) && TextUtils.isEmpty(ET_TITLE_3.getText().toString())) {
                    temp.add("N/A");
                } else if (!TextUtils.isEmpty(ET_ORG_3.getText().toString()) && TextUtils.isEmpty(ET_TITLE_3.getText().toString())) {
                    ET_TITLE_3.setError("Please enter the title");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 3);
                    return;
                } else if (TextUtils.isEmpty(ET_ORG_3.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_3.getText().toString())) {
                    ET_ORG_3.setError("Please enter the organization name");
                    MoveToError(Integer.parseInt(ML_CARDS.getTag().toString()), 3);
                    return;
                } else if (!TextUtils.isEmpty(ET_ORG_3.getText().toString()) && !TextUtils.isEmpty(ET_TITLE_3.getText().toString())) {
                    temp.add(ET_TITLE_3.getText().toString() + "$o*" + ET_ORG_3.getText().toString());
                }
            }
            if (alsSelectedList.contains("N/A")) {
                alsSelectedList.clear();
                alsSelectedList.add("N/A");
            }

            bundle.putStringArrayList(mContext.getString(R.string.field_titles_posts), temp);
            bundle.putBoolean(mContext.getString(R.string.field_is_private), isLocked);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            requireFragmentManager().beginTransaction().remove(PositionsFragment.this).commit();
        });
    }

    private void MoveToError(int from, int to) {
        if (from == 1 && to == 2) {
            ML_CARDS.setTag("1");
            IFV_NEXT.performClick();
        } else if (from == 1 && to == 3) {
            ML_CARDS.setTransition(R.id.TRANS_1CL_2RL_3RC);
            ML_CARDS.transitionToEnd();
            ML_CARDS.setTag("3");
            IFV_NEXT.animate().alpha(0).setDuration(500).start();
            IFV_PREV.animate().alpha(1).setDuration(500).start();
            V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
            V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
            V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
        } else if (from == 2 && to == 1) {
            ML_CARDS.setTag("2");
            IFV_PREV.performClick();
        } else if (from == 2 && to == 3) {
            ML_CARDS.setTag("2");
            IFV_NEXT.performClick();
        } else if (from == 3 && to == 1) {
            ML_CARDS.setTransition(R.id.TRANS_1CL_2RL_3RC);
            ML_CARDS.transitionToStart();
            ML_CARDS.setTag("1");
            IFV_NEXT.animate().alpha(1).setDuration(500).start();
            IFV_PREV.animate().alpha(0).setDuration(500).start();
            V_DOT_1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64FFAE")));
            V_DOT_2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
            V_DOT_3.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C5C5C5")));
        } else if (from == 3 && to == 2) {
            ML_CARDS.setTag("3");
            IFV_PREV.performClick();
        }
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

}
