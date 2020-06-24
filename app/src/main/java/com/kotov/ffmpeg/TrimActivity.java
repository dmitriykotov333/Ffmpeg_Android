package com.kotov.ffmpeg;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.kotov.ffmpeg.trim.TrimFile;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.FileNotFoundException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class TrimActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView textViewLeft, textViewRight;
    private RangeSeekBar rangeSeekBar;
    private int duration;
    private String filePrefix;
    private Uri uri;
    private int code;
    private boolean isPlaying = false;
    private TrimFile trimFile;
    private androidx.appcompat.widget.AppCompatEditText editText;
    private androidx.cardview.widget.CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        videoView = findViewById(R.id.videoView);
        cardView = findViewById(R.id.cards);
        Intent i = getIntent();
        if (i != null) {
            code = i.getIntExtra("code", -1);
            getWindows(this);
            uri = Uri.parse(i.getStringExtra("uri"));
            isPlaying = true;
            videoView.setVideoURI(uri);
            videoView.setLayoutParams(videoFull());
            videoView.start();
        }
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
                    next.setOnClickListener(v -> {
                        filePrefix = Objects.requireNonNull(editText.getText()).toString();
                        try {
                            if (code == 0) {
                                trimFile.trimVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000,
                                        rangeSeekBar.getSelectedMaxValue().intValue() * 1000, filePrefix);
                            } else {
                                trimFile.trimVideoCadr(rangeSeekBar.getSelectedMinValue().intValue() * 1000,
                                        rangeSeekBar.getSelectedMaxValue().intValue() * 1000, filePrefix);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Intent myintent = new Intent(TrimActivity.this, ProgressBarActivity.class);
                        myintent.putExtra("duration", trimFile.getDuration());
                        myintent.putExtra("command", trimFile.getCommand());
                        myintent.putExtra("destination", trimFile.getDest().getAbsolutePath());
                        startActivity(myintent);
                        finish();
                    });
                } else {
                    next.setImageResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
                    next.setOnClickListener(v -> finish());
                }
            }
        });

        trimFile = new TrimFile(this, uri) {
            @Override
            public void trimVideoCadr(int startMs, int endMs, String fileName) throws FileNotFoundException {
                super.trimVideoCadr(startMs, endMs, fileName);
            }

            @Override
            public void trimVideo(int startMs, int endMs, String fileName) {
                super.trimVideo(startMs, endMs, fileName);
            }
        };
        setListeners();
    }

    private LinearLayout.LayoutParams videoFull() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        return params;
    }

    @SuppressLint("SetTextI18n")
    private void setListeners() {
        cardView.setOnClickListener(v -> {
            if (isPlaying) {
                videoView.pause();
                isPlaying = false;
            } else {
                videoView.start();
                isPlaying = true;
            }
        });
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            duration = mp.getDuration() / 1000;
            textViewLeft.setText("00 : 00 : 00");
            textViewRight.setText(getTime(mp.getDuration() / 1000));
            rangeSeekBar.setRangeValues(0, duration);
            rangeSeekBar.setSelectedMaxValue(duration);
            rangeSeekBar.setSelectedMinValue(0);
            rangeSeekBar.setEnabled(true);
            rangeSeekBar.setOnRangeSeekBarChangeListener((bar, minValue, maxValue) -> {
                videoView.seekTo((int) minValue * 1000);
                textViewLeft.setText(getTime((int) bar.getSelectedMinValue()));
                textViewRight.setText(getTime((int) bar.getSelectedMaxValue()));
            });
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000) {
                    videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                }
            }, 1000);
        });
    }

    @SuppressLint("DefaultLocale")
    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
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
