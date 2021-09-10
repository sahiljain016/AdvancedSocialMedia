package com.gic.memorableplaces.Share;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.Adapters.GridViewAdapter;
import com.gic.memorableplaces.Adapters.SpinnerAdapter;
import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.DirectoryScanner;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.MediaFilesScanner;
import com.gic.memorableplaces.utils.MediaStorePaths;
import com.gic.memorableplaces.utils.Platform;
import com.gic.memorableplaces.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //Constants
    private static final int NUM_OF_GRID_COLUMNS = 3;
    private static final int REQUEST_CODE_CHOOSE = 100;
    private final String mAppend = "file:/";

    //Widgets
    private GridView gridView;
    private TextView mNextShare;
    private ImageView mCloseShare, mImageViewMain, mPlayButton;
    private VideoView mVideoViewMain;
    private Spinner DirectorySpinner;
    private ProgressBar mProgressBar;
    private ImageButton mChangeAspectRatio;
    private RelativeLayout rl;
    //VARIABLES
    //File path tools for ANDROID Q+
    ArrayList<String> allPhotos, allVideos, allFiles;
    ArrayList<String> filePaths;
    ArrayList<String> AlbumCoverPaths;
    HashMap<String, ArrayList<String>> MainHashMap;
    String mPath;
    //File path tools for ANDROID Q Or Less
    ArrayList<String> dirPaths;
    ArrayList<String> dummyAlbumCovers;
    ArrayList<String> directoryNames;
    //CLASSES
    //Classes related to ANDROID Q+ Method
    MediaStorePaths mediaStorePaths;
    GestureDetector gestureDetector;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        Log.d(TAG, "onCreateView: GAllllerryy Fragment");
        //ANDROID R METHODS
        AlbumCoverPaths = new ArrayList<>();
        allFiles = new ArrayList<>();
        allPhotos = new ArrayList<>();
        allVideos = new ArrayList<>();
        //ANDROID Q or BELOW
        dummyAlbumCovers = new ArrayList<>();
        dirPaths = new ArrayList<>();
        MainHashMap = new HashMap<>();
        directoryNames = new ArrayList<>();

        //Widgets
        gridView = view.findViewById(R.id.GridViewGallery);
        mProgressBar = view.findViewById(R.id.ProgressBarGallery);
        mImageViewMain = view.findViewById(R.id.GalleryImageView);
        mPlayButton = view.findViewById(R.id.PlayButton);
        mVideoViewMain = view.findViewById(R.id.videoView);
        mCloseShare = view.findViewById(R.id.close_share);
        mNextShare = view.findViewById(R.id.next_share_tv);
        DirectorySpinner = view.findViewById(R.id.spinner_share);
        mChangeAspectRatio = view.findViewById(R.id.ChangeAspectRatio);
        rl = view.findViewById(R.id.RLGallery);
        //Lists, HashMaps, Arrays
        filePaths = new ArrayList<>();
        //Classes

        //onCreate Properties
        mProgressBar.setVisibility(View.GONE);
//        if (Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra(getString(R.string.Update_profile), false)) {
//            Log.d(TAG, "onCreateView: REACHED");
//            mChangeAspectRatio.setVisibility(View.INVISIBLE);
//        }
        // Settings Methods
        //initImageLoader();
        InitListPaths();
        setupSpinner();

        //On CLICK LISTENER FOR TEXT VIEWS
        mCloseShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d(TAG, "onClick: CLosed Share");
                requireActivity().finish();

            }
        });
        mNextShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: Navigating to publish");
                if (requireActivity().getIntent().getBooleanExtra(getString(R.string.Update_profile), false)) {

                    if (!MediaStorePaths.isVideo(mPath)) {
                        new FirebaseMethods(getActivity()).UploadNewPhoto(getString(R.string.new_profile_photo), null, null, null, null, 0, mPath, getActivity());
                    } else {
                        Toast.makeText(getContext(), "Videos are not allowed as profile picture!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(MediaStorePaths.isVideo(mPath)){
                        if(getVideoDuration(mPath) > 30000){
                            Toast.makeText(getActivity(), "Videos greater than 30 Seconds are not allowed!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(getActivity(), PublishActivity.class);
                            intent.putExtra(getString(R.string.selected_image), mPath);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }

                    }else {
                        Intent intent = new Intent(getActivity(), PublishActivity.class);
                        intent.putExtra(getString(R.string.selected_image), mPath);
                        intent.putExtra(getString(R.string.scale_type), mImageViewMain.getScaleType() == ImageView.ScaleType.CENTER_CROP);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }
        });
        mChangeAspectRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d(TAG, "onClick: mChangeAspectRatio ");
                if (mImageViewMain.getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
                    mImageViewMain.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    mImageViewMain.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
            }
        });
        hideImageView();
        return view;
    }
    public long getVideoDuration(String Path) {
        FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mFFmpegMediaMetadataRetriever.setDataSource(Path);
        String mVideoDuration = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(mVideoDuration);
    }

    private void InitListPaths() {
        if (Platform.hasAndroidR()) {
            mediaStorePaths = new MediaStorePaths();
            mediaStorePaths.getAllFinalPaths(getActivity(), directoryNames, allFiles, allPhotos, allVideos, MainHashMap);
            for (int i = 0; i < MainHashMap.size(); i++) {
                //Log.d(TAG, "onCreateView: i " + "= " + i);
                //Log.d(TAG, String.format("onCreateView: hasa %s", MainHashMap.get(directoryName.get(i))));
                AlbumCoverPaths.add(Objects.requireNonNull(MainHashMap.get(directoryNames.get(i))).get(0));
                // Log.d(TAG, String.format("onCreateView: AlbumCoverPaths %s", AlbumCoverPaths));
            }
        } else {
            dirPaths = DirectoryScanner.getFileDirectories();
            //Log.d(TAG, String.format("onCreateView: dirPaths %s", dirPaths));
            directoryNames = DirectoryScanner.getDirectoryNames();
            // Log.d(TAG, "onCreateView: directoryNames " + directoryNames);
            for (int i = 0; i < directoryNames.size(); i++) {
                try {
                    Log.d(TAG, "onCreateView: diretoryNames = " + directoryNames.get(i));
                    dummyAlbumCovers = new MediaFilesScanner(null).execute(dirPaths.get(i)).get();
                    AlbumCoverPaths.add(dummyAlbumCovers.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Log.d(TAG, String.format("onCreateView: AlbumCoverPaths %s", AlbumCoverPaths));
            }
        }
    }

    private void setupSpinner() {

        SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), R.layout.spinner_layout, directoryNames, AlbumCoverPaths);
        DirectorySpinner.setAdapter(adapter);


        DirectorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPlayButton.setVisibility(View.GONE);
                if (Platform.hasAndroidR()) {
                    //setting up gridView for selected directory
                    setupGridView(directoryNames.get(position));
                } else {
                    if (filePaths != null) {
                        filePaths.clear();
                    }
                    //setting up gridView for selected directory

                    setupGridView(dirPaths.get(position));
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupGridView(final String selectedDirectory) {

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_OF_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);


        try {

            // Log.d(TAG, "setupGridView: Selected Directory " + selectedDirectory);
            // Log.d(TAG, String.format("setupGridView: TempHAShMAp %s", MainHashMap));
            if (Platform.hasAndroidR()) {
                filePaths = MainHashMap.get(selectedDirectory);
            } else {
                //Log.d(TAG, "setupGridView: Selected Directory = " + selectedDirectory);
                filePaths = new MediaFilesScanner(mProgressBar).execute(selectedDirectory).get();
                //Log.d(TAG, String.format("setupGridView: listPaths %s", filePaths));
            }
            //Log.d(TAG, String.format("setupGridView: listPaths %s", filePaths));
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridViewAdapter adapter = new GridViewAdapter(requireActivity(), R.layout.layout_gridview_images, mAppend, filePaths);
        gridView.setAdapter(adapter);

        if (filePaths != null) {
            final int position = 0;
            mPath = filePaths.get(position);
            if (!MediaStorePaths.isVideo(mPath)) {
                displayImage(mPath);
            } else {
                playVideo(mPath);
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayButton.setVisibility(View.GONE);
                mPath = filePaths.get(position);
                if (!MediaStorePaths.isVideo(mPath)) {
                    displayImage(mPath);
                } else {
                    playVideo(mPath);
                }
            }
        });
    }

    private void playVideo(String path) {

        mChangeAspectRatio.setVisibility(View.GONE);
        mImageViewMain.setVisibility(View.GONE);
        mVideoViewMain.setVisibility(View.VISIBLE);
        mVideoViewMain.setVideoPath(path);
        mVideoViewMain.start();

        mVideoViewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoViewMain.isPlaying()) {
                    mPlayButton.setVisibility(View.VISIBLE);
                    mVideoViewMain.pause();
                } else {
                    mPlayButton.setVisibility(View.GONE);
                    mVideoViewMain.start();
                }
            }
        });
        mVideoViewMain.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoViewMain.start();
            }
        });
    }


    private void displayImage(String path) {
        if (requireActivity().getIntent().getBooleanExtra(getString(R.string.Update_profile), false)) {
            mChangeAspectRatio.setVisibility(View.INVISIBLE);
        } else {
            mChangeAspectRatio.setVisibility(View.VISIBLE);
        }
        mVideoViewMain.setVisibility(View.GONE);
        mImageViewMain.setVisibility(View.VISIBLE);
        //GlideImageLoader.loadImageWithTransition(getContext(), path,camImage);
        UniversalImageLoader.setImage(path, mImageViewMain, mProgressBar, mAppend);
    }

//    private void initImageLoader() {
//
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
//        ImageLoader.getInstance().init(UniversalImageLoader.getConfig(getActivity(),R.drawable.ic_default_image_share));
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void hideImageView() {

        gestureDetector = new GestureDetector(getContext(), new com.gic.memorableplaces.utils.GestureDetector(rl, gridView,true));

        rl.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
