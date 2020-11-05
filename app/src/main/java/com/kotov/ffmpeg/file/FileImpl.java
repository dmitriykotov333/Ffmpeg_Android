package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public abstract class FileImpl {

    abstract File saveFile(String n);

    public File mkDir(String a) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + a);
        if (!folder.exists()) {
            if (folder.mkdir()) {

            }
        }
        return folder;
    }
}
