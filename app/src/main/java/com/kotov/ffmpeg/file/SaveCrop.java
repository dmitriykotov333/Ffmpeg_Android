package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public class SaveCrop extends FileImpl {


    @Override
    public File saveFile(String n) {
        File folder = mkDir(n);
        String fileExt = ".mp4";
        return new File(folder, n + fileExt);
    }

}
