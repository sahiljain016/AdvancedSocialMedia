package com.gic.memorableplaces.Share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gic.memorableplaces.Adapters.FontsSpinnerAdapter;
import com.gic.memorableplaces.CustomLibs.shinebuttonlib.ShineButton;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.GlideImageLoader;
import com.gic.memorableplaces.utils.MediaStorePaths;
import com.gic.memorableplaces.utils.StringManipulation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import top.defaults.colorpicker.ColorPickerPopup;


public class PublishActivity extends AppCompatActivity {
    //CONSTANTS
    private static final String TAG = "PublishActivity";
    private Context context;
    private Activity activity;
    private String mAppend = "file:/";
    private Boolean clicked = false;
    private Boolean fileType = false;
    private static final int REQ_CODE_SPEECH_INPUT = 10;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    //WIDGETS
    private EditText mCaption;
    private ShineButton mBold_SB, mUnderline_SB, mItalics_SB;
    private TextView mPublish, swipeTV, preview, PreviewTV;
    private ImageView swipe, backArrow;
    private JellyToggleButton jellySwitch;
    private RelativeLayout LockRelLayout;
    private ImageButton lockIB, PlayButton, Speak_Now, ColorPicker;
    private Spinner fontSpinner;
    //Variables
    private int StartSelection, EndSelection;
    private Typeface face;
    private Intent intent;
    private int imageCount;
    private int videoCount;
    private String mPath = "", CaptionHTML= "", CodeBUI= "", font= "", scaleType= "", fontColor= "-16777216";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        context = PublishActivity.this;
        activity = PublishActivity.this;

        // Intent
        intent = getIntent();
        mPath = intent.getStringExtra(getString(R.string.selected_image));
        if (!MediaStorePaths.isVideo(Objects.requireNonNull(mPath))) {
            fileType = true;
        }
//        Log.d(TAG, "onCreate: got chosen Image " + getIntent().getStringExtra(getString(R.string.selected_image)));
        //Classes
        mFirebaseMethods = new FirebaseMethods(context);

        //widgets INITIALIZATION
        mCaption = findViewById(R.id.edit_text_publish);
        backArrow = findViewById(R.id.ic_back_arrow);
        mPublish = findViewById(R.id.Publish_tv);
        mBold_SB = findViewById(R.id.Bold);
        mUnderline_SB = findViewById(R.id.Underline);
        mItalics_SB = findViewById(R.id.Italics);
        jellySwitch = findViewById(R.id.jellySwitch);
        LockRelLayout = findViewById(R.id.LockRelLayout);
        lockIB = findViewById(R.id.ic_lock);
        swipe = findViewById(R.id.swipe);
        swipeTV = findViewById(R.id.swipeTV);
        fontSpinner = findViewById(R.id.spinner_publish);
        PlayButton = findViewById(R.id.play_publish);
        Speak_Now = findViewById(R.id.speak_reader);
        preview = findViewById(R.id.Preview);
        PreviewTV = findViewById(R.id.previewTV);
        ColorPicker = findViewById(R.id.IB_COLOR_PICKER_CHAT);

        fontSpinner.setEnabled(false);
        mBold_SB.setClickable(false);
        mUnderline_SB.setClickable(false);
        mItalics_SB.setClickable(false);
        /*
        BACK ARROW ON CLICK
         */
        backArrow.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing publish activity");
            finish();
        });
        /*
        PUBLISH TV ON CLICK
         */
        mPublish.setOnClickListener(v -> {
            //Upload image to firebase
            if (PreviewTV.getText().toString().equals("Preview")) {
                CodeBUI = mCaption.getText().toString();

                Log.d(TAG, "onClick: CodeBUUi" + CodeBUI);
                if (!fileType) {
                    Toast.makeText(context, "Sharing your Video...", Toast.LENGTH_SHORT).show();
                    mFirebaseMethods.UploadNewVideo(CodeBUI, font, fontColor, videoCount, mPath, activity);
                } else {
                    Toast.makeText(context, "Sharing your Image...", Toast.LENGTH_SHORT).show();

                    mFirebaseMethods.UploadNewPhoto(getString(R.string.new_photo), CodeBUI, scaleType, font, fontColor,
                            imageCount, mPath, activity);

                }
            }
            else{
                Toast.makeText(context, "Please click done to proceed!", Toast.LENGTH_SHORT).show();
            }
        });
        lockIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: lock");
                clicked = true;
                jellySwitch.setChecked(false);
                LockRelLayout.setVisibility(View.VISIBLE);
                jellySwitch.setVisibility(View.VISIBLE);
                swipe.setVisibility(View.VISIBLE);
                swipeTV.setVisibility(View.VISIBLE);
                lockIB.setVisibility(View.GONE);

            }
        });
        /*
        JELLY SWITCH ON STATE CHANGE LISTENER
         */

        jellySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                LockRelLayout.setVisibility(View.VISIBLE);
                jellySwitch.setVisibility(View.VISIBLE);
                swipe.setVisibility(View.VISIBLE);
                swipeTV.setVisibility(View.VISIBLE);
                lockIB.setVisibility(View.GONE);
                fontSpinner.setEnabled(false);
                mBold_SB.setClickable(false);
                mUnderline_SB.setClickable(false);
                mItalics_SB.setClickable(false);
                mCaption.setShowSoftInputOnFocus(true);
            } else {
                Log.d(TAG, "onStateChange: check jelly");
                LockRelLayout.setVisibility(View.GONE);
                jellySwitch.setVisibility(View.GONE);
                swipe.setVisibility(View.GONE);
                swipeTV.setVisibility(View.GONE);
                lockIB.setVisibility(View.VISIBLE);
                fontSpinner.setEnabled(true);
                mBold_SB.setClickable(true);
                mUnderline_SB.setClickable(true);
                mItalics_SB.setClickable(true);
                mCaption.setShowSoftInputOnFocus(false);
                if (!clicked) {
                    Toast.makeText(context, "Select your text to make it \nBold / Underlined / Italics!", Toast.LENGTH_SHORT).show();
                }
                clicked = false;
            }
        });
        /*
        LOCK ON TEXT VIEW
         */

        /*
        ON CLICK FOR PLAY BUTTON
         */
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(context).create();
                final VideoView videoView = new VideoView(context);
                videoView.setVideoPath(Objects.requireNonNull(mPath));
                videoView.start();
                builder.setView(videoView);
                builder.show();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.start();
                    }
                });
            }
        });
        //INITIALIZING METHODS TO BOLD & UNDERLINE & ITALICS ETC
        MakeTextBold();
        MakeTextUnderline();
        MakeTextItalics();
        PreviewCaption();
        SetUpSpinner();
        setupFirebaseAuth();
        SetUpImage();
        SetUpMicrophone();
        showColorPicker();
    }

    /**
     * Color picker library setup
     */

    public void showColorPicker() {
        ColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(PublishActivity.this)
                        .initialColor(Color.RED)// Set initial color
                        .enableBrightness(true) // Enable brightness slider or not
                        .enableAlpha(true) // Enable alpha slider or not
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                mCaption.setTextColor(color);
                                fontColor = String.valueOf(color);
                            }
                        });
            }
        });

    }

    /**
     * Setup Microphone to type according to what user speaks
     */

    public void SetUpMicrophone() {

        Speak_Now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_type));
                intent.putExtra("android.speech.extra.DICTATION_MODE", true);
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(context, "Unable to Hear", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * SETTINGS SELECTED IMAGE INTO IMAGEVIEW
     */
    private void SetUpImage() {

        ImageView ImageViewPublish = findViewById(R.id.ImageViewPublish);

        if (!fileType) {
            PlayButton.setVisibility(View.VISIBLE);
        } else {
            if (intent.getBooleanExtra(getString(R.string.scale_type), false)) {
                Log.d(TAG, "SetUpImage: false");
                ImageViewPublish.setScaleType(ImageView.ScaleType.CENTER_CROP);
                scaleType = "CENTER_CROP";
            } else {
                Log.d(TAG, "SetUpImage: true");
                ImageViewPublish.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                scaleType = "CENTER_INSIDE";
            }
            PlayButton.setVisibility(View.GONE);
        }
        GlideImageLoader.loadImageWithTransition(context,mPath,ImageViewPublish,null,null);
        //UniversalImageLoader.setImage(mPath, ImageViewPublish, null, mAppend);
    }


    /**
     * Making selected text bold
     */
    private void MakeTextBold() {
        mBold_SB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSelection = mCaption.getSelectionStart();
                EndSelection = mCaption.getSelectionEnd();
                Log.d(TAG, "onClick: SELCTIONS " + StartSelection + " AND " + EndSelection);
                if (StartSelection == EndSelection) {
                    mBold_SB.setChecked(false);
                    Toast.makeText(context, "Select desired text to make it bold.", Toast.LENGTH_SHORT).show();
                } else {
                    String part1 = mCaption.getText().toString().substring(0, StartSelection);
                    String part2 = mCaption.getText().toString().substring(StartSelection, EndSelection);
                    String part3 = mCaption.getText().toString().substring(EndSelection, mCaption.getText().length());
                    CaptionHTML = part1 + "<B>" + part2 + "</B>" + part3;
                    //Log.d(TAG, "onClick: caption " + caption);
                    mCaption.setText(CaptionHTML);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBold_SB.setChecked(false);
                        }
                    }, 2000);
                }
            }
//                StartSelection = mCaption.getSelectionStart();
//                EndSelection = mCaption.getSelectionEnd();
//                boolean b = false;
//                SpannableStringBuilder stringBuilders = (SpannableStringBuilder) mCaption.getText();
//                StyleSpan[] styleSpanss = stringBuilders.getSpans(0, mCaption.getText().toString().length(), StyleSpan.class);
//                if (styleSpanss.length > 0){
//                    b = styleSpanss[0].getStyle() == Typeface.BOLD;}
//                Log.d(TAG, "onClick: stylespan " + "= " + styleSpanss.length + " " + b + " " + Arrays.toString(styleSpanss));
//                if (styleSpanss.length > 0 && styleSpanss[0].getStyle() == Typeface.BOLD) {
//                    Log.d(TAG, "onClick: if " + Arrays.toString(styleSpanss));
//                    for (StyleSpan styleSpans : styleSpanss) {
//                        stringBuilders.removeSpan(styleSpans);
//                    }
//
//                } else {
//                    Log.d(TAG, "onClick: else");
//                    stringBuilders.setSpan(new StyleSpan(Typeface.BOLD), 0, mCaption.getText().toString().length()
//                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    //mCaption.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                }
//                styleSpanss = null;
//                Log.d(TAG, "onClick: empty array " + Arrays.toString(styleSpanss));
//                mCaption.setText(stringBuilders);

        });
    }

    /**
     * Make selected text underlined
     */
    private void MakeTextUnderline() {
        //                SpannableStringBuilder stringBuilder = (SpannableStringBuilder) mCaption.getText();
//                UnderlineSpan[] underlineSpans = stringBuilder.getSpans(0, mCaption.getText().toString().length(), UnderlineSpan.class);
//                // Log.d(TAG, String.format("onClick: underlineSpans %s", (Object) underlineSpans));
//                if (underlineSpans != null && underlineSpans.length > 0/*&& StartSelection >= 0 && EndSelection >= 0*/) {
//                    // Log.d(TAG, String.format("onClick: if %d", underlineSpans.length));
//                    for (int i = 0; i < underlineSpans.length; i++) {
//                        // Log.d(TAG, "onClick: underline Spans " + underlineSpans[i]);
//                        stringBuilder.removeSpan(underlineSpans[i]);
//                    }
//                    underlineSpans = null;
//                } else {
//                    Log.d(TAG, "onClick: else");
//                    stringBuilder.setSpan(new UnderlineSpan(), 0, mCaption.getText().toString().length(), 0);
//                }
//                mCaption.setText(stringBuilder);
        mUnderline_SB.setOnClickListener(v -> {
            StartSelection = mCaption.getSelectionStart();
            EndSelection = mCaption.getSelectionEnd();
            if (StartSelection == EndSelection) {
                mUnderline_SB.setChecked(false);
                Toast.makeText(context, "Select desired text to underline it.", Toast.LENGTH_SHORT).show();
            } else {
                String part1 = mCaption.getText().toString().substring(0, StartSelection);
                String part2 = mCaption.getText().toString().substring(StartSelection, EndSelection);
                String part3 = mCaption.getText().toString().substring(EndSelection, mCaption.getText().length());
                CaptionHTML = part1 + "<U>" + part2 + "</U>" + part3;
                // Log.d(TAG, "onClick: caption " + caption);
                mCaption.setText(CaptionHTML);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUnderline_SB.setChecked(false);
                    }
                }, 2000);
            }
        });
    }

    /**
     * Make Selected Text Italics
     */
    private void MakeTextItalics() {
        mItalics_SB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSelection = mCaption.getSelectionStart();
                EndSelection = mCaption.getSelectionEnd();
                if (StartSelection == EndSelection) {
                    mItalics_SB.setChecked(false);
                    Toast.makeText(context, "Select desired text to make it Italics!", Toast.LENGTH_SHORT).show();
                } else {
                    String part1 = mCaption.getText().toString().substring(0, StartSelection);
                    String part2 = mCaption.getText().toString().substring(StartSelection, EndSelection);
                    String part3 = mCaption.getText().toString().substring(EndSelection, mCaption.getText().length());
                    CaptionHTML = part1 + "<I>" + part2 + "</I>" + part3;
                    //Log.d(TAG, "onClick: caption " + CaptionHTML);
                    mCaption.setText(CaptionHTML);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mItalics_SB.setChecked(false);
                        }
                    }, 2000);
                }
            }
//                boolean b = false;
//                SpannableStringBuilder stringBuilder = (SpannableStringBuilder) mCaption.getText();
//                StyleSpan[] styleSpans = stringBuilder.getSpans(0, mCaption.getText().toString().length(), StyleSpan.class);
//                if (styleSpans.length > 0){
//                    b = styleSpans[0].getStyle() == Typeface.ITALIC;}
//                Log.d(TAG, "onClick: stylespan " + "= " + styleSpans.length + " " + b + " " + Arrays.toString(styleSpans));
//                if (styleSpans.length > 0 && styleSpans[0].getStyle() == Typeface.ITALIC) {
//                    Log.d(TAG, "onClick: if " + Arrays.toString(styleSpans));
//                    for (StyleSpan styleSpan : styleSpans) {
//                        Log.d(TAG, "onClick: stylespan ");
//                        stringBuilder.removeSpan(styleSpan);
//                        //}
//
//                        //}
//                    }} else {
//                    Log.d(TAG, "onClick: else");
//                    stringBuilder.setSpan(new StyleSpan(Typeface.ITALIC), 0, mCaption.getText().toString().length(), 0);
//                }
//                styleSpans = null;
//                mCaption.setText(stringBuilder);

        });
    }

    /**
     * Settings preview textView
     */

    public void PreviewCaption() {
        PreviewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (PreviewTV.getText().toString().equals("Preview")) {
                    CodeBUI = mCaption.getText().toString();
                   // Log.d(TAG, "afterTextChanged: " + CodeBUI);
                    mCaption.setFocusableInTouchMode(false);
                    mCaption.clearFocus();
                    mCaption.setText(Html.fromHtml(StringManipulation.findEmptyLines(mCaption.getText().toString()), Html.FROM_HTML_MODE_LEGACY));
                    PreviewTV.setText("Done");
                } else if (PreviewTV.getText().toString().equals("Done")) {
                    mCaption.setFocusableInTouchMode(true);
                    mCaption.setText(CodeBUI);
                    PreviewTV.setText("Preview");
                }
            }
        });

    }

    /**
     * SETTING UP SPINNER WITH FONTS
     */
    private void SetUpSpinner() {
        ArrayList<String> FontFaces = new ArrayList<>();
        FontFaces.add("Arial");
        FontFaces.add("akaDora");
        FontFaces.add("Eutemia");
        FontFaces.add("GREENPIL");
        FontFaces.add("Grinched");
        FontFaces.add("Helvetica");
        FontFaces.add("Libertango");
        FontFaces.add("Metal Macabre");
        FontFaces.add("Parry Hotter");
        FontFaces.add("The GodFather");
        FontFaces.add("Waltograph");

        FontsSpinnerAdapter adapter = new FontsSpinnerAdapter(this, R.layout.custom_spinner, FontFaces);
        fontSpinner.setAdapter(adapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/Arial.ttf");
                        font = "Arial";
                        mCaption.setTextSize(20);
                        break;
                    case 1:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/akaDora.ttf");
                        font = "akaDora";
                        mCaption.setTextSize(25);
                        break;
                    case 2:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/Eutemia.ttf");
                        font = "Eutemia";
                        mCaption.setTextSize(35);
                        break;
                    case 3:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/GREENPIL.ttf");
                        font = "GREENPIL";
                        mCaption.setTextSize(35);
                        break;
                    case 4:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/Grinched.ttf");
                        font = "Grinched";
                        mCaption.setTextSize(25);
                        break;
                    case 5:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/Helvetica.ttf");
                        font = "Helvetica";
                        mCaption.setTextSize(25);
                        break;
                    case 6:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/Libertango.ttf");
                        font = "Libertango";
                        mCaption.setTextSize(25);
                        break;
                    case 7:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/MetalMacabre.ttf");
                        font = "MetalMacabre";
                        mCaption.setTextSize(20);
                        break;
                    case 8:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/ParryHotter.ttf");
                        font = "ParryHotter";
                        mCaption.setTextSize(20);
                        break;
                    case 9:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/TheGodfather_v2.ttf");
                        font = "TheGodfather";
                        mCaption.setTextSize(35);
                        break;
                    case 10:
                        face = Typeface.createFromAsset(context.getAssets(), "fonts/waltograph42.ttf");
                        font = "waltograph";
                        mCaption.setTextSize(35);
                        break;
                }
                mCaption.setTypeface(face);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * getting result from speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String existingText = mCaption.getText().toString() + " ";
                if (result != null) {
                    mCaption.setText(String.format("%s%s", existingText, result.get(0)));
                }
            }
        }
    }
    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabse.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (fileType) {
                    imageCount = mFirebaseMethods.getImageCount(snapshot);
                    Log.d(TAG, "onDataChange: imageCOunt " + imageCount);
                } else {
                    videoCount = mFirebaseMethods.getVideoCount(snapshot);
                    Log.d(TAG, "onDataChange: videoCount " + videoCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseMethods firebaseMethods = new FirebaseMethods(this);
        firebaseMethods.SetOnlineStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseMethods firebaseMethods = new FirebaseMethods(this);
        firebaseMethods.SetOnlineStatus(true);
    }
}
