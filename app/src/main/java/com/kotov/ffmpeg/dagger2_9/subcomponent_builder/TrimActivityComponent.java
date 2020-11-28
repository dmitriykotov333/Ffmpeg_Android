package com.kotov.ffmpeg.dagger2_9.subcomponent_builder;

import com.kotov.ffmpeg.TrimActivity;
import com.kotov.ffmpeg.dagger2_9.ActivityComponent;
import com.kotov.ffmpeg.dagger2_9.ActivityComponentBuilder;

import dagger.Subcomponent;

@TrimActivityScope
@Subcomponent(modules = TrimActivityModule.class)
public interface TrimActivityComponent extends ActivityComponent<TrimActivity> {

    @Subcomponent.Builder
    interface Builder extends ActivityComponentBuilder<TrimActivityComponent, TrimActivityModule> {

    }


}