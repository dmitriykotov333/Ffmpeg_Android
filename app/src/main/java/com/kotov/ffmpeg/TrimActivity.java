package com.kotov.ffmpeg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.kotov.ffmpeg.mvp.Constants;
import com.kotov.ffmpeg.mvp.TrimContractView;
import com.kotov.ffmpeg.mvp.VideoActionPresenter;
import com.kotov.ffmpeg.mvp.VideoActionsModel;
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
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

public class TrimActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener, TrimContractView {


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

    @OnTextChanged(value = R.id.search_edit_frame, callback = AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        if (s.length() != 0) {
            next.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        } else {
            next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }
        next.setOnClickListener(v -> {
            if (s.length() != 0) {
                presenter.action();
            } else {
                finish();
            }
        });
    }

    private Surface surface;
    private MediaPlayer mMediaPlayer;
    private VideoActionPresenter presenter;
    private Uri uri;
    private int code;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        ButterKnife.bind(this);
        mMediaPlayer = new MediaPlayer();
        mPreview.setSurfaceTextureListener(this);
        Intent i = getIntent();
        Constants.PinCodeMode pinCodeMode = (Constants.PinCodeMode)
                getIntent().getSerializableExtra(Constants.EXTRA_MODE);
        if (i != null) {
            uri = Uri.parse(i.getStringExtra("uri"));
            editText.setHint(i.getStringExtra("name"));
        }
        if (Constants.PinCodeMode.CROP == pinCodeMode) {
            getBackground(R.drawable.grad);
        } else {
            getBackground(R.drawable.grad1);
        }
        next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        next.setOnClickListener(v -> finish());
        setListeners();

        VideoActions videoActions = new VideoActions(getApplicationContext(), uri);
        VideoActionsModel videoActionsModel = new VideoActionsModel(videoActions, this);
        presenter = new VideoActionPresenter(videoActionsModel, pinCodeMode);
        presenter.attachView(this);
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("start", rangeSeekBar.getSelectedMinValue().intValue() * 1000);
        contentValues.put("end", rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
        contentValues.put("name", Objects.requireNonNull(editText.getText()).toString());
        return contentValues;
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "Processing", getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                CustomToast.getToast(getApplicationContext(), R.drawable.ic_error_outline, R.string.please_wait, R.color.blue_500, Toast.LENGTH_LONG);
            }
            return true;
        });
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showToast(int color, int drawable, int message) {
        CustomToast.getToast(getApplicationContext(), drawable, message, color, Toast.LENGTH_LONG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
