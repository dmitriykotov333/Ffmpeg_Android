package com.kotov.ffmpeg.app.presenters;

import android.content.ContentValues;
import android.text.TextUtils;
import android.widget.ImageView;


import com.kotov.ffmpeg.PresenterBase;
import com.kotov.ffmpeg.R;
import com.kotov.ffmpeg.app.enums.Constants;
import com.kotov.ffmpeg.app.models.VideoActionsModel;
import com.kotov.ffmpeg.app.views.TrimContractView;
import com.kotov.ffmpeg.di.video_action.VideoActionScope;

import java.util.List;

import javax.inject.Inject;

@VideoActionScope
public class VideoActionPresenter extends PresenterBase<TrimContractView.Trim> implements TrimContractView.TrimAction {


    private final VideoActionsModel model;
    private Constants.PinCodeMode codeMode;

    @Inject
    public VideoActionPresenter(VideoActionsModel model) {
        this.model = model;
    }

    public void setCodeMode(Constants.PinCodeMode codeMode) {
        this.codeMode = codeMode;
    }

    public List<ImageView> getAllImages() {
        return model.getBitmap();
    }

    public VideoActionsModel getModel() {
        return model;
    }

    public void action() {
        ContentValues cv = getView().getContentValues();
        if (TextUtils.isEmpty(cv.getAsString("name"))) {
            getView().showToast(R.color.red_line_chat, R.drawable.ic_error_outline, R.string.isEmpty);
            return;
        }
        getView().showProgress();
        if (Constants.PinCodeMode.FRAME == codeMode) {
            model.getFrame(cv, () -> {
                getView().hideProgress();
                getView().showToast(R.color.green, R.drawable.ic_done, R.string.successfully);
            });
        } else {
            model.getCrop(cv, () -> {
                getView().hideProgress();
                getView().showToast(R.color.green, R.drawable.ic_done, R.string.successfully);
            });
        }
    }

}
