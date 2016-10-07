package com.example.chenhangye.audiowavecut.Audio;

import android.support.annotation.NonNull;
import android.view.SurfaceView;

import com.example.chenhangye.audiowavecut.Audio.recorder.RecordState;
import com.example.chenhangye.audiowavecut.Audio.recorder.RecorderCallback;

import java.util.LinkedList;

/**
 * Created by chenhangye on 2016/10/2.
 */

public interface AudioRecorder {
    // act
    void record(@NonNull String targetPath);

    void pause();

    void resume();

    void stop();

    void release();

    int getDuration();

    void setDuration(int duration);

    void clearCanvas();

    void reDrawCanvas();
    //获取波形图
    LinkedList<Double> getValumws();

    void setValumws(LinkedList<Double> linkedList, SurfaceView sf);
    // state
    RecordState getState();
    /**
     * 设置文件路径
     */
    void setFilePath(String pathFile);

    // callback
    void addCallback(RecorderCallback callback);
}
