package com.kotov.ffmpeg.dagger.subcomponent;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.kotov.ffmpeg.dagger.subcomponent.ControllerScope;
import com.kotov.ffmpeg.dagger2_9.subcomponent_builder.TrimActivityScope;
import com.kotov.ffmpeg.mvp.Constants;
import com.kotov.ffmpeg.mvp.VideoActionPresenter;
import com.kotov.ffmpeg.mvp.VideoActionsModel;
import com.kotov.ffmpeg.trim.VideoActions;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class VideoActionsModule {

    private final Context context;
    private final Uri uri;
    private final Activity activity;
    private final Constants.PinCodeMode pinCodeMode;
    private final List<ImageView> imageViews;

    public VideoActionsModule(Context context, Constants.PinCodeMode pinCodeMode, Activity activity, Uri uri, List<ImageView> imageViews) {
        this.context = context;
        this.pinCodeMode = pinCodeMode;
        this.activity = activity;
        this.uri = uri;
        this.imageViews = imageViews;
    }


    @ControllerScope
    @Provides
    Context provideContext() {
        return context;
    }

    @ControllerScope
    @Provides
    Activity provideActivity() {
        return activity;
    }

    @ControllerScope
    @Provides
    Constants.PinCodeMode provideConstantsPinCodeMode() {
        return pinCodeMode;
    }

    @ControllerScope
    @Provides
    Uri provideUri() {
        return uri;
    }

    @ControllerScope
    @Provides
    List<ImageView> provideImageView() {
        return imageViews;
    }

    @ControllerScope
    @Provides
    VideoActions provideVideoActions(Context context, Uri uri, Activity activity, List<ImageView> imageViews) {
        return new VideoActions(context, uri, activity, imageViews);
    }

    @ControllerScope
    @Provides
    VideoActionsModel provideVideoActionsModel(VideoActions videoActions, Activity activity) {
        return new VideoActionsModel(videoActions, activity);
    }

    @ControllerScope
    @Provides
    VideoActionPresenter provideVideoActionPresenter(VideoActionsModel videoActionsModel, Constants.PinCodeMode pinCodeMode) {
        return new VideoActionPresenter(videoActionsModel, pinCodeMode);
    }
}
