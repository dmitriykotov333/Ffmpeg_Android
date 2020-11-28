package com.kotov.ffmpeg.dagger2_9.subcomponent_builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface TrimActivityScope {
}

