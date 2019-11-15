package com.example.fimanavi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.io.File;

public class Common {

    // Minimum the path if it is too long
    public static String minimumPath(String s){
        if(s.length() > Constant.MAX_LENGTH_TITLE){
            int start = s.lastIndexOf("/");
            int end = s.length();
            s = s.substring(0,9) + "..." + s.substring(start, end);
        }
        return s;
    }

    // Check permission
    public static boolean arePermissionDenied(Context context) {
        int p = 0;
        while (p < Constant.PERMISSION_COUNT) {
            if (ActivityCompat.checkSelfPermission(context, Constant.PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED) {
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

    // Function to remove the element
    public static File[] removeArrayElement(File[] arr, int index) {
        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        File[] anotherArray = new File[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }
}
