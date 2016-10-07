package com.example.chenhangye.audiowavecut.Audio.recorder;

import com.example.chenhangye.audiowavecut.Audio.AudioRecorder;

/**
 * Created by chenhangye on 2016/10/2.
 */

public abstract class AbsRecorder implements AudioRecorder {
    RecordState mState;
    RecorderCallback mCallback;


    AbsRecorder() {
        this.mState = RecordState.Stoped;
    }

    @Override
    public void record(String targetPath) {
        if (mState == RecordState.Stoped){
            this.mState = RecordState.Recording;
            onAudioRecord(targetPath);
        }
    }

    @Override
    public void pause() {
        if (mState == RecordState.Recording){
            this.mState = RecordState.Paused;
            onAudioPause();
        }
    }

    @Override
    public void resume() {
        if (mState == RecordState.Paused){
            this.mState = RecordState.Recording;
            onAudioResume();
        }
    }

    @Override
    public void stop() {
        if (mState == RecordState.Paused || mState == RecordState.Recording){
            this.mState = RecordState.Stoped;
            onAudioStop();
        }
    }

    @Override
    public void release() {
        onAudioRelease();
    }

    abstract void onAudioRecord(String targetPath);
    void onAudioPause(){}
    void onAudioResume(){}
    void onAudioStop(){}
    abstract void onAudioRelease();

    @Override
    public RecordState getState() {
        return mState;
    }

    @Override
    public void addCallback(RecorderCallback callback) {
        this.mCallback = callback;
    }
}
