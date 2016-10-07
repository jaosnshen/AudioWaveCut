package com.example.chenhangye.audiowavecut.Audio.recorder;

/**
 * Created by chenhangye on 2016/10/2.
 */

public interface RecorderCallback {
    void onRecord();

    void onProgress(int sec);

    void onPause();

    void onStop();
}
