package com.kotov.ffmpeg.di.video_action;


import com.kotov.ffmpeg.app.presenters.VideoActionPresenter;
import com.kotov.ffmpeg.app.views.TrimContractView;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class VideoActionActivityBuildersModule {

    @VideoActionScope
    @Binds
    public abstract TrimContractView.TrimAction bindVideoActionPresenter(VideoActionPresenter presenter);

}
