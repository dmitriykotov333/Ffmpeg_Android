package com.kotov.ffmpeg.models;

import android.annotation.SuppressLint;
import android.net.Uri;


import com.kotov.ffmpeg.di.video_action.VideoActionScope;
import com.kotov.ffmpeg.file.SaveFrames;
import com.kotov.ffmpeg.interfaces.VideoActionImpl;

import java.io.File;

import javax.inject.Inject;

@VideoActionScope
public class FramesVideo implements VideoActionImpl {

    private Uri uri;
    private RealPathFromUri realPathFromUri;

    @Inject
    public FramesVideo() {

    }

    public void setData(Uri uri, RealPathFromUri realPathFromUri) {
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
