package com.kotov.ffmpeg.trim;

import android.content.Context;
import android.net.Uri;

public class VideoActions {

    private CropVideo cropVideo;
    private FramesVideo framesVideo;
    private int a;
    private int b;
    private String n;

    public VideoActions(Context context, Uri uri, int a, int b, String n) {
        RealPathFromUri realPathFromUri = new RealPathFromUri(context);
        cropVideo = new CropVideo(context, uri, realPathFromUri);
        framesVideo = new FramesVideo(uri, realPathFromUri);
        this.a = a;
        this.b = b;
        this.n = n;
    }

    public String[] crop() {
        return cropVideo.action(a, b, n);
    }

    public String[] frames() {
        return framesVideo.action(a, b, n);
    }

}
