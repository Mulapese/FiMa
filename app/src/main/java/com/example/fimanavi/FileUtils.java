package com.example.fimanavi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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

    public static String[] getLastModified(File[] files){
        String[] dates = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            Date lastModDate = new Date(files[i].lastModified());
            dates[i] = lastModDate.toString();
        }
        return dates;
    }

    public static int[] getIcon(File[] files){
        int[] icon = new int[files.length];
        for(int i = 0; i < files.length; i++){
            if(files[i].isDirectory()){
                icon[i] = R.drawable.ic_menu_camera;
            } else {
                if(FileUtils.fileExt(files[i].getAbsolutePath()).equals("png")){
                    icon[i] = R.drawable.ic_menu_gallery;
                } else if (FileUtils.fileExt(files[i].getAbsolutePath()).equals("mp4")){
                    icon[i] = R.drawable.ic_menu_video;
                } else if (FileUtils.fileExt(files[i].getAbsolutePath()).equals("mp3")){
                    icon[i] = R.drawable.ic_menu_manage;
                } else {
                    String s = FileUtils.fileExt(files[i].getAbsolutePath());
                    icon[i] = R.drawable.ic_menu_send;
                }
            }
        }
        return icon;
    }
}
