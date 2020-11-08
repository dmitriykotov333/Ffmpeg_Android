package com.kotov.ffmpeg.dagger.component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Scope
@Documented
@Retention(value = RUNTIME)
public @interface ApplicationScope {
}
