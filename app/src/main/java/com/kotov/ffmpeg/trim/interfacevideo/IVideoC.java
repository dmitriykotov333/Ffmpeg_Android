package com.kotov.ffmpeg.trim.interfacevideo;

import java.io.FileNotFoundException;

public interface IVideoC {
    String[] trimVideoCadr(int a, int b, String n) throws FileNotFoundException;
}
