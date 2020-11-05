package com.kotov.ffmpeg.mvp;

import android.content.ContentValues;
import android.text.TextUtils;

import com.kotov.ffmpeg.R;

public class VideoActionPresenter {

    private TrimContractView view;

    private final VideoActionsModel model;
    private Constants.PinCodeMode codeMode;

    public VideoActionPresenter(VideoActionsModel model, Constants.PinCodeMode codeMode) {
        this.model = model;
        this.codeMode = codeMode;
    }

    public void attachView(TrimContractView view) {
        this.view = view;
    }

    public void detachView() {
        view = null;
    }

    public void action() {
        ContentValues cv = view.getContentValues();
        if (TextUtils.isEmpty(cv.getAsString("name"))) {
            view.showToast(R.color.red_line_chat, R.drawable.ic_error_outline, R.string.isEmpty);
            return;
        }
        view.showProgress();
        if (Constants.PinCodeMode.FRAME == codeMode) {
            model.getFrame(cv, () -> {
                view.hideProgress();
                view.showToast(R.color.green, R.drawable.ic_done, R.string.successfully);
            });
        } else {
            model.getCrop(cv, () -> {
                view.hideProgress();
                view.showToast(R.color.green, R.drawable.ic_done, R.string.successfully);
            });
        }
    }
}
