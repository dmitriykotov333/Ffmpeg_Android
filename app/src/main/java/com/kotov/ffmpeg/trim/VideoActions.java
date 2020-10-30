package com.kotov.ffmpeg.trim;

import android.content.Context;
import android.net.Uri;


import com.kotov.ffmpeg.trim.interfacevideo.IDuration;

import java.util.Arrays;


public class VideoActions implements IDuration {

    private CropVideo cropVideo;
    private FramesVideo framesVideo;
    private RealPathFromUri realPathFromUri;
    private int a;
    private int b;
    private String n;

    public VideoActions(Context context, Uri uri, int a, int b, String n) {
        realPathFromUri = new RealPathFromUri(context);
        cropVideo = new CropVideo(context, uri, realPathFromUri);
        framesVideo = new FramesVideo(uri, realPathFromUri);
        this.a = a;
        this.b = b;
        this.n = n;
    }

    public String[] crop() {
        return cropVideo.trimVideo(a, b, n);
    }

    public String[] frames() {
        return framesVideo.trimVideoCadr(a, b, n);
    }

    @Override
    public int getDuration() {
        return (b - a) / 1000;
    }

    public String getFile(String[] cmd) {
        if (Arrays.toString(cmd).equals(Arrays.toString(crop()))) {
            return cropVideo.getFIle().getAbsolutePath();
        } else {
            return framesVideo.getFIle().getAbsolutePath();
        }
    }
}
