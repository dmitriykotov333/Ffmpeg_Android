package com.kotov.ffmpeg.dagger2.component;

import android.app.Application;
import android.content.Context;

import com.kotov.ffmpeg.dagger.component.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {


    private final Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationScope
    Application applicationContext() {
        return mApplication;
    }
}