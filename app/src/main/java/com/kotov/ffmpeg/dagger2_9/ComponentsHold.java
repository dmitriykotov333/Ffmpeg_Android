package com.kotov.ffmpeg.dagger2_9;


import android.app.Application;
import android.content.Context;

import com.kotov.ffmpeg.dagger2_9.component_builder.AppComponent2_9;
import com.kotov.ffmpeg.dagger2_9.component_builder.AppModule;
import com.kotov.ffmpeg.dagger2_9.component_builder.DaggerAppComponent2_9;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ComponentsHold {

    private final Application context;

    @Inject
    Map<Class<?>, Provider<ActivityComponentBuilder>> builders;

    private Map<Class<?>, ActivityComponent> components;
    private AppComponent2_9 appComponent;

    public ComponentsHold(Application context) {
        this.context = context;
    }

    void init() {
        appComponent = DaggerAppComponent2_9.builder().appModule(new AppModule(context)).build();
        appComponent.injectComponentsHolder(this);
        components = new HashMap<>();
    }

    public AppComponent2_9 getAppComponent() {
        return appComponent;
    }


    public ActivityComponent getActivityComponent(Class<?> cls) {
        return getActivityComponent(cls, null);
    }

    public ActivityComponent getActivityComponent(Class<?> cls, ActivityModule module) {
        ActivityComponent component = components.get(cls);
        if (component == null) {
            ActivityComponentBuilder builder = builders.get(cls).get();
            if (module != null) {
                builder.module(module);
            }
            component = builder.build();
            components.put(cls, component);
        }
        return component;
    }

    public void releaseActivityComponent(Class<?> cls) {
        components.put(cls, null);

    }

}