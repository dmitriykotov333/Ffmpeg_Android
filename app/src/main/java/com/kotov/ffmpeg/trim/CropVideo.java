package com.kotov.ffmpeg.trim;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.kotov.ffmpeg.file.SaveCrop;
import com.kotov.ffmpeg.trim.interfacevideo.ActionImpl;

import java.io.File;

public class CropVideo implements ActionImpl {

    private Uri uri;
    private RealPathFromUri realPathFromUri;
    private Context context;

    public CropVideo(Context context, Uri uri, RealPathFromUri realPathFromUri) {
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

    /**
     * TrimVideo on new Video
     *
     * @param videoFile v
     */
    private void addVideo(File videoFile, String fileName) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, fileName);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }
}
