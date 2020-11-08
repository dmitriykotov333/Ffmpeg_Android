package com.kotov.ffmpeg.dagger.component;

import com.kotov.ffmpeg.dagger.subcomponent.VideoActionsModule;
import com.kotov.ffmpeg.dagger.subcomponent.ControllerComponent;

import dagger.Component;

@ApplicationScope
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    ControllerComponent newControllerComponent(VideoActionsModule module);

}