package com.kotov.ffmpeg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogMessage;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kotov.ffmpeg.dagger2_9.App;
import com.kotov.ffmpeg.dagger.subcomponent.ControllerComponent;
import com.kotov.ffmpeg.dagger.subcomponent.VideoActionsModule;
import com.kotov.ffmpeg.dagger2_9.subcomponent_builder.TrimActivityComponent;
import com.kotov.ffmpeg.dagger2_9.subcomponent_builder.TrimActivityModule;
import com.kotov.ffmpeg.mvp.Constants;
import com.kotov.ffmpeg.mvp.TrimContractView;
import com.kotov.ffmpeg.mvp.VideoActionPresenter;
import com.kotov.ffmpeg.mvp.VideoActionsModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;

public class TrimActivity extends AppCompatActivity implements TrimContractView {


    private static final String TAG = "TrimActivity";
    @BindView(R.id.player_view_lib)
    PlayerView playerView;
    @BindView(R.id.seekbar_controller)
    CrystalSeekbar seekbarController;


    @BindView(R.id.search_edit_frame)
    AppCompatEditText editText;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.image_play_pause)
    ImageView imagePlayPause;
    @BindView(R.id.range_seek_bar)
    CrystalRangeSeekbar seekbar;
    @BindView(R.id.txt_start_duration)
    TextView txtStartDuration;
    @BindView(R.id.txt_end_duration)
    TextView txtEndDuration;

    @BindViews({R.id.image_one, R.id.image_two, R.id.image_three, R.id.image_four, R.id.image_five, R.id.image_six, R.id.image_seven, R.id.image_eight})
    List<ImageView> imageViews;

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
                Toast.makeText(this, lastMinValue + " - " + lastMaxValue, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });
    }


    @Inject
    VideoActionPresenter presenter;

    private Surface surface;
    private Uri uri;
    private ProgressDialog progressDialog;
    private long lastMaxValue = 0;
    private long lastMinValue = 0;
    private long totalDuration;

    private SimpleExoPlayer videoPlayer;
    private boolean isValidVideo = true, isVideoEnded;
    private long currentDuration, lastClickedTime;
    private Handler seekHandler;
    private void initPlayer() {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector =
                    new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            videoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            playerView.requestFocus();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPlayer(videoPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setDataInView() {
        try {
         //   uri = Uri.parse(getIntent().getStringExtra(TrimVideo.TRIM_VIDEO_URI));
           // uri = Uri.parse(FileUtils.getPath(this, uri));
            //LogMessage.v("VideoUri:: " + uri);
            totalDuration = TrimmerUtils.getDuration(this, uri);
            imagePlayPause.setOnClickListener(v ->
                    onVideoClicked());
            playerView.getVideoSurfaceView().setOnClickListener(v ->
                    onVideoClicked());
            validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue);
                videoPlayer.setPlayWhenReady(true);
                return;
            }
            if ((currentDuration - lastMaxValue) > 0)
                seekTo(lastMinValue);
            videoPlayer.setPlayWhenReady(!videoPlayer.getPlayWhenReady());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void seekTo(long sec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(sec * 1000);
    }
    private void validate() {
        buildMediaSource(uri);
    }
    private void buildMediaSource(Uri mUri) {
        try {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mUri);
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.addListener(new Player.DefaultEventListener() {

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    switch (playbackState) {
                   case Player.STATE_ENDED:
                            imagePlayPause.setVisibility(View.VISIBLE);
                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                            startProgress();
                            imagePlayPause.setVisibility(videoPlayer.getPlayWhenReady() ? View.GONE :
                                    View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
            });
            setImageBitmaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startProgress() {
        updateSeekbar.run();
    }

    void stopRepeatingTask() {
        seekHandler.removeCallbacks(updateSeekbar);
    }
    Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            try {
                currentDuration = videoPlayer.getCurrentPosition() / 1000;
                if (!videoPlayer.getPlayWhenReady())
                    return;
                if (currentDuration <= lastMaxValue)
                    seekbarController.setMinStartValue((int) currentDuration).apply();
                else
                   videoPlayer.setPlayWhenReady(false);
            } finally {
                seekHandler.postDelayed(updateSeekbar, 1000);
            }
        }
    };
    private void setImageBitmaps() {
        presenter.getAllImages();

        seekbarController.setMaxValue(totalDuration).apply();
        seekbar.setMaxValue(totalDuration).apply();
        seekbar.setMaxStartValue((float) totalDuration).apply();
        seekbar.setGap(2).apply();
        lastMaxValue = totalDuration;
        seekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
          //  if (!hidePlayerSeek)
                seekbarController.setVisibility(View.VISIBLE);
        });
        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            long minVal = (long) minValue;
            long maxVal = (long) maxValue;
            if (lastMinValue != minVal) {
                seekTo((long) minValue);
            }
            lastMinValue = minVal;
            lastMaxValue = maxVal;
            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));

        });
        seekbarController.setOnSeekbarFinalValueListener(value -> {
            long value1 = (long) value;
            if (value1 < lastMaxValue && value1 > lastMinValue) {
                seekTo(value1);
                return;
            }
            if (value1 > lastMaxValue)
                seekbarController.setMinStartValue((int) lastMaxValue).apply();
            else if (value1 < lastMinValue) {
                seekbarController.setMinStartValue((int) lastMinValue).apply();
                if (videoPlayer.getPlayWhenReady())
                    seekTo(lastMinValue);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        ButterKnife.bind(this);
        Constants.PinCodeMode pinCodeMode = (Constants.PinCodeMode)
                getIntent().getSerializableExtra(Constants.EXTRA_MODE);
        Intent i = getIntent();
        if (i != null) {
            uri = Uri.parse(i.getStringExtra("uri"));
            editText.setHint(i.getStringExtra("name"));
            totalDuration = TrimmerUtils.getDuration(this, uri);
        }
        if (Constants.PinCodeMode.CROP == pinCodeMode) {
            getBackground(R.drawable.grad);
        } else {
            getBackground(R.drawable.grad1);
        }
        next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        next.setOnClickListener(v -> finish());

        TrimActivityComponent component =
                (TrimActivityComponent) App.getApp(this).getComponentsHolder().getActivityComponent(getClass(),
                        new TrimActivityModule(getApplicationContext(), pinCodeMode, TrimActivity.this, uri, imageViews));
        component.inject(this);
        presenter.attachView(this);
        seekHandler = new Handler();
        initPlayer();
        setDataInView();
    }


    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("start", lastMinValue * 1000);
        contentValues.put("end", lastMaxValue * 1000);
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
        if (isFinishing()) {
            App.getApp(this).getComponentsHolder().releaseActivityComponent(getClass());
        }
        presenter.detachView();
        if (videoPlayer != null)
            videoPlayer.release();
        stopRepeatingTask();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        videoPlayer.setPlayWhenReady(false);
    }



}

/*

public class TrimActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, TrimContractView {


    @BindView(R.id.textureView)
    TextureView mPreview;
    @BindView(R.id.search_edit_frame)
    AppCompatEditText editText;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.image_play_pause)
    ImageView image_play_pause;
    @BindView(R.id.range_seek_bar)
    CrystalRangeSeekbar seekbar;
    @BindView(R.id.txt_start_duration)
    TextView txtStartDuration;
    @BindView(R.id.txt_end_duration)
    TextView txtEndDuration;

    @BindViews({R.id.image_one, R.id.image_two, R.id.image_three, R.id.image_four, R.id.image_five, R.id.image_six, R.id.image_seven, R.id.image_eight})
    List<ImageView> imageViews;

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
                Toast.makeText(this, lastMinValue + " - " + lastMaxValue, Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });
    }


    @Inject
    VideoActionPresenter presenter;

    private Surface surface;
    private MediaPlayer mMediaPlayer;
    private Uri uri;
    private ProgressDialog progressDialog;
    private long lastMaxValue = 0;
    private long lastMinValue = 0;
    private long totalDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        ButterKnife.bind(this);
        Constants.PinCodeMode pinCodeMode = (Constants.PinCodeMode)
                getIntent().getSerializableExtra(Constants.EXTRA_MODE);
        Intent i = getIntent();
        if (i != null) {
            uri = Uri.parse(i.getStringExtra("uri"));
            editText.setHint(i.getStringExtra("name"));
            totalDuration = TrimmerUtils.getDuration(this, uri);
        }

        mMediaPlayer = new MediaPlayer();
        mPreview.setSurfaceTextureListener(this);

        if (Constants.PinCodeMode.CROP == pinCodeMode) {
            getBackground(R.drawable.grad);
        } else {
            getBackground(R.drawable.grad1);
        }
        next.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        next.setOnClickListener(v -> finish());
        image_play_pause.setVisibility(View.GONE);
        setListeners();

        //Dagger
        // App.getInstance().getVideoActionsComponent().newControllerComponent(new VideoActionsModule(getApplicationContext(),
        //       pinCodeMode, TrimActivity.this, uri)).inject(TrimActivity.this);

        //Dagger2
        // App.getApp(this).getComponentsHolder().
        //       getFirstActivityComponent(getApplicationContext(), pinCodeMode, TrimActivity.this, uri).inject(this);

        //Dagger2_9
        TrimActivityComponent component =
                (TrimActivityComponent) App.getApp(this).getComponentsHolder().getActivityComponent(getClass(),
                        new TrimActivityModule(getApplicationContext(), pinCodeMode, TrimActivity.this, uri, imageViews));
         component.inject(this);
        presenter.attachView(this);
        presenter.getAllImages();
        Toast.makeText(this, presenter.getAllImages().size() + "\n" + presenter.toString(), Toast.LENGTH_SHORT).show();
    }

    private void setImageBitmaps() {
        // long diff = totalDuration / 8;


       */
/* new Handler().postDelayed(() -> {
        int index = 0;
        for (Bitmap img : presenter.getAllImages()) {
            imageViews[index].setImageBitmap(img);
            index++;
        }

        }, 1000);
*//*



        seekbar.setMaxValue(totalDuration).apply();
        seekbar.setMaxStartValue((float) totalDuration).apply();
        lastMaxValue = totalDuration;
        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            long minVal = (long) minValue;
            long maxVal = (long) maxValue;
            if (lastMinValue != minVal) {
                seekTo(mMediaPlayer, (long) minValue);
            }
            lastMinValue = minVal;
            lastMaxValue = maxVal;
            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));

        });

    }

    @Override
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("start", lastMinValue * 1000);
        contentValues.put("end", lastMaxValue * 1000);
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
        if (isFinishing()) {
            //dagger2
            //App.getApp(this).getComponentsHolder().releaseFirstActivityComponent();

            //dagger2_9
            App.getApp(this).getComponentsHolder().releaseActivityComponent(getClass());
        }
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
            mMediaPlayer.setOnVideoSizeChangedListener((mp, width1, height1) -> {
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                android.view.ViewGroup.LayoutParams lp = mPreview.getLayoutParams();
                lp.width = screenWidth;
                lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
                mPreview.setLayoutParams(lp);
            });
            mMediaPlayer.prepareAsync();
            setImageBitmaps();
            mMediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
                image_play_pause.setVisibility(View.VISIBLE);
            } else {
                mMediaPlayer.start();
                image_play_pause.setVisibility(View.GONE);
            }
        });
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
        uri = Uri.parse(getIntent().getStringExtra("uri"));
    }

    private void seekTo(MediaPlayer mp, long sec) {
        if (mp != null)
            mp.seekTo((int) (sec * 1000));
    }

}
*/
