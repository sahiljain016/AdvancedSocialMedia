package com.gic.memorableplaces.SignUp;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.gic.memorableplaces.Adapters.MyImagesRecyclerViewAdapter;
import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.RoomsDatabases.FilterDetailsDatabase;
import com.gic.memorableplaces.RoomsDatabases.UserDetailsDatabase;
import com.gic.memorableplaces.interfaces.FilterDetailsDao;
import com.gic.memorableplaces.interfaces.UserDetailsDao;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.MiscTools;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.virgilsecurity.common.callback.OnResultListener;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import me.grantland.widget.AutofitTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfilePhotoFragment extends Fragment {
    private static final String TAG = "ProfilePhotoFragment";
    private int pos = 4, SelectedPos = 100;

    private User user;
    private UserDetailsDao userDetailsDao;

    private ImageView IV_BG, IV_STAR;
    private ConstraintLayout CL_AUTO_DESP;

    private FilterDetails fdMyDetails;
    private FilterDetailsDatabase FD_DETAILS;
    private ArrayList<String> aluImagePaths;
    private ExecutorService databaseWriteExecutor;

    private Context mContext;
    private SpotlightConfig spotlightConfig;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    private MyImagesRecyclerViewAdapter myImagesRecyclerViewAdapter;
    private StringBuilder stringBuilder, MusicStringBuilder, MovieStringBuilder, BooksStringBuilder;

    private CardView CV_ADD_IMAGE;
    private CardStackView CSV;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_pt_3, container, false);
        Log.d(TAG, "onCreateView: ProfilePhotoFragment");
        mContext = getActivity();
        stringBuilder = new StringBuilder();
        MusicStringBuilder = new StringBuilder();
        MovieStringBuilder = new StringBuilder();
        BooksStringBuilder = new StringBuilder();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        aluImagePaths = new ArrayList<>(5);
        fdMyDetails = new FilterDetails();

        user = new User();
        UserDetailsDatabase UD_DETAILS = UserDetailsDatabase.getDatabase(mContext);
        userDetailsDao = UD_DETAILS.userDetailsDao();

        FD_DETAILS = FilterDetailsDatabase.getDatabase(mContext);
        databaseWriteExecutor = FD_DETAILS.databaseWriteExecutor;

        GetFilterDetails();
        GetPhotosFromDatabase();

        IV_BG = view.findViewById(R.id.IV_BG_PP);
        IV_STAR = view.findViewById(R.id.IV_STAR_PP);

        AutofitTextView ATV_TITLE_1_PP = view.findViewById(R.id.ATV_TITLE_1_PP);
        Typeface tf = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/Abril_fatface.ttf");
        ATV_TITLE_1_PP.setTypeface(tf, Typeface.NORMAL);

        CL_AUTO_DESP = view.findViewById(R.id.CL_AUTO_DESP_PP);

        AppCompatImageButton mNextCard = view.findViewById(R.id.ACB_NEXT_PP);
        AppCompatImageButton mBackCard = view.findViewById(R.id.ACB_BACK_PP);
        CV_ADD_IMAGE = view.findViewById(R.id.CV_ADD_IMAGE_PP);

        //SetPageNumber();
        DevelopUserDescription();
        SetPhotosCardStackView(view);
        ShowSpotlights();
        GetResultFromGallery();

        mBackCard.setOnClickListener(v -> {
            QuestionsFragment fragment = new QuestionsFragment();
            FragmentTransaction Transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Transaction.replace(R.id.FrameLayoutCard, fragment);
            Transaction.addToBackStack(mContext.getString(R.string.questions_fragment));
            Transaction.commit();
            requireFragmentManager().beginTransaction().remove(ProfilePhotoFragment.this).commit();

        });

        mNextCard.setOnClickListener(v -> {
            boolean isPhoto = false;
            for (String string : aluImagePaths) {
                if (!string.equals("N/A")) {
                    isPhoto = true;
                    break;
                }
            }
            if (isPhoto) {
                FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                firebaseMethods.UploadPhotosList(aluImagePaths, user.getProfile_photo(), requireActivity(), new OnResultListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        myRef.child(mContext.getString(R.string.dbname_users))
                                .child(mAuth.getCurrentUser().getUid())
                                .child(mContext.getString(R.string.field_sign_up_complete))
                                .setValue(true);
                        databaseWriteExecutor.execute(() -> userDetailsDao.UpdateSignUpCompleteBool(true, mAuth.getCurrentUser().getUid()));
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
            } else {
                Toast.makeText(mContext, "Please upload at least 1 image to proceed.", Toast.LENGTH_SHORT).show();
            }

            /*Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            requireFragmentManager().beginTransaction().remove(ProfilePhotoFragment.this).commit();*/
        });


        return view;
    }

    private void GetResultFromGallery() {

        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //  int pos = myImagesRecyclerViewAdapter.GetPosition();
                        Log.d(TAG, "onCreateView: pos: " + pos);

                        new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                                .headingTvText("Make Profile Picture")
                                .subHeadingTvText("Please click on the image to either remove or select it as your profile picture.")
                                .target(CSV)
                                .usageId("SB_STAR")
                                .setListener(spotlightViewId -> {
                                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                                }).show();

                        if (pos == 0) {
                            pos = 1;
                        } else if (pos == 1) {
                            pos = 2;
                        } else if (pos == 2) {
                            pos = 3;
                        } else if (pos == 3) {
                            pos = 4;
                        } else if (pos == 4) {
                            pos = 0;
                        }
                        //aluImagePaths.indexOf(Uri.parse("N/A"));
                        Uri uri = null;
                        if (result.getData() != null) {
                            uri = result.getData().getData();
                        }
                        if (pos == 0) {
                            if (uri != null) {
                                aluImagePaths.set(0, uri.toString());
                            }
                            myImagesRecyclerViewAdapter.notifyItemChanged(0);
                            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(0))).into(IV_BG);

                        } else if (pos == 1) {
                            if (uri != null) {
                                aluImagePaths.set(1, uri.toString());
                            }
                            myImagesRecyclerViewAdapter.notifyItemChanged(1);
                            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(1))).into(IV_BG);
                        } else if (pos == 2) {
                            if (uri != null) {
                                aluImagePaths.set(2, uri.toString());
                            }
                            myImagesRecyclerViewAdapter.notifyItemChanged(2);
                            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(2))).into(IV_BG);
                        } else if (pos == 3) {
                            if (uri != null) {
                                aluImagePaths.set(3, uri.toString());
                            }
                            myImagesRecyclerViewAdapter.notifyItemChanged(3);
                            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(3))).into(IV_BG);
                        } else if (pos == 4) {
                            if (uri != null) {
                                aluImagePaths.set(4, uri.toString());
                            }
                            myImagesRecyclerViewAdapter.notifyItemChanged(4);
                            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(4))).into(IV_BG);
                        }
                        databaseWriteExecutor.execute(() -> userDetailsDao.UpdatePhotoList(aluImagePaths, mAuth.getCurrentUser().getUid()));
                        // Use the uri to load the image
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });

        CV_ADD_IMAGE.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Changing Profile Photo");
            ImagePicker.Companion.with(requireActivity())
                    .crop()
                    .cropOval()
                    .maxResultSize(512, 512, true)
                    .createIntentFromDialog(new Function1() {
                        public Object invoke(Object var1) {
                            this.invoke((Intent) var1);
                            return Unit.INSTANCE;
                        }

                        public final void invoke(@NotNull Intent it) {
                            Intrinsics.checkNotNullParameter(it, "it");
                            launcher.launch(it);
                        }
                    });
        });

    }

    private void SetPhotosCardStackView(View view) {
        CSV = view.findViewById(R.id.CSV_PP);

        myImagesRecyclerViewAdapter = new MyImagesRecyclerViewAdapter(aluImagePaths, pos -> {
            Glide.with(mContext).load(Uri.parse(aluImagePaths.get(pos))).into(IV_BG);
            if (IV_STAR.getTag().equals("visible")) {
                IV_STAR.setVisibility(View.GONE);
                IV_STAR.setTag("gone");
                user.setProfile_photo("N/A");
                databaseWriteExecutor.execute(() -> userDetailsDao.UpdateProfilePhoto("N/A", mAuth.getCurrentUser().getUid()));
            } else {
                IV_STAR.setVisibility(View.VISIBLE);
                IV_STAR.setTag("visible");
                Toast.makeText(mContext, "Image set as profile picture!", Toast.LENGTH_SHORT).show();
                user.setProfile_photo(aluImagePaths.get(pos));
                databaseWriteExecutor.execute(() -> userDetailsDao.UpdateProfilePhoto(user.getProfile_photo(), mAuth.getCurrentUser().getUid()));
            }
            Log.d(TAG, "onItemClick: clicked pos: " + pos);
            if (pos == 0) {
                SelectedPos = 4;
            } else if (pos == 1) {
                SelectedPos = 0;
            } else if (pos == 2) {
                SelectedPos = 1;
            } else if (pos == 3) {
                SelectedPos = 2;
            } else if (pos == 4) {
                SelectedPos = 3;

            }
        }, mContext);
        CardStackLayoutManager cardStackLayoutManager = new CardStackLayoutManager(mContext, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

                //  Log.d(TAG, "onCardRewound: dragging dir: " + direction.name());
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: Selected Pos: " + SelectedPos);
                Log.d(TAG, "onCardSwiped: pos: " + pos);
                if (SelectedPos == pos) {
                    IV_STAR.setVisibility(View.VISIBLE);
                    IV_STAR.setTag("visible");
                } else {
                    IV_STAR.setVisibility(View.GONE);
                    IV_STAR.setTag("gone");
                }
                if (pos == 4) {
                    myImagesRecyclerViewAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCardRewound() {
                //Log.d(TAG, "onCardRewound: rewound");
            }

            @Override
            public void onCardCanceled() {

                //  Log.d(TAG, "onCardRewound: canceled");
            }

            @Override
            public void onCardAppeared(View view, int position) {

                //  Log.d(TAG, "onCardRewound: appered pos: " + position);
            }

            @Override
            public void onCardDisappeared(View view, int position) {

                // Log.d(TAG, "onCardRewound: disappered pos: " + position);
                pos = position;

            }
        }) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL);
        cardStackLayoutManager.setStackFrom(StackFrom.Bottom);
        cardStackLayoutManager.setMaxDegree(45);

        cardStackLayoutManager.setVisibleCount(3);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        CSV.setLayoutManager(cardStackLayoutManager);
        CSV.setAdapter(myImagesRecyclerViewAdapter);

    }

    private void GetFilterDetails() {
        databaseWriteExecutor.execute(() -> {
            FilterDetailsDao FD_Dao = FD_DETAILS.filterDetailsDao();
            fdMyDetails = FD_Dao.GetAllFilterDetails().get(0);


            if (!fdMyDetails.getMusic().get(0).equals("N/A")) {
                for (int i = 0; i < fdMyDetails.getMusic().size(); i++) {
                    GetMusicFromID(fdMyDetails.getMusic().get(i), i);
                }
            }
            if (!fdMyDetails.getMovies().get(0).equals("N/A")) {
                for (int i = 0; i < fdMyDetails.getMovies().size(); i++) {
                    String query = fdMyDetails.getMovies().get(i);
                    GetMovieFromID(query.substring(0, query.indexOf("$4$")), query.substring(query.indexOf("$4$") + 3), i);
                }
            }

            if (!fdMyDetails.getBooks().get(0).equals("N/A")) {
                for (int i = 0; i < fdMyDetails.getBooks().size(); i++) {
                    GetBooksFromID(fdMyDetails.getBooks().get(i), i);
                }
            }
        });

    }

    private void ShowSpotlights() {
        spotlightConfig = new SpotlightConfig();

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

        new SpotlightView.Builder(requireActivity()).setConfiguration(spotlightConfig)
                .headingTvText("Add Photos")
                .subHeadingTvText("Use this button to upload images from your gallery or camera. You can upload a max of 5 images.")
                .target(CV_ADD_IMAGE)
                .usageId("SB_PHOTOS")
                .setListener(spotlightViewId -> {
                    Log.d(TAG, "InitViews: spotlightViewId: " + spotlightViewId);
                }).show();
    }

    private void GetPhotosFromDatabase() {

        aluImagePaths.add("N/A");
        aluImagePaths.add("N/A");
        aluImagePaths.add("N/A");
        aluImagePaths.add("N/A");
        aluImagePaths.add("N/A");
        databaseWriteExecutor.execute(() -> {
            user = userDetailsDao.GetAllDetails(mAuth.getCurrentUser().getUid()).get(0);

            for (int i = 0; i < user.getPhotos_list().size(); i++) {
                aluImagePaths.set(i, user.getPhotos_list().get(i));

                if (user.getProfile_photo().equals(user.getPhotos_list().get(i))) {
                    if (i == 0) {
                        SelectedPos = 4;
                    } else if (i == 1) {
                        SelectedPos = 0;
                    } else if (i == 2) {
                        SelectedPos = 1;
                    } else if (i == 3) {
                        SelectedPos = 2;
                    } else {
                        SelectedPos = 3;
                    }
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                Glide.with(mContext).load(Uri.parse(aluImagePaths.get(0))).into(IV_BG);
                myImagesRecyclerViewAdapter.notifyItemRangeChanged(0, 5);
                if (SelectedPos == 4) {
                    IV_STAR.setVisibility(View.VISIBLE);
                    IV_STAR.setTag("visible");
                }
            });
        });
    }

    private void SetPageNumber() {
        Query Page_query = myRef.child(mContext.getString(R.string.dbname_users))
                .child(mAuth.getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_page_number));

        Page_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().toString().equals(mContext.getString(R.string.card_completed)) || !snapshot.exists()) {
                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(mAuth.getCurrentUser().getUid())
                            .child(mContext.getString(R.string.field_page_number))
                            .setValue("3");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DevelopUserDescription() {

        CL_AUTO_DESP.setOnClickListener(v -> {
            Dialog ET_Dialog = MiscTools.InflateDialog(mContext, R.layout.layout_auto_desp);
            EditText ET = ET_Dialog.findViewById(R.id.ET_AUTO_DESP);
            TextView TV_CLEAR = ET_Dialog.findViewById(R.id.TV_CLEAR_AUTO_DESP);

            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(user.getAuto_desp());
            stringBuilder.append(" I am in the ").append("<B>").append(fdMyDetails.getCollege_year()).append("</B>").append(" of my College Life. ");
            stringBuilder.append("I was born on ").append("<B>").append(fdMyDetails.getBirthdate()).append("</B>").append(" ");
            stringBuilder.append("& I am ").append("<B>").append(fdMyDetails.getAge()).append("</B>").append(" years old. ");

            if (fdMyDetails.getGeneral_details().size() > 8) {
                if (!fdMyDetails.getGeneral_details().get(8).equals("N/A"))
                    stringBuilder.append("My zodiac Sign is ").append("<B>").append(fdMyDetails.getGeneral_details().get(8)).append("</B>").append(". ");

            }
            if (!fdMyDetails.getGender().equals(mContext.getString(R.string.field_no_gender))) {
                stringBuilder.append("I identify as ").append("<B>").append(fdMyDetails.getGender()).append("</B>").append(". ");
            }
            if (!fdMyDetails.getPronouns().equals("N/A")) {
                stringBuilder.append("The pronouns that I prefer are ").append("<B>").append(fdMyDetails.getPronouns()).append("</B>").append(". ");
            }
            stringBuilder.append("Now, Here are some insights into my choices over common subjects in life. \n");

            if (!fdMyDetails.getTitles_posts().get(0).equals("N/A")) {
                for (int i = 0; i < fdMyDetails.getTitles_posts().size(); i++) {
                    String Title = fdMyDetails.getTitles_posts().get(i).substring(0, fdMyDetails.getTitles_posts().get(i).indexOf("$o*"));
                    String Org = fdMyDetails.getTitles_posts().get(i).substring(fdMyDetails.getTitles_posts().get(i).indexOf("$o*") + 3);

                    if (i == 0) {
                        stringBuilder.append("I am the ").append("<B>").append(Title).append("</B>").append(" at ").append("<B>").append(Org).append("</B>");
                    } else if (i < (fdMyDetails.getTitles_posts().size() - 1)) {
                        stringBuilder.append(", ").append("<B>").append(Title).append("</B>").append(" at ").append("<B>").append(Org).append("</B>");
                    } else if (i == (fdMyDetails.getTitles_posts().size() - 1)) {
                        stringBuilder.append("and ").append("<B>").append(Title).append("</B>").append(" at ").append("<B>").append(Org).append("</B>");
                    }
                }
            }

            if (!fdMyDetails.getSociety_in_college().get(0).equals("N/A")) {
                stringBuilder.append(". I am a part of <B>").append(fdMyDetails.getSociety_in_college().toString().substring(1, (fdMyDetails.getSociety_in_college().toString().length() - 1))).append("</B>. ");
            }
            if (!fdMyDetails.getHobbies().get(0).equals("N/A")) {
                stringBuilder.append("In my free time, I like <B>").append(fdMyDetails.getHobbies().toString().substring(1, (fdMyDetails.getHobbies().toString().length() - 1))).append("</B>. ");
            }
            if (!fdMyDetails.getVideo_games().get(0).equals("N/A")) {
                stringBuilder.append("I am quite the gamer, i like playing <B>").append(fdMyDetails.getVideo_games().toString().substring(1, (fdMyDetails.getVideo_games().toString().length() - 1))).append("</B>.");
            }
            if (fdMyDetails.getMusic().get(0).equals("N/A"))
                stringBuilder.append("I am not really fond of any music.");
            else
                stringBuilder.append("I am also quite fond of music, some of my favourite tracks are <B>").append(MusicStringBuilder.toString()).append("</B>. ");

            if (fdMyDetails.getMovies().get(0).equals("N/A"))
                stringBuilder.append("I am not really fond of any movies.");
            else
                stringBuilder.append("I loved watching movies like <B>").append(MovieStringBuilder.toString()).append("</B>. ");

            if (fdMyDetails.getBooks().get(0).equals("N/A"))
                stringBuilder.append("I am not really fond of any books.");
            else
                stringBuilder.append("I also like to read a few books, my best reads are <B>").append(BooksStringBuilder.toString()).append("</B>. ");

            stringBuilder.append("So, that's it about me. Thanks for reading. Bye!");
            ET.setText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY));

            Log.d(TAG, "onDataChange: stringbuilder desp: " + stringBuilder.toString());

            TV_CLEAR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Automatic Description.")
                            .setIcon(R.drawable.ic_alert_icon)
                            .setMessage("This will delete all of the auto description that has been created.")
                            .setPositiveButton("OK",
                                    (dialog, whichButton) -> {
                                        ET.setText("");
                                        stringBuilder.delete(0, stringBuilder.length());
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    (dialog, whichButton) -> dialog.dismiss()
                            )
                            .create();
                    builder.show();
                }
            });
            ET_Dialog.show();
        });
    }

    private void GetMusicFromID(String query, int pos) {
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


                    if (!TextUtils.isEmpty(jsonObject.getString("id"))) {
                        if (TextUtils.isEmpty(jsonObject.getString("title"))) {
                            MusicStringBuilder.append("N/A");
                        } else {
                            MusicStringBuilder.append(jsonObject.getString("title"));
                        }
                    }
                    if (pos != (fdMyDetails.getMusic().size() - 1)) {
                        MusicStringBuilder.append(", ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(mContext, "Error Received! Please try again.", Toast.LENGTH_SHORT).show();
                }
                // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
            }
        });

    }

    private void GetMovieFromID(String query, String type, int pos) {
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
                        if (jsonObject.has("id")) {
                            if (type.equals("movie")) {
                                if (!jsonObject.has("title")) {
                                    MovieStringBuilder.append("N/A");
                                } else {
                                    MovieStringBuilder.append(jsonObject.getString("title"));

                                }
                            } else if (type.equals("tv")) {
                                if (!jsonObject.has("name")) {
                                    MovieStringBuilder.append("N/A");
                                } else {
                                    MovieStringBuilder.append(jsonObject.getString("name"));
                                }
                            }

                            if (pos != (fdMyDetails.getMovies().size() - 1)) {
                                MovieStringBuilder.append(", ");
                            }
                        }


                        response.close();
                    } catch (Exception ignored) {
                    }
                    // Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
            // }
//                            }, 1000);


        } catch (Exception ignored) {
        }
    }

    private void GetBooksFromID(String query, int pos) {
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
                        if (jsonObject.has("id")) {
                            if (TextUtils.isEmpty(volumeInfoObject.getString("title"))) {
                                BooksStringBuilder.append("N/A");
                            } else {
                                BooksStringBuilder.append(volumeInfoObject.getString("title"));
                            }
                        }
                        if (pos != (fdMyDetails.getBooks().size() - 1)) {
                            BooksStringBuilder.append(", ");
                        }

                    } catch (Exception ignored) {

                    }
                    Log.d(TAG, String.format("onResponse: API RESPONSE %s", data));
                }
            });
        } catch (Exception ignored) {

        }
    }

}
