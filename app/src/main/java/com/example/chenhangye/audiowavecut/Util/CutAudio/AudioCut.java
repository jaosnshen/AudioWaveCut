package com.example.chenhangye.audiowavecut.Util.CutAudio;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.chenhangye.audiowavecut.Audio.AudioConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chenhangye on 2016/10/6.
 */

public class AudioCut implements IAudioCut {
    private cutAudioCallBack mCutAudioCallBack;


    public interface cutAudioCallBack{
        void cutErrer(String message);
        void cutFinish(String path);
    }


    private void  cutAudio(final String InputPath, final String OutPutPath, final int fromTime, final int duration) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess=true;
                File inputfile=new File(InputPath);
                File outPutfile=new File(OutPutPath);
                try {
                    FileInputStream fileInputS=new FileInputStream(inputfile);
                    FileOutputStream fileOutput=new FileOutputStream(outPutfile);
                    int n= AudioConfig.EACHSCENDSIZE;
                    byte[] bs=new byte[n];
                    int lengh=fileInputS.read(bs);
                    int scend=0;
                    while(lengh!=-1&&scend<=duration+fromTime){
                        if (fromTime<=scend) {
                            fileOutput.write(bs, 0, lengh);
                            lengh=fileInputS.read(bs);
                        }
                        scend++;
                    }
                } catch (FileNotFoundException e) {
                    isSuccess=false;
                    mCutAudioCallBack.cutErrer(e.toString());
                    // TODO Auto-generated catch block

                }catch (IOException e) {
                    isSuccess=false;
                    mCutAudioCallBack.cutErrer(e.toString());
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(isSuccess){
                    mCutAudioCallBack.cutFinish(OutPutPath);
                }
            }
        }).start();
    }

    /**
     * 监听回调
     * @param mCallback
     */
    @Override
    public void addCallBack(cutAudioCallBack mCallback) {
        this.mCutAudioCallBack=mCallback;
    }

    /**
     *
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     * @param fromTime 剪切开始时间
     * @param duration  剪切持续时间
     */

    @Override
    public void start(String inputPath, String outputPath, int fromTime, int duration) {
        cutAudio(inputPath,outputPath,fromTime,duration);
    }
}
