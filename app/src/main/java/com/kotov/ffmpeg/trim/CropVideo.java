package com.kotov.ffmpeg.trim;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.kotov.ffmpeg.file.SaveCrop;
import com.kotov.ffmpeg.trim.interfacevideo.TrimVideo;

import java.io.File;

public class CropVideo implements TrimVideo {

    private Context context;
    private Uri uri;
    private File dest;
    public CropVideo(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }
    @SuppressLint("DefaultLocale")
    @Override
    public String[] trimVideo(int a, int b, String n) {
        SaveCrop saveCrop = new SaveCrop();
        dest = saveCrop.saveFile(n);
        String original_path = getRealPathFromURI(uri);
        int duration = (b - a) / 1000;
        addVideo(dest, n);
        return new String[]{"-ss", "" +  (a / 1000), "-y", "-i", original_path, "-t", "" + duration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", dest.getAbsolutePath()};
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

    @Override
    public File mkDir(String a) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + a);
        if (!folder.exists()) {
            if (folder.mkdir()) {

            }
        } else {

        }
        return folder;
    }

    @Override
    public String getRealPathFromURI(Uri contentURI) {
        String filePath;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }

    @Override
    public File getFIle() {
        return dest;
    }
}
