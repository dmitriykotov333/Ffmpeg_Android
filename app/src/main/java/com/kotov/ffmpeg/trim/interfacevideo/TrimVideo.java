package com.kotov.ffmpeg.trim.interfacevideo;

import java.io.File;

public interface TrimVideo extends IVideoT, PathUri, IFile {
   File mkDir(String a);
}
