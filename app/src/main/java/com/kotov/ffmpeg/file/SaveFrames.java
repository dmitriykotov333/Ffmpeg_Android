package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public class SaveFrames extends FileImpl {

    @Override
    public File saveFile(String n) {
        File folder = mkDir(n);
        String filePrefix = "%05d";
        String fileExt = ".png";
        return new File(folder, filePrefix + fileExt);
    }

}



