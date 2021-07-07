package com.kotov.ffmpeg.interfaces;

import java.io.FileNotFoundException;

public interface VideoActionImpl {
    String[] action(int a, int b, String n) throws FileNotFoundException;
}
