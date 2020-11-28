package com.kotov.ffmpeg.dagger2;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.kotov.ffmpeg.dagger.subcomponent.VideoActionsModule;
import com.kotov.ffmpeg.dagger2.component.AppComponent;
import com.kotov.ffmpeg.dagger2.subcomponent.TrimActivityComponent;
import com.kotov.ffmpeg.dagger2.subcomponent.TrimActivityModule;
import com.kotov.ffmpeg.mvp.Constants;
import com.kotov.ffmpeg.mvp.VideoActionsModel;

import java.util.List;

public class ComponentsHolder {

    private final Application context;

    private AppComponent appComponent;
    private TrimActivityComponent firstActivityComponent;

    public ComponentsHolder(Application context) {
        this.context = context;
    }

    void init() {
        //appComponent = DaggerAppComponent.builder().appModule(new AppModule(context)).build();
    }

    // AppComponent

    public AppComponent getAppComponent() {
        return appComponent;
    }




    public TrimActivityComponent getFirstActivityComponent(Context context, Constants.PinCodeMode pinCodeMode, Activity activity, Uri uri, List<ImageView> imageViewList) {
        if (firstActivityComponent == null) {
            firstActivityComponent = getAppComponent().createTrimActivityComponent(new TrimActivityModule(context, pinCodeMode, activity, uri, imageViewList));
        }
        return firstActivityComponent;
    }

    public void releaseFirstActivityComponent() {
        firstActivityComponent = null;
    }



}