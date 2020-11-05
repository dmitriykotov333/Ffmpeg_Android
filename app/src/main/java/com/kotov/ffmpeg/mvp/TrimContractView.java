package com.kotov.ffmpeg.mvp;

import android.content.ContentValues;

public interface TrimContractView {

    ContentValues getContentValues();
    void showProgress();
    void hideProgress();
    void showToast(int color, int drawable, int message);
}
