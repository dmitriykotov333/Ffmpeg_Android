package com.kotov.ffmpeg.di.video_action;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class VideoActionModule {

    @VideoActionScope
    @Provides
    static Context provideContext(Application application){
        return application.getApplicationContext();
    }

    @VideoActionScope
    @Provides
    static Activity provideActivity(){
        return new Activity();
    }


}
