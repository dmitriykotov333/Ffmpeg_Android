package com.kotov.ffmpeg.trim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import com.kotov.ffmpeg.file.SaveFrames;
import com.kotov.ffmpeg.trim.interfacevideo.ActionImpl;

import java.io.File;

public class FramesVideo implements ActionImpl {

    private Uri uri;
    private RealPathFromUri realPathFromUri;

    public FramesVideo(Uri uri, RealPathFromUri realPathFromUri) {
        this.uri = uri;
        this.realPathFromUri = realPathFromUri;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String[] action(int a, int b, String n) {
        SaveFrames saveFrames = new SaveFrames();
        File dest = saveFrames.saveFile(n);
        int duration = (b - a) / 1000;
        String original_path = realPathFromUri.getPath(uri);
        return new String[]{"-i", original_path, "-an", "-ss", "" + a / 1000, "-t", "" + duration, dest.getAbsolutePath()};
    }
}
