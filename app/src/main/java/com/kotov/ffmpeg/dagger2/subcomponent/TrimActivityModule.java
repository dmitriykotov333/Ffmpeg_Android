package com.kotov.ffmpeg.dagger2.subcomponent;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.kotov.ffmpeg.dagger.subcomponent.ControllerScope;
import com.kotov.ffmpeg.mvp.Constants;
import com.kotov.ffmpeg.mvp.VideoActionPresenter;
import com.kotov.ffmpeg.mvp.VideoActionsModel;
import com.kotov.ffmpeg.trim.VideoActions;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class TrimActivityModule {

    private final Context context;
    private final Uri uri;
    private final Activity activity;
    private final Constants.PinCodeMode pinCodeMode;
    private final List<ImageView> imageViews;
    public TrimActivityModule(Context context, Constants.PinCodeMode pinCodeMode, Activity activity, Uri uri, List<ImageView> imageViews) {
        this.context = context;
        this.pinCodeMode = pinCodeMode;
        this.activity = activity;
        this.uri = uri;
        this.imageViews = imageViews;
    }


    @FirstActivityScope
    @Provides
    Context provideContext() {
        return context;
    }

    @FirstActivityScope
    @Provides
    Activity provideActivity() {
        return activity;
    }

    @FirstActivityScope
    @Provides
    Constants.PinCodeMode provideConstantsPinCodeMode() {
        return pinCodeMode;
    }

    @FirstActivityScope
    @Provides
    Uri provideUri() {
        return uri;
    }

    @FirstActivityScope
    @Provides
    List<ImageView> provideImageView() {
        return imageViews;
    }
    @FirstActivityScope
    @Provides
    VideoActions provideVideoActions(Context context, Uri uri, Activity activity, List<ImageView> imageViews) {
        return new VideoActions(context, uri, activity, imageViews);
    }

    @FirstActivityScope
    @Provides
    VideoActionsModel provideVideoActionsModel(VideoActions videoActions, Activity activity) {
        return new VideoActionsModel(videoActions, activity);
    }

    @FirstActivityScope
    @Provides
    VideoActionPresenter provideVideoActionPresenter(VideoActionsModel videoActionsModel, Constants.PinCodeMode pinCodeMode) {
        return new VideoActionPresenter(videoActionsModel, pinCodeMode);
    }
}
