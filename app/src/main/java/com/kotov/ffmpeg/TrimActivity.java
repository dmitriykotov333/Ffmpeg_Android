package com.kotov.ffmpeg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.kotov.ffmpeg.trim.VideoActions;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class TrimActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;
    private Surface surface;

    private TextView textViewLeft, textViewRight;
    private RangeSeekBar rangeSeekBar;
    private Uri uri;
    private int code;
    private boolean isPlaying = true;
    private androidx.appcompat.widget.AppCompatEditText editText;

    private VideoActions videoAction;


    private ServiceConnection mConnection;
    private FFMpegService ffMpegService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        Intent i = getIntent();
        if (i != null) {
            uri = Uri.parse(i.getStringExtra("uri"));
            code = i.getIntExtra("code", -1);
            isPlaying = true;
        }
        getWindows(this);
        mPreview = findViewById(R.id.textureView);
        mMediaPlayer = new MediaPlayer();
        mPreview.setSurfaceTextureListener(this);

        textViewRight = findViewById(R.id.tvvRight);
        textViewLeft = findViewById(R.id.tvvLeft);
        rangeSeekBar = findViewById(R.id.seekbar);
        ImageView next = findViewById(R.id.next);
        editText = findViewById(R.id.search_edit_frame);
        editText.setClickable(true);
        editText.setFocusable(true);
        editText.setHint(getIntent().getStringExtra("name"));
        next.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
        next.setOnClickListener(v -> finish());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    next.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    next.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
                }
                next.setOnClickListener(v -> {
                    if (s.length() != 0) {
                        videoAction = new VideoActions(getApplicationContext(), uri,
                                rangeSeekBar.getSelectedMinValue().intValue() * 1000,
                                rangeSeekBar.getSelectedMaxValue().intValue() * 1000,
                                Objects.requireNonNull(editText.getText()).toString());
                        if (code == 0) {
                            ffmpegServices(videoAction.getDuration(), videoAction.crop(), videoAction.getFile(videoAction.crop()));
                        } else {
                            ffmpegServices(videoAction.getDuration(), videoAction.frames(), videoAction.getFile(videoAction.frames()));
                        }
                    } else {
                        finish();
                    }
                });
            }
        });

        setListeners();
    }


    private void ffmpegServices(int duration, String[] command, String path) {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.DialogFullscreen);
        progressDialog.setProgressStyle(1);
        progressDialog.setIcon(R.drawable.wait);
        progressDialog.setTitle("Processsing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setOnKeyListener((dialog, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                new CustomToast(getApplicationContext()).getToast(R.drawable.ic_error_outline, R.string.please_wait, R.color.blue_500, Toast.LENGTH_LONG);

            }

            return true;
        });
        Intent myIntent = new Intent(TrimActivity.this, FFMpegService.class);
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
                        progressDialog.setProgress(integer);

                    }
                    if (integer == 100) {
                        progressDialog.dismiss();
                        stopService(myIntent);
                        finish();
                    }
                };
                ffMpegService.getPercentage().observe(TrimActivity.this, resultObserver);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConnection != null) {
            unbindService(mConnection);
        }
        finish();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaces, int width, int height) {
        surface = new Surface(surfaces);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.start();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        mPreview.getSurfaceTexture().release();
        if (this.surface != null) {
            this.surface.release();
            this.surface = null;
        }

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaces) {
        surface = new Surface(surfaces);
    }


    private void setListeners() {
        mPreview.setOnClickListener(v -> {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                isPlaying = false;
            } else {
                mMediaPlayer.start();
                isPlaying = true;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d : %02d : %02d", hr, mn, sec);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.requestLayout();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        action(mp);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        action(mMediaPlayer);
    }

    private void action(MediaPlayer mp) {
        textViewLeft.setText(R.string._00_00_00);
        textViewRight.setText(getTime(mp.getDuration() / 1000));
        rangeSeekBar.setRangeValues(0, mp.getDuration() / 1000);
        rangeSeekBar.setSelectedMaxValue(mp.getDuration() / 1000);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setEnabled(true);
        rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {
            mMediaPlayer.seekTo((int) minValue * 1000);
            textViewLeft.setText(getTime((int) bar.getSelectedMinValue()));
            textViewRight.setText(getTime((int) bar.getSelectedMaxValue()));
        });
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        action(mp);
    }
    private void getWindows(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background;
            if (code == 0) {
                background = activity.getResources().getDrawable(R.drawable.grad);
            } else {
                background = activity.getResources().getDrawable(R.drawable.grad1);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}
