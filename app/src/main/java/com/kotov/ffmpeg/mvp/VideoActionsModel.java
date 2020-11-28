package com.kotov.ffmpeg.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.kotov.ffmpeg.TrimActivity;
import com.kotov.ffmpeg.TrimmerUtils;
import com.kotov.ffmpeg.trim.VideoActions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VideoActionsModel {

    private final VideoActions videoActions;

    private Activity activity;

    public VideoActionsModel(VideoActions videoActions, Activity activity) {
        this.videoActions = videoActions;
        this.activity = activity;
    }

    public void getFrame(ContentValues contentValues, CompleteCallback completeCallback) {
        TrimVideoOnFramesTask trimVideoOnFramesTask = new TrimVideoOnFramesTask(completeCallback);
        trimVideoOnFramesTask.execute(contentValues);
    }

    public void getCrop(ContentValues contentValues, CompleteCallback completeCallback) {
        CropVideoTask cropVideoTask = new CropVideoTask(completeCallback);
        cropVideoTask.execute(contentValues);
    }

    public List<ImageView> getBitmap() {
        return videoActions.frameBySec();
    }

    interface CompleteCallback {
        void onComplete();
    }

    @SuppressLint("StaticFieldLeak")
    class TrimVideoOnFramesTask extends AsyncTask<ContentValues, Void, Void> {

        private final CompleteCallback callback;


        TrimVideoOnFramesTask(CompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ContentValues... voids) {
            ContentValues param = voids[0];
            FFmpeg.execute(videoActions.frames(param.getAsInteger("start"), param.getAsInteger("end"), param.getAsString("name")));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onComplete();
                activity.finish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class CropVideoTask extends AsyncTask<ContentValues, Void, Void> {

        private final CompleteCallback callback;


        CropVideoTask(CompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(ContentValues... voids) {
            ContentValues param = voids[0];
            FFmpeg.execute(videoActions.crop(param.getAsInteger("start"), param.getAsInteger("end"), param.getAsString("name")));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (callback != null) {
                callback.onComplete();
                activity.finish();
            }
        }
    }
}
