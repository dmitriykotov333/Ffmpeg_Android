package com.kotov.ffmpeg.trim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class VideoActions {

    private CropVideo cropVideo;
    private FramesVideo framesVideo;
    private FrameBySec frameBySec;

    public VideoActions(Context context, Uri uri, Activity activity, List<ImageView> imageViews) {
        RealPathFromUri realPathFromUri = new RealPathFromUri(context);
        cropVideo = new CropVideo(context, uri, realPathFromUri);
        framesVideo = new FramesVideo(uri, realPathFromUri);
        frameBySec = new FrameBySec(activity, uri, imageViews);
    }

    public String[] crop(int a, int b, String n) {
        return cropVideo.action(a, b, n);
    }

    public String[] frames(int a, int b, String n) {
        return framesVideo.action(a, b, n);
    }

    public List<ImageView> frameBySec() {
        return frameBySec.getBitmap();
    }

}
