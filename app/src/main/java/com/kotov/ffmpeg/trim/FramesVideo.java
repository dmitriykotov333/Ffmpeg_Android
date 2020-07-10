package com.kotov.ffmpeg.trim;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.kotov.ffmpeg.file.SaveFrames;
import com.kotov.ffmpeg.trim.interfacevideo.TrimCadr;

import java.io.File;

public class FramesVideo implements TrimCadr {

    private Context context;
    private Uri uri;
    private File dest;
    public FramesVideo(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }
    @SuppressLint("DefaultLocale")
    @Override
    public String[] trimVideoCadr(int a, int b, String n) {
        String[] output = null;
        SaveFrames saveFrames = new SaveFrames();
        dest = saveFrames.saveFile(n);
        int duration = (b - a) / 1000;
        String original_path = getRealPathFromURI(uri);
        output = new String[]{"-i", original_path, "-an", "-ss", "" + a / 1000, "-t", "" + duration, dest.getAbsolutePath()};
        return output;
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
