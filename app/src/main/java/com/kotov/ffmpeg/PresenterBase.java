package com.kotov.ffmpeg;

public abstract class PresenterBase<T> implements MvpPresenter<T> {

    private T view;

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    public T getView() {
        return view;
    }

    public boolean isViewAttached() {
        return view != null;
    }

}