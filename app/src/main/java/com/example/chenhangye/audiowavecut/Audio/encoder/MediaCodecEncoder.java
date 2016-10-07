package com.example.chenhangye.audiowavecut.Audio.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;

import com.example.chenhangye.audiowavecut.Audio.AudioConfig;
import com.example.chenhangye.audiowavecut.Audio.AudioEncoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by chenhangye on 2016/10/2.
 */

public class MediaCodecEncoder implements AudioEncoder {

    private static final String TAG = "MediaCodecEncoder";

    private EncoderCallback mCallback;

    public MediaCodecEncoder(){
    }

    @Override
    public void addEncoderCallback(EncoderCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void encode(String inputPath, String outputPath) {
        new EncoderTask().execute(inputPath, outputPath);
    }


    private class EncoderTask extends AsyncTask<String, Void, Void> {

        private static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
        private static final int CODEC_TIMEOUT = 5000;

        private int audioTrackId;
        private int totalBytesRead;
        private double presentationTimeUs;


        private MediaFormat mediaFormat;
        private MediaCodec mediaCodec;
        private MediaMuxer mediaMuxer;
        private ByteBuffer[] codecInputBuffers;
        private ByteBuffer[] codecOutputBuffers;
        private MediaCodec.BufferInfo bufferInfo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallback != null){
                mCallback.onStartEncode();
            }
        }

        @Override
        protected Void doInBackground(String...params) {

            String inputPath = params[0];
            String outputPath = params[1];

            // 一些初始化工作
            try {
                mediaFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE, AudioConfig.SAMPLE_RATE, AudioConfig.CHANNEL_COUNT);
                mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, AudioConfig.BITRATE);

                // init MediaCodec
                mediaCodec = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE);
                // configure
                mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mediaCodec.start();

                codecInputBuffers = mediaCodec.getInputBuffers();
                codecOutputBuffers = mediaCodec.getOutputBuffers();

                bufferInfo = new MediaCodec.BufferInfo();

                mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                totalBytesRead = 0;
                presentationTimeUs = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] tempBuffer = new byte[2 * AudioConfig.SAMPLE_RATE];
            boolean hasMoreData = true;
            boolean stop = false;

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(inputPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 初始化完毕, 开始转码
            while (!stop) {
                int inputBufferIndex = 0;
                int currentBatchRead = 0;
                while (inputBufferIndex != -1 && hasMoreData && currentBatchRead <= 50 * AudioConfig.SAMPLE_RATE) {
                    inputBufferIndex = mediaCodec.dequeueInputBuffer(CODEC_TIMEOUT);

                    if (inputBufferIndex >= 0) {
                        ByteBuffer buffer = codecInputBuffers[inputBufferIndex];
                        buffer.clear();

                        int bytesRead = 0;
                        try {
                            bytesRead = inputStream.read(tempBuffer, 0, buffer.limit());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (bytesRead == -1) {
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, (long) presentationTimeUs, 0);
                            hasMoreData = false;
                            stop = true;
                        } else {
                            totalBytesRead += bytesRead;
                            currentBatchRead += bytesRead;
                            buffer.put(tempBuffer, 0, bytesRead);
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, bytesRead, (long) presentationTimeUs, 0);
                            presentationTimeUs = 1000000L * (totalBytesRead / 2) / AudioConfig.SAMPLE_RATE;
                        }
                    }
                }

                int outputBufferIndex = 0;
                while (outputBufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIMEOUT);
                    if (outputBufferIndex >= 0) {
                        ByteBuffer encodedData = codecOutputBuffers[outputBufferIndex];
                        encodedData.position(bufferInfo.offset);
                        encodedData.limit(bufferInfo.offset + bufferInfo.size);

                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && bufferInfo.size != 0) {
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                        } else {
                            mediaMuxer.writeSampleData(audioTrackId, codecOutputBuffers[outputBufferIndex], bufferInfo);
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                        }
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        mediaFormat = mediaCodec.getOutputFormat();
                        audioTrackId = mediaMuxer.addTrack(mediaFormat);
                        mediaMuxer.start();
                    }
                }
            }

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            // 释放缓冲区
            mediaCodec.stop();
            mediaCodec.release();
            mediaMuxer.stop();
            mediaMuxer.release();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mCallback != null){
                mCallback.onFinishEncode();
            }
        }
    }
}
