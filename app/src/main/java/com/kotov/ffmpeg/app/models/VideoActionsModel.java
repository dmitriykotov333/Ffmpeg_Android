package com.kotov.ffmpeg.app.models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.kotov.ffmpeg.di.video_action.VideoActionScope;
import com.kotov.ffmpeg.models.RealPathFromUri;
import com.kotov.ffmpeg.models.VideoActions;

import java.util.List;

import javax.inject.Inject;

@VideoActionScope
public class VideoActionsModel {

    private VideoActions videoActions;

    private Activity activity;

    @Inject
    public VideoActionsModel(VideoActions videoActions, Activity activity) {
        this.videoActions = videoActions;
        this.activity = activity;
    }


    public void setVideoActions(Activity activity, Context context, Uri uri, RealPathFromUri realPathFromUri, List<ImageView> imageViews) {
        videoActions.setData(activity, context, uri, realPathFromUri, imageViews);
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

    public interface CompleteCallback {
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
