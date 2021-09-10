package com.gic.memorableplaces.Share;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.R;
import com.gic.memorableplaces.utils.FirebaseMethods;
import com.gic.memorableplaces.utils.MediaStorePaths;
import com.gic.memorableplaces.utils.Permissions;
import com.gic.memorableplaces.utils.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";

    //Constants
    private static final int GALLERY_FRAGMENT_NUM = 0;
    private static final int CAMERA_FRAGMENT_NUM = 1;

    private static final int PHOTO_REQUEST_CODE = 5;
    private static final int VIDEO_REQUEST_CODE = 10;
    final String outputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
    File imageFile, videoFile;
    private ImageButton openPhoto, openVideo;
    private Uri imageUri, VideoUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        openPhoto = view.findViewById(R.id.openPhoto);
        openVideo = view.findViewById(R.id.openVideo);

        //Ignoring FileUri expose exception
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        openVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Video = null;
                if (((ShareActivity) requireActivity()).getCurrentTabNumber() == CAMERA_FRAGMENT_NUM) {
                    if (((ShareActivity) requireActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])) {
                        String dateAdded = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                        if (Platform.hasAndroidR()) {
                            //Log.d(TAG, "onCreateView: Starting Camera For Photo");
                            AndroidQSavePostCaptured(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"SXCSC-" + dateAdded + ".mp4","video/mp4");
                            Video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            Video.putExtra(MediaStore.EXTRA_OUTPUT, VideoUri);
                        } else {
                            videoFile = new File(outputDirectory, "SXCSC-" + dateAdded + ".mp4");
                            Video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            Video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                        }
                        Objects.requireNonNull(Video).putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                        startActivityForResult(Video, VIDEO_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }
            }
        });
        openPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CameraIntent = null;
                if (((ShareActivity) requireActivity()).getCurrentTabNumber() == CAMERA_FRAGMENT_NUM) {
                    if (((ShareActivity) requireActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])) {
                        if (Platform.hasAndroidR()) {
                            Log.d(TAG, "onCreateView: Starting Camera For Photo");
                            String dateAdded = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                            AndroidQSavePostCaptured(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"SXCSC-" + dateAdded + ".png","image/png");
                            CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        } else {
                            String dateAdded = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                            imageFile = new File(outputDirectory, "SXCSC-" + dateAdded + ".png");
                            CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        }
                        startActivityForResult(CameraIntent, PHOTO_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                }

            }
        });

    }

    /**
     * saving image in camera in St Xavier's Social CLub folder for Android Q
     * @param uri
     */

    private void AndroidQSavePostCaptured(Uri uri,String fileName, String MimeType) {
        OutputStream fos = null;
        Context mContext = getActivity();
        ContentResolver resolver = Objects.requireNonNull(mContext).getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, MimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "St Xavier's Social Club");
        imageUri = resolver.insert(uri, contentValues);
        try {
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Objects.requireNonNull(fos).flush();
            Objects.requireNonNull(fos).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + CAMERA_FRAGMENT_NUM);
        if (requestCode == PHOTO_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: done taking photo");
            Log.d(TAG, "onActivityResult: navigating to publish activity");

            if(!Platform.hasAndroidR()){
            if (CheckIfFileIsNull(imageFile) && imageFile.exists()) {
                UploadingImageAfterCapture(imageFile.getAbsolutePath());
            } }
            else{
                if (!Uri.EMPTY.equals(imageUri)) {
                UploadingImageAfterCapture(imageUri.getPath());
            }}
        } else if (requestCode == VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: done taking photo");
            Log.d(TAG, "onActivityResult: navigating to publish activity");
            //navigate to final publishing activity
            if(!Platform.hasAndroidR()){
            if (CheckIfFileIsNull(videoFile) && videoFile.exists()) {
                UploadingVideoAfterCapture(videoFile.getAbsolutePath());
            } }
            else{
                if (!Uri.EMPTY.equals(VideoUri)) {
                UploadingVideoAfterCapture(VideoUri.getPath());
            }}
        }
    }

    /**
     * checking if image/video file is null for build version less than android Q
     * @param file
     * @return
     */
    private Boolean CheckIfFileIsNull(File file) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            if (br.readLine() != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * method to upload profile photo or post a new photo after capturing
     * @param filepath
     */
    private void UploadingImageAfterCapture(String filepath) {
        if (requireActivity().getIntent().getBooleanExtra(getString(R.string.Update_profile), false)) {

            new FirebaseMethods(getActivity()).UploadNewPhoto(getString(R.string.new_profile_photo), null, null,
                    null, null, 0, filepath, getActivity());
        } else {
            Intent intent = new Intent(getActivity(), PublishActivity.class);
            intent.putExtra(getString(R.string.selected_image), filepath);
            startActivity(intent);
        }

    }

    private void UploadingVideoAfterCapture(String filePath){
        if (requireActivity().getIntent().getBooleanExtra(getString(R.string.Update_profile), false)) {
            Toast.makeText(getActivity(), "Videos are not allowed as profile picture!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), PublishActivity.class);
            intent.putExtra(getString(R.string.selected_image), filePath);
            startActivity(intent);
        }
    }
}
