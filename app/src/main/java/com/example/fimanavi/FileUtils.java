package com.example.fimanavi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    // Delete file or folder
    public static void deleteFileOrFolder(File fileOrFolder) {
        if (fileOrFolder.isDirectory()) {
            if (fileOrFolder.list().length == 0) {
                fileOrFolder.delete();
            } else {
                String files[] = fileOrFolder.list();
                for (String temp : files) {
                    File fileToDelete = new File(fileOrFolder, temp);
                    deleteFileOrFolder(fileToDelete);
                }
                if (fileOrFolder.list().length == 0) {
                    fileOrFolder.delete();
                }
            }
        } else {
            fileOrFolder.delete();
        }
    }

    // Get extension of file
    public static String fileExt(String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    // Copy a file
    public static void copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
