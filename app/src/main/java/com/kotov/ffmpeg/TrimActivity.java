package com.kotov.ffmpeg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.FFmpeg;
import com.kotov.ffmpeg.trim.VideoActions;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class TrimActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener {


    @BindView(R.id.textureView)
    TextureView mPreview;
    @BindView(R.id.tvvLeft)
    TextView textViewLeft;
    @BindView(R.id.tvvRight)
    TextView textViewRight;
    @BindView(R.id.seekbar)
    RangeSeekBar rangeSeekBar;
    @BindView(R.id.search_edit_frame)
    AppCompatEditText editText;
    @BindView(R.id.next)
    ImageView next;

    private Surface surface;
    private MediaPlayer mMediaPlayer;
    private VideoActions videoAction;
    private Uri uri;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        ButterKnife.bind(this);
        mMediaPlayer = new MediaPlayer();
        mPreview.setSurfaceTextureListener(this);
        Intent i = getIntent();
        if (i != null) {
            uri = Uri.parse(i.getStringExtra("uri"));
            code = i.getIntExtra("code", -1);
            editText.setHint(i.getStringExtra("name"));
        }
        if (code == 3) {
            getBackground(R.drawable.grad1);
        } else {
            getBackground(R.drawable.grad);
        }
        next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
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
                    next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                }
                next.setOnClickListener(v -> {
                    if (s.length() != 0) {
                        videoAction = new VideoActions(getApplicationContext(), uri,
                                rangeSeekBar.getSelectedMinValue().intValue() * 1000,
                                rangeSeekBar.getSelectedMaxValue().intValue() * 1000, s.toString());
                        if (code == 0) {
                            new AsyncTaskFfmpeg().execute(videoAction.crop());
                        } else {
                            new AsyncTaskFfmpeg().execute(videoAction.frames());
                        }
                    } else {
                        finish();
                    }
                });
            }
        });

        setListeners();
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskFfmpeg extends AsyncTask<String, Integer, Void> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(TrimActivity.this);
            progress.setTitle("Processing");
            progress.setMessage("Please wait...");
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Drawable drawable = new ProgressBar(TrimActivity.this).getIndeterminateDrawable().mutate();
                drawable.setColorFilter(ContextCompat.getColor(TrimActivity.this, R.color.colorAccent),
                        PorterDuff.Mode.SRC_IN);
                progress.setIndeterminateDrawable(drawable);
            }

            progress.setOnKeyListener((dialog, keyCode, event) -> {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    CustomToast.getToast(getApplicationContext(), R.drawable.ic_error_outline, R.string.please_wait, R.color.blue_500, Toast.LENGTH_LONG);

                }

                return true;
            });
            progress.show();
        }

        @Override
        protected Void doInBackground(String... command) {
            FFmpeg.execute(command);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            CustomToast.getToast(getApplicationContext(), R.drawable.ic_done, R.string.successfully, R.color.green, Toast.LENGTH_LONG);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaces, int width, int height) {
        surface = new Surface(surfaces);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mMediaPlayer.setOnCompletionListener(MediaPlayer::start);
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
            } else {
                mMediaPlayer.start();
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

    private void getBackground(int drawable) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(getResources().getDrawable(drawable, null));
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        } else {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.requestLayout();
        mMediaPlayer.start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        action(mp);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        action(mp);
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
}
