package com.kotov.ffmpeg.trim.interfacevideo;

import java.io.FileNotFoundException;

public interface IVideoT {
    String[] trimVideo(int a, int b, String n) throws FileNotFoundException;
}
