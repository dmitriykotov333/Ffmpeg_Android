package com.kotov.ffmpeg.mvp;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kotov.ffmpeg.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public List<ImageView> getAllImages() {
        return model.getBitmap();
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
