package com.kotov.ffmpeg.dagger2.component;

import com.kotov.ffmpeg.dagger2.subcomponent.TrimActivityComponent;
import com.kotov.ffmpeg.dagger2.subcomponent.TrimActivityModule;

import dagger.Component;

@AppScope
@Component(modules = AppModule.class)
public interface AppComponent {
    TrimActivityComponent createTrimActivityComponent(TrimActivityModule trimActivityModule);
}