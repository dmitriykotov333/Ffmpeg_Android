package com.kotov.ffmpeg.models;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;


import com.kotov.ffmpeg.di.video_action.VideoActionScope;

import java.util.List;

import javax.inject.Inject;

@VideoActionScope
public class VideoActions {

    private CropVideo cropVideo;
    private FramesVideo framesVideo;
    private FrameBySec frameBySec;

    @Inject
    public VideoActions(CropVideo cropVideo, FramesVideo framesVideo, FrameBySec frameBySec) {
        this.cropVideo = cropVideo;
        this.framesVideo = framesVideo;
        this.frameBySec = frameBySec;
    }

    public void setData(Activity activity, Context context, Uri uri, RealPathFromUri realPathFromUri, List<ImageView> imageViews) {
        cropVideo.setData(context, uri, realPathFromUri);
        framesVideo.setData(uri, realPathFromUri);
        frameBySec.setData(activity, uri, imageViews);
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
