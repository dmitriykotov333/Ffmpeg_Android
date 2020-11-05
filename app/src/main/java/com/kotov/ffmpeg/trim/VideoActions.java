package com.kotov.ffmpeg.trim;

import android.content.Context;
import android.net.Uri;

public class VideoActions {

    private CropVideo cropVideo;
    private FramesVideo framesVideo;

    public VideoActions(Context context, Uri uri) {
        RealPathFromUri realPathFromUri = new RealPathFromUri(context);
        cropVideo = new CropVideo(context, uri, realPathFromUri);
        framesVideo = new FramesVideo(uri, realPathFromUri);
    }

    public String[] crop(int a, int b, String n) {
        return cropVideo.action(a, b, n);
    }

    public String[] frames(int a, int b, String n) {
        return framesVideo.action(a, b, n);
    }

}
