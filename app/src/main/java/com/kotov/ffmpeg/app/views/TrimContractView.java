package com.kotov.ffmpeg.app.views;

import android.content.ContentValues;

import com.kotov.ffmpeg.MvpPresenter;


public class TrimContractView {

    public interface Trim {

        void showToast(int color, int drawable, int message);
        void showProgress();
        void hideProgress();
        ContentValues getContentValues();
    }

    public interface TrimAction extends MvpPresenter<Trim> {


    }



}
