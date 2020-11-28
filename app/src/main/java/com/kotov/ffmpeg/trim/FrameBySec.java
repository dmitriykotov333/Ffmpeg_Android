package com.kotov.ffmpeg.trim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.kotov.ffmpeg.TrimmerUtils;

import java.util.ArrayList;
import java.util.List;

public class FrameBySec {

    private Uri uri;
    private Activity activity;
    private List<ImageView> imageViews;

    public FrameBySec(Activity activity, Uri uri, List<ImageView> imageViews) {
        this.activity = activity;
        this.uri = uri;
        this.imageViews = imageViews;
    }

    public List<ImageView> getBitmap() {
        long diff = TrimmerUtils.getDuration(activity, uri) / 8;
        new Handler().postDelayed(() -> {
            int index = 1;
            for (ImageView imageView : imageViews) {
                imageView.setImageBitmap(getFrameBySec(activity, uri, diff * index));
                index++;
            }
        }, 1000);
        Log.i("A", String.valueOf(imageViews.size()));
        return imageViews;
    }

    public Bitmap getFrameBySec(Activity context, Uri videoPath, long millies) {
        try {
            String formatted = millies + "000000";
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoPath);
            Bitmap bitmap = retriever.getFrameAtTime(Long.parseLong(formatted));
            retriever.release();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
