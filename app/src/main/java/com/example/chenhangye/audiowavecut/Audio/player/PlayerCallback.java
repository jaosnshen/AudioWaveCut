package com.example.chenhangye.audiowavecut.Audio.player;

/**
 * Created by chenhangye on 2016/10/2.
 */
public interface PlayerCallback {

    void onPlay();

    void onProgress(int progress);

    void onPause();

    void onStop();
}
