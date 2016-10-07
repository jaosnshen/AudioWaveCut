package com.example.chenhangye.audiowavecut.Util.CutAudio;

import android.support.annotation.NonNull;

/**
 * Created by chenhangye on 2016/10/6.
 */

public interface IAudioCut {
    void addCallBack(@NonNull AudioCut.cutAudioCallBack mCallback);

    void start(String inputPath,String outputPath,int fromTime,int duration);
}
