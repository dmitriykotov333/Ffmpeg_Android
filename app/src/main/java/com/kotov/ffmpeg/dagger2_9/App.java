package com.kotov.ffmpeg.dagger2_9;


import android.app.Application;
import android.content.Context;

public class App extends Application {

    private ComponentsHold componentsHolder;

    public static App getApp(Context context) {
        return (App)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        componentsHolder = new ComponentsHold(this);
        componentsHolder.init();
    }

    public ComponentsHold getComponentsHolder() {
        return componentsHolder;
    }
}