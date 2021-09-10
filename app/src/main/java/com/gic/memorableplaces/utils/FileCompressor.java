package com.gic.memorableplaces.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class FileCompressor {
    private Context mContext;

//    private final String IMAGE_DESTINATION_FILE = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Images";
//    private final String VIDEO_DESTINATION_PATH = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Videos";

    FileCompressor(Context mContext) {
        this.mContext = mContext;
    }

    public File compressImage(String imageUrl,int quality){

        File imageFile = new File(imageUrl);
        try {
            return new Compressor(mContext).setQuality(quality).compressToFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return new File(imageUrl);
        }
    }



}
