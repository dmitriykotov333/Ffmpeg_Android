package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public class SaveFrames implements FileI {

    @Override
    public File saveFile(String n) {
        File folder = mkDir(n);
        String filePrefix = "%05d";
        String fileExt = ".png";
        return new File(folder, filePrefix + fileExt);
    }

    @Override
    public File mkDir(String a) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + a);
        if (!folder.exists()) {
            if (folder.mkdir()) {

            }
        } else {

        }
        return folder;
    }
}



