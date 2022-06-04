package com.gic.memorableplaces.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;


public class FileCompressor {
    private Context mContext;

//    private final String IMAGE_DESTINATION_FILE = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Images";
//    private final String VIDEO_DESTINATION_PATH = Environment.getExternalStorageDirectory().getPath()+"/InstagramClone/Videos";

    FileCompressor(Context mContext) {
        this.mContext = mContext;
    }

    public File compressImage(String imageUrl, int quality) {

        File imageFile = new File(imageUrl);
        try {
            return new Compressor(mContext).setQuality(quality).compressToFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return new File(imageUrl);
        }
    }

    public String saveBitmapToFile(String filePath) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/uploaded_images");
            myDir.mkdirs();
            File file = new File (myDir, filePath);
            if (file.exists()) file.delete();
            file.createNewFile();

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file.toURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
