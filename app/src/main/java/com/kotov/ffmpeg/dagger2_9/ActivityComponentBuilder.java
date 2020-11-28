package com.kotov.ffmpeg.dagger2_9;

public interface ActivityComponentBuilder<C extends ActivityComponent, M extends ActivityModule>   {
    C build();
    ActivityComponentBuilder<C,M> module(M module);
}