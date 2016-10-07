package com.example.chenhangye.audiowavecut.Audio;

import com.example.chenhangye.audiowavecut.Audio.encoder.EncoderCallback;

/**
 * Created by chenhangye on 2016/10/2.
 */

public interface AudioEncoder {
    void addEncoderCallback(EncoderCallback callback);

    void encode(String inputPath, String outputPath);
}
