package com.gic.memorableplaces.utils;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MediaFilesScanner extends AsyncTask<String, Void, ArrayList> {

    private final ProgressBar progressBar;
    private ArrayList<String> filePaths = new ArrayList<String>();

    public MediaFilesScanner(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    protected ArrayList<String> doInBackground(String[] directoryName) {

        File directory = new File(directoryName[0]);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && isPhotoOrVideo(file.getName())) {
                //Log.d("filePath", "path : " + file.getAbsolutePath());
//                if (getVideoDuration(file.getAbsolutePath()) <= 60000) {
                    filePaths.add(0, file.getAbsolutePath());
                //}
            }
        }
        return filePaths;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        super.onPostExecute(arrayList);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
    }

    //Checking file extension
    public boolean isPhotoOrVideo(String fileName) {

        return fileName.endsWith(".jpg") || fileName.endsWith(".png") ||
                fileName.endsWith(".gif") || fileName.endsWith(".mp4") ||
                fileName.endsWith(".3gp") || fileName.endsWith(".mkv")
                || fileName.endsWith("bmp") || fileName.endsWith(".JPG") || fileName.endsWith(".PNG");
    }

    public static boolean isVideo(String fileName) {

        return fileName.endsWith(".mp4") || fileName.endsWith(".3gp") || fileName.endsWith(".mkv");
    }

    public long getVideoDuration(String Path) {
        FFmpegMediaMetadataRetriever mFFmpegMediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mFFmpegMediaMetadataRetriever.setDataSource(Path);
        String mVideoDuration = mFFmpegMediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(mVideoDuration);
    }


}