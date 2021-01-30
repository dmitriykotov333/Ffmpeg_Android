package com.kotov.ffmpeg.models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;


import com.kotov.ffmpeg.di.video_action.VideoActionScope;
import com.kotov.ffmpeg.file.SaveCrop;
import com.kotov.ffmpeg.interfaces.VideoActionImpl;

import java.io.File;

import javax.inject.Inject;


@VideoActionScope
public class CropVideo implements VideoActionImpl {

    private Uri uri;
    private RealPathFromUri realPathFromUri;
    private Context context;

    @Inject
    public CropVideo() {

    }

    public void setData(Context context, Uri uri, RealPathFromUri realPathFromUri) {
        this.context = context;
        this.uri = uri;
        this.realPathFromUri = realPathFromUri;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String[] action(int a, int b, String n) {
        SaveCrop saveCrop = new SaveCrop();
        File dest = saveCrop.saveFile(n);
        String original_path = realPathFromUri.getPath(uri);
        int duration = (b - a) / 1000;
        addVideo(dest, n);
        return new String[]{"-ss", "" + (a / 1000), "-y", "-i", original_path, "-t", "" + duration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", dest.getAbsolutePath()};
    }

    private void addVideo(File videoFile, String fileName) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }
}
