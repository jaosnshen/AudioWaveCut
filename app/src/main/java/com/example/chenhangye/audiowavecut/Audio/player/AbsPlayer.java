package com.example.chenhangye.audiowavecut.Audio.player;

import android.support.annotation.NonNull;

import com.example.chenhangye.audiowavecut.Audio.AudioPlayer;

/**
 * Created by chenhangye on 2016/10/2.
 */

public abstract class AbsPlayer implements AudioPlayer {
    PlayerState mState;
    PlayerCallback mCallback;

    // 状态层
    AbsPlayer() {
        this.mState = PlayerState.Stoped;
    }

    @Override
    public void play(String tagetPath){
        if (mState == PlayerState.Paused || mState == PlayerState.Stoped){
            this.mState = PlayerState.Playing;
            onAudioPlay(tagetPath);
        }
    }

    @Override
    public void pause(){
        if (mState == PlayerState.Playing){
            this.mState = PlayerState.Paused;
            onAudioPause();
        }
    }

    @Override
    public void resume(){
        if (mState == PlayerState.Paused){
            this.mState = PlayerState.Playing;
            onAudioResume();
        }
    }

    @Override
    public void stop(){
        if (mState == PlayerState.Paused || mState == PlayerState.Playing){
            this.mState = PlayerState.Stoped;
            onAudioStop();
        }
    }

    abstract void onAudioPlay(String targetPath);

    void onAudioResume(){}

    void onAudioPause(){}

    void onAudioStop(){}

    @Override
    public void setOffSet(int offSet){
    }

    @Override
    public void addCallback(PlayerCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public PlayerState getState() {
        return mState;
    }
}
