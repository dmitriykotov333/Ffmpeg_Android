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

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,1);
            }
        }  //permission is automatically granted on sdk<23 upon installation

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindows(this);
        isStoragePermissionGranted();
        androidx.appcompat.widget.AppCompatEditText editText = findViewById(R.id.search_edit_frame);
        editText.setClickable(false);
        editText.setFocusable(false);
        editText.setEnabled(false);

        CustomViewPager viewPager = findViewById(R.id.pager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new
                                                   TabLayout.OnTabSelectedListener() {
                                                       @Override
                                                       public void onTabSelected(TabLayout.Tab tab) {
                                                           viewPager.setCurrentItem(tab.getPosition());
                                                           if (tabLayout.getSelectedTabPosition() == 0) {
                                                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                   Window window = getWindow();
                                                                   Drawable background = getResources().getDrawable(R.drawable.grad);
                                                                   window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                                   window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                                                                   window.setBackgroundDrawable(background);
                                                               }
                                                           } else if (tabLayout.getSelectedTabPosition() == 1) {
                                                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                   Window window = getWindow();
                                                                   Drawable background = getResources().getDrawable(R.drawable.grad1);
                                                                   window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                                   window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
                                                                   window.setBackgroundDrawable(background);
                                                               }
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

    private void setupViewPager(CustomViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), getResources().getString(R.string.tab_labell));
        adapter.addFragment(new TabFragment2(), getResources().getString(R.string.tab_label2));
        viewPager.setAdapter(adapter);
    }

    @SuppressLint("IntentReset")
    public void onButton(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                i.putExtra("code", 0);
            }
            if (requestCode == 101 && resultCode == RESULT_OK) {
                i.putExtra("name", "Enter the name of the folder where frames are stored");
                i.putExtra("code", 1);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void getWindows(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.grad);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
}