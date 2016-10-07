package com.example.chenhangye.audiowavecut.Audio;

import android.support.annotation.NonNull;

import com.example.chenhangye.audiowavecut.Audio.player.PlayerCallback;
import com.example.chenhangye.audiowavecut.Audio.player.PlayerState;

/**
 * Created by chenhangye on 2016/10/2.
 */

public interface AudioPlayer {

    // play
    void play(@NonNull String tagetPath);

    void pause();

    void resume();

    void stop();

    void release();

    // state
    PlayerState getState();

    // interactive
    void setOffSet(int offSet);

    // callback
    void addCallback(PlayerCallback callback);
}
