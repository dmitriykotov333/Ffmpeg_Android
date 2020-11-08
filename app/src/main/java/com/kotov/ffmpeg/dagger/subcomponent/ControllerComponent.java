package com.kotov.ffmpeg.dagger.subcomponent;

import com.kotov.ffmpeg.TrimActivity;

import dagger.Subcomponent;

@ControllerScope
@Subcomponent(modules = {VideoActionsModule.class})
public interface ControllerComponent {

    void inject(TrimActivity trimActivity);

}