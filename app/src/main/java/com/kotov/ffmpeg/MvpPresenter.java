package com.kotov.ffmpeg;

public interface MvpPresenter<V> {

    void attachView(V mvpView);

    void detachView();

}