package com.kotov.ffmpeg.trim;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import androidx.appcompat.app.AppCompatActivity;

public abstract class TrimFile extends AppCompatActivity {
    private int duration;
    private String[] command;
    private File dest;
    private String original_path;
    private Uri uri;
    private Context context;

    public TrimFile(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

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


    @SuppressLint("DefaultLocale")
    public void trimVideoCadr(int startMs, int endMs, String fileName) throws FileNotFoundException {
        File folder = mkDir(fileName);
        String filePrefix = "%05d";
        String fileExt = ".png";
        dest = new File(folder, filePrefix + fileExt);
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(dest)))) {
            original_path = getRealPathFromURI(uri);
            duration = (endMs - startMs) / 1000;
            command = new String[]{"-i", original_path, "-an", "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, dest.getAbsolutePath()};
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trimVideo(int startMs, int endMs, String fileName) {
        File folder = mkDir(fileName);
        String fileExt = ".mp4";
        dest = new File(folder, fileName + fileExt);

        original_path = getRealPathFromURI(uri);
        duration = (endMs - startMs) / 1000;
        command = new String[]{"-ss", "" + startMs / 1000, "-y", "-i", original_path, "-t", "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", dest.getAbsolutePath()};
        addVideo(dest, fileName);
    }

    private File mkDir(String fileName) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat("/") + fileName);
        if (!folder.exists()) {
            if (folder.mkdir()) {

            }
        } else {

        }
        return folder;
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

    public int getDuration() {
        return duration;
    }

    public File getDest() {
        return dest;
    }

    public String[] getCommand() {
        return command;
    }
}
