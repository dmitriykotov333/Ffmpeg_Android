package com.kotov.ffmpeg.dagger2.subcomponent;

import com.kotov.ffmpeg.TrimActivity;

import dagger.Subcomponent;

@FirstActivityScope
@Subcomponent(modules = TrimActivityModule.class)
public interface  TrimActivityComponent {
    void inject(TrimActivity trimActivity);
}
