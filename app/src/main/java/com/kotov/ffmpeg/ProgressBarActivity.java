package com.kotov.ffmpeg;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.widget.Toast;
import com.dinuscxj.progressbar.CircleProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class ProgressBarActivity extends AppCompatActivity {
    private CircleProgressBar circleProgressBar;
    private ServiceConnection mConnection;
    private FFMpegService ffMpegService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);
        circleProgressBar = findViewById(R.id.circleProgressBar);
        circleProgressBar.setMax(100);
        Intent i = getIntent();
        if (i != null) {
            int duration = i.getIntExtra("duration", 0);
            String[] command = i.getStringArrayExtra("command");
            String path = i.getStringExtra("destination");
            Intent myIntent = new Intent(ProgressBarActivity.this, FFMpegService.class);
            myIntent.putExtra("duration", String.valueOf(duration));
            myIntent.putExtra("command", command);
            myIntent.putExtra("destination", path);
            startService(myIntent);

            mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    FFMpegService.LocalBinder binder = (FFMpegService.LocalBinder) service;
                    ffMpegService = binder.getServiceInstance();
                    ffMpegService.registerClient(getParent());
                    final Observer<Integer> resultObserver = integer -> {
                        if (integer < 100) {
                            circleProgressBar.setProgress(integer);
                        }
                        if (integer == 100) {
                            circleProgressBar.setProgress(integer);
                            stopService(myIntent);
                            finish();
                        }
                    };
                    ffMpegService.getPercentage().observe(ProgressBarActivity.this, resultObserver);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };

            bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConnection != null) {
            unbindService(mConnection);
            Toast.makeText(getApplicationContext(), getString(R.string.successfully), Toast.LENGTH_SHORT).show();
        }
    }
}
