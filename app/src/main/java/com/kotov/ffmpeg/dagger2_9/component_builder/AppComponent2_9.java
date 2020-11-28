package com.kotov.ffmpeg.dagger2_9.component_builder;

import com.kotov.ffmpeg.dagger2_9.ComponentsHold;

import dagger.Component;

@AppScope
@Component(modules = AppModule.class)
public interface AppComponent2_9 {
    void injectComponentsHolder(ComponentsHold componentsHolder);
}