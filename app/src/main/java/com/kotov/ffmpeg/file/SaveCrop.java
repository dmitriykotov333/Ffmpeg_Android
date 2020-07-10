package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public class SaveCrop implements FileI {


    @Override
    public File saveFile(String n) {
        File folder = mkDir(n);
        String fileExt = ".mp4";
        return new File(folder, n + fileExt);
    }

    @Override
    public File mkDir(String a) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + a);
        if (!folder.exists()) {
            if (folder.mkdir()) {

            }
        }
        return folder;
    }
}
