package com.gic.memorableplaces.utils;

import android.Manifest;

public class Permissions {
    public final static String [] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    public final static String [] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA
    };
    public final static String [] WRITE_STORAGE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public final static String [] READ_STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}
