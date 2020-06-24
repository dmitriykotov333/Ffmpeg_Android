package com.kotov.ffmpeg;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


import androidx.lifecycle.MutableLiveData;

public class FFMpegService extends Service {
    private FFmpeg fFmpeg;
    private int duration;
    private String[] command;
    protected Callbacks activity;
    private MutableLiveData<Integer> percentage;
    private IBinder binder = new LocalBinder();

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            duration = Integer.parseInt(intent.getStringExtra("duration"));
            command = intent.getStringArrayExtra("command");
            try {
                loadFFMpegBinary();
                execFFMpegCommand();
            } catch (FFmpegNotSupportedException | FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void execFFMpegCommand() throws FFmpegCommandAlreadyRunningException {
        fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String message) {
                super.onFailure(message);
            }

            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            @Override
            public void onProgress(String message) {
                String[] arr;
                if (message.contains("time=")) {
                    arr = message.split("time=");
                    //Поменять название переменных
                    String y = arr[1];
                    String[] up = y.split(":");
                    String[] gone = up[2].split(" ");
                    String seconds = gone[0];
                    int hours = Integer.parseInt(up[0]);
                    hours = hours * 3600;
                    int min = Integer.parseInt(up[1]);
                    min = min * 60;
                    float sec = Float.valueOf(seconds);
                    float timeInSec = hours + min + sec;
                    percentage.setValue((int) ((timeInSec / duration) * 100));
                }
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                percentage.setValue(100);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            loadFFMpegBinary();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
        percentage = new MutableLiveData<>();
    }

    private void loadFFMpegBinary() throws FFmpegNotSupportedException {
        if (fFmpeg == null) {
            fFmpeg = FFmpeg.getInstance(this);
        }
        fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
            @Override
            public void onFailure() {
                super.onFailure();
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
            }
        });
    }

    public FFMpegService() {
        super();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class LocalBinder extends Binder {
        FFMpegService getServiceInstance() {
            return FFMpegService.this;
        }
    }

    public void registerClient(Activity activity) {
        this.activity = (Callbacks) activity;
    }

    public MutableLiveData<Integer> getPercentage() {
        return percentage;
    }

    public interface Callbacks {
        void updateClient(float data);
    }
}
