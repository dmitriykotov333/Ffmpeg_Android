package com.kotov.ffmpeg.dagger;

import android.app.Application;

import com.kotov.ffmpeg.dagger.component.ApplicationComponent;
import com.kotov.ffmpeg.dagger.component.DaggerApplicationComponent;

public class App extends Application {

    private static App instance;

    private ApplicationComponent applicationComponent;
  
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationComponent = DaggerApplicationComponent.create();
    }

    public static App getInstance() {
        return instance;
    }

    public ApplicationComponent getVideoActionsComponent() {
        return applicationComponent;
    }
}
