package com.kotov.ffmpeg.di;


import com.kotov.ffmpeg.app.ui.activities.TrimActivity;
import com.kotov.ffmpeg.di.video_action.VideoActionActivityBuildersModule;
import com.kotov.ffmpeg.di.video_action.VideoActionModule;
import com.kotov.ffmpeg.di.video_action.VideoActionScope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {

    @VideoActionScope
    @ContributesAndroidInjector(
            modules = {
                    VideoActionActivityBuildersModule.class,
                    VideoActionModule.class
            }
    )
    abstract TrimActivity contributeTrimActivity();

}
