package com.kotov.ffmpeg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.kotov.ffmpeg.custompager.CustomViewPager;
import com.kotov.ffmpeg.custompager.PagerAdapter;
import com.kotov.ffmpeg.fragments.TabFragment1;
import com.kotov.ffmpeg.fragments.TabFragment2;
import com.kotov.ffmpeg.mvp.Constants;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    CustomViewPager viewPager;
    @BindView(R.id.search_edit_frame)
    AppCompatEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeColor(R.drawable.grad);
        setContentView(R.layout.activity_main);
        isStoragePermissionGranted();
        ButterKnife.bind(this);
        editText.setClickable(false);
        editText.setFocusable(false);
        editText.setEnabled(false);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tabLayout.getSelectedTabPosition() == 0) {
                    changeColor(R.drawable.grad);
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    changeColor(R.drawable.grad1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 1);
            }
        }
    }

    private void setupViewPager(CustomViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), getResources().getString(R.string.tab_labell));
        adapter.addFragment(new TabFragment2(), getResources().getString(R.string.tab_label2));
        viewPager.setAdapter(adapter);
    }

    private void changeColor(int drawable) {
        Window window = getWindow();
        Drawable background = getResources().getDrawable(drawable, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
    }

    @SuppressLint("IntentReset")
    public void onButton(View view) {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        if (tabLayout.getSelectedTabPosition() == 0) {
            startActivityForResult(i, 100);
        } else {
            startActivityForResult(i, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Intent i = new Intent(MainActivity.this, TrimActivity.class);
            i.putExtra("uri", Objects.requireNonNull(data.getData()).toString());
            if (requestCode == 100 && resultCode == RESULT_OK) {
                i.putExtra("name", "Change video name");
                i.putExtra(Constants.EXTRA_MODE, Constants.PinCodeMode.CROP);
            }
            if (requestCode == 101 && resultCode == RESULT_OK) {
                i.putExtra("name", "Enter the name of the folder where frames are stored");
                i.putExtra(Constants.EXTRA_MODE, Constants.PinCodeMode.FRAME);
            }
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}