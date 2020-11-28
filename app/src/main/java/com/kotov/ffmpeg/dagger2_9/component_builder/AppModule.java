package com.kotov.ffmpeg.dagger2_9.component_builder;

import android.app.Application;
import android.content.Context;

import com.kotov.ffmpeg.TrimActivity;
import com.kotov.ffmpeg.dagger2_9.ActivityComponentBuilder;
import com.kotov.ffmpeg.dagger2_9.subcomponent_builder.TrimActivityComponent;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = {TrimActivityComponent.class/*, SecondActivityComponent.class*/})
public class AppModule {

    private final Application context;

    public AppModule(Application context) {
        this.context = context;
    }

    @AppScope
    @Provides
    Application provideContext() {
        return context;
    }

    @Provides
    @IntoMap
    @ClassKey(TrimActivity.class)
    ActivityComponentBuilder provideFirstActivityBuilder(TrimActivityComponent.Builder builder) {
        return builder;
    }

   /* @Provides
    @IntoMap
    @ClassKey(SecondActivity.class)
    ActivityComponentBuilder provideSecondActivityBuilder(SecondActivityComponent.Builder builder) {
        return builder;
    }*/

}