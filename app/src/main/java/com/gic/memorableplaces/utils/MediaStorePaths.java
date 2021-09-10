package com.gic.memorableplaces.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MediaStorePaths {
    private static final String TAG = "MediaFilesScanner";
    private ArrayList<String> filePaths = new ArrayList<String>();

    public void getAllFinalPaths(Context context, ArrayList<String> directoryName, ArrayList<String> allFiles, ArrayList<String> allImages, ArrayList<String> allVideos, HashMap<String,
            ArrayList<String>> MainHashMap) {
        //  Log.d(TAG, "getAllFinalPaths: WHATTTTTTTT");
        //Query To GENERATE ALL FILES
        String[] ProjFiles = new String[]{
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_TAKEN
        };
        //URI FOR ALL FILES
        Uri uriFiles = MediaStore.Files.getContentUri("external");
        final String orderByFiles = MediaStore.Files.FileColumns.DATE_TAKEN;
        final String dataFiles = MediaStore.Files.FileColumns.DATA;
        //FILE SELECTION CRITERIA
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                + " AND "
                + MediaStore.Files.FileColumns.DURATION + " <=" + "60000";
        //File Selection ARGS
//                String[] VideoSelectionArgs = new String[]{
//          MediaStore.Files.FileColumns.DURATION + " <?" + "60000"
//                 };
        //Query To GENERATE ALL FILES
        String[] ProjIMAGE = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN};
        //URI FOR ALL IMAGES
        Uri uriIMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final String orderByImage = MediaStore.Images.Media.DATE_TAKEN;
        final String dataImage = MediaStore.Images.Media.DATA;

        // QUERY TO GENERATE ALL VIDEOS PATHS
        String[] ProjVideo = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN};
        //URI FOR ALL VIDEOS
        Uri uriVideo = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final String orderByVideo = MediaStore.Video.Media.DATE_TAKEN;
        final String dataVideo = MediaStore.Video.Media.DATA;

        getAllFilePaths(context, allFiles, ProjFiles, uriFiles, selection, null, orderByFiles, dataFiles);
        if (!allFiles.isEmpty()) {
            MainHashMap.put("All Media", allFiles);
            directoryName.add("All Media");
            Log.d(TAG, String.format("getAllFinalPaths: allFiles %s", allFiles));
        }
        getAllFilePaths(context, allImages, ProjIMAGE, uriIMAGE, null, null, orderByImage, dataImage);
        if (!allImages.isEmpty()) {
            MainHashMap.put("Photos", allImages);
            directoryName.add("Photos");
        }
        getAllFilePaths(context, allVideos, ProjVideo, uriVideo, null, null, orderByVideo, dataVideo);
        // Log.d(TAG, "getAllFinalPaths: allVideos " + allVideos);
        if (!allVideos.isEmpty()) {
            MainHashMap.put("Videos", allVideos);
            directoryName.add("Videos");
        }
        // Log.d(TAG, "getAllFinalPaths: MainHashMap " + MainHashMap);


    }

    public void getAllFilePaths(Context context, ArrayList<String> allDirectories, String[] Projection, Uri contentUri, String selection, String[] SelectionArgs, String OrderBY, String data) {
        //  Log.d(TAG, "getAllFilePaths: YAHA TAK AAGAYA");

        try (Cursor cursor = context.getContentResolver().query(contentUri, Projection, // Which
                // columns
                // to return
                selection, // Which rows to return (all rows)
                null, // Selection arguments (none)
                OrderBY + " DESC" // Ordering
        )) {
            if (Objects.requireNonNull(cursor).moveToFirst()) {
                //Log.d(TAG, "getAllFilePaths: in if");

                do {
                    if (allDirectories == null) {
                        allDirectories = new ArrayList<>();
                    }

                    allDirectories.add(cursor.getString(cursor
                            .getColumnIndex(data)));

                } while (cursor.moveToNext());
                //Log.d(TAG, String.format("getAllFilePaths: IMAGE/VIDEO/ALL %s", allDirectories));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Checking file extension
    public boolean isPhotoOrVideo(String fileName) {

        return fileName.endsWith(".jpg") || fileName.endsWith(".png") ||
                fileName.endsWith(".gif") || fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") || fileName.endsWith(".mkv")
                || fileName.endsWith("bmp");
    }

    public static boolean isVideo(String fileName) {

        return fileName.endsWith(".mp4") || fileName.endsWith(".3gp") || fileName.endsWith(".mkv");
    }
}