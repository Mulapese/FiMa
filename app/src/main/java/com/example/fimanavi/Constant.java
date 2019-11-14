package com.example.fimanavi;

import android.Manifest;
import android.os.Environment;

public class Constant {
    public static final int REQUEST_PERMISSIONS = 1234;
    public static final int MAX_LENGTH_TITLE = 29;
    public static final int PERMISSION_COUNT = 2;
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String DOWNLOAD_DIRECTORY = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
}
