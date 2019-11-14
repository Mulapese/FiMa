package com.example.fimanavi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.File;

public class Common {
    public static final int REQUEST_PERMISSIONS = 1234;
    public static final int MAX_LENGTH_TITLE = 29;
    public static int PERMISSION_COUNT = 2;
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Minimum the path if it is too long
    public static String minimumPath(String s){
        if(s.length() > MAX_LENGTH_TITLE){
            int start = s.lastIndexOf("/");
            int end = s.length();
            s = s.substring(0,9) + "..." + s.substring(start, end);
        }
        return s;
    }

    // Check permission
    public static boolean arePermissionDenied(Context context) {
        int p = 0;
        while (p < PERMISSION_COUNT) {
            if (ActivityCompat.checkSelfPermission(context, PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            p++;
        }
        return false;
    }

    // Reverse file array
    public static void reverseFileArray(File[] files){
        for(int i = 0; i < files.length / 2; i++)
        {
            File temp = files[i];
            files[i] = files[files.length - i - 1];
            files[files.length - i - 1] = temp;
        }
    }
}
