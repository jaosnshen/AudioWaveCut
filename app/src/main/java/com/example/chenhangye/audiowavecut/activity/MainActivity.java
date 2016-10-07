package com.example.chenhangye.audiowavecut.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chenhangye.audiowavecut.Audio.AudioConfig;
import com.example.chenhangye.audiowavecut.Audio.AudioPlayer;
import com.example.chenhangye.audiowavecut.Audio.AudioRecorder;
import com.example.chenhangye.audiowavecut.Audio.player.AudioPCMPlayer;
import com.example.chenhangye.audiowavecut.Audio.player.PlayerCallback;
import com.example.chenhangye.audiowavecut.Audio.player.PlayerState;
import com.example.chenhangye.audiowavecut.Audio.recorder.AudioPCMRecorder;
import com.example.chenhangye.audiowavecut.Audio.recorder.RecordState;
import com.example.chenhangye.audiowavecut.Audio.recorder.RecorderCallback;
import com.example.chenhangye.audiowavecut.R;
import com.example.chenhangye.audiowavecut.Util.FileOperation;
import com.example.chenhangye.audiowavecut.Util.TimeUtils;
import com.example.chenhangye.audiowavecut.View.WaveSurfaceView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private WaveSurfaceView surfaceView;
    private ImageView  imgPlaystate;
    private AudioRecorder mRecorder;
    private File currentAudioFile;
    private String currentAudioFilePath;
    private MediaPlayer bgmPlayer;
    private ImageView imgBgmPlayIconCut;
    private ImageView imgCutPlay;
    private TextView txtREcoderTime;
    private int time=-1;
    private int REQUSET=1;
    private LinkedList<Double> mValumws;

    private boolean isPlaying=true;//播放停止是因为录音停止还是点击停止


    private TextView txtBackgroundmusicDelete;


    private AudioPlayer mPlayer;

    @Override
    protected void initDate() {
        if(surfaceView != null) {
            //解决surfaceView黑色闪动效果
            surfaceView.setZOrderOnTop(true);
            surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        }
        mRecorder=new AudioPCMRecorder(surfaceView);
    }

    @Override
    protected void initVariable() {
        mPlayer=new AudioPCMPlayer();

    }

    @Override
    protected void initView() {
        surfaceView= (WaveSurfaceView) findViewById(R.id.act_animalePlayWave_sf);
        imgPlaystate= (ImageView) findViewById(R.id.act_playstate_img);
        imgBgmPlayIconCut = (ImageView) findViewById(R.id.act_animaleRecorde_icon_cut);
        imgCutPlay= (ImageView) findViewById(R.id.act_animaleRecorde_icon_play);
        txtBackgroundmusicDelete= (TextView) findViewById(R.id.act_backgroundmusic_delete);
        txtREcoderTime= (TextView) findViewById(R.id.act_animateRecorderTime_txt);
        imgCutPlay= (ImageView) findViewById(R.id.act_animaleRecorde_icon_play);
        imgCutPlay.setOnClickListener(this);
        imgPlaystate.setOnClickListener(this);
        imgBgmPlayIconCut.setOnClickListener(this);
        txtBackgroundmusicDelete.setOnClickListener(this);
        imgCutPlay.setOnClickListener(this);
    }

    @Override
    protected int LayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.act_playstate_img:
                handleRecord();
                break;
            case R.id.act_animaleRecorde_icon_play:
                mPlayer.addCallback(new PlayerCallback() {
                    @Override
                    public void onPlay() {
                        setState(imgCutPlay,true,R.drawable.audio_animalerecorde_icon_playing);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onPause() {
                        setState(imgCutPlay,true,R.drawable.audio_animalerecorde_icon_play_green);
                    }

                    @Override
                    public void onStop() {
                        if (isPlaying) {
                            setState(imgCutPlay, true, R.drawable.audio_animalerecorde_icon_play_green);
                        }else{
                            setState(imgCutPlay, true, R.drawable.audio_animalerecorde_icon_play_grey);
                        }
                    }
                });

                if (!TextUtils.isEmpty(currentAudioFilePath)) {
                    if (mPlayer.getState() == PlayerState.Paused) { //暂停
                        mPlayer.play(currentAudioFilePath);
                    } else if (mPlayer.getState() == PlayerState.Playing) {//播放
                        mPlayer.pause();
                        //   mPlayer.release();
                    } else if (mPlayer.getState() == PlayerState.Stoped) {//停止
                        mPlayer.play(currentAudioFilePath);
                    }
                }else{
                   Log.e("播放","播放错误");
                }
                break;
            case R.id.act_animaleRecorde_icon_cut:
                Jump2Cut();
                break;
            case R.id.act_backgroundmusic_delete:
                handleDelete();
                break;
            default:
                break;


        }
    }

    private void Jump2Cut(){

        Intent intent=new Intent(MainActivity.this,AnimalCutAudioActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(AnimalCutAudioActivity.VOICEVALUMS,mRecorder.getValumws());
        bundle.putString(AnimalCutAudioActivity.FILEPATH,currentAudioFilePath);
        bundle.putDouble(AnimalCutAudioActivity.CURRENTTIME,currentAudioFile.length()*1.0/AudioConfig.EACHSCENDSIZE*1.0);
        intent.putExtra(AnimalCutAudioActivity.AUDIOBUNDLE,bundle);
        startActivityForResult(intent,REQUSET);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==REQUSET){
               Bundle bundle=data.getBundleExtra(AnimalCutAudioActivity.AUDIOBUNDLE);
                currentAudioFilePath=bundle.getString(AnimalCutAudioActivity.FILEPATH);
                mValumws=new LinkedList((ArrayList<Double>) bundle.getSerializable(AnimalCutAudioActivity.VOICEVALUMS));
                boolean isCut=bundle.getBoolean(AnimalCutAudioActivity.ISCUT);
                File file=new File(currentAudioFilePath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (isCut)
                    currentAudioFile = file;
                }
                if (isCut){
                    currentAudioFile = file;
                }
                surfaceView.postInvalidate();
                mRecorder.setValumws(mValumws,surfaceView);
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mValumws!=null&&surfaceView!=null) {
            mRecorder.setValumws(mValumws, surfaceView);
        }
    }

    private void handleDelete() {
        if (currentAudioFile != null && currentAudioFile.exists()) {
            new AlertDialog.Builder(this)
                    .setCustomTitle(null)
                    .setMessage(R.string.launch_audio_live_delete_dialog_content)
                    .setPositiveButton(R.string.launch_audio_live_delete_dialog_pos, new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    handleRecorderStateToReset();
                                    if (mRecorder.getState() != RecordState.Stoped) {
                                        mRecorder.stop();
                                    }
                                    if (mPlayer.getState() != PlayerState.Stoped) {
                                        mPlayer.stop();
                                    }
                                    mRecorder.reDrawCanvas();
                                }
                            })
                    .setNegativeButton(R.string.launch_audio_live_delete_dialog_neg, new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                    .show();
        }
    }


    private void handleRecorderStateToReset(){
        imgBgmPlayIconCut.setVisibility(View.GONE);
        imgCutPlay.setVisibility(View.INVISIBLE);
        txtBackgroundmusicDelete.setVisibility(View.INVISIBLE);
        imgPlaystate.setImageResource(R.drawable.audio_animalerecorde_init);
        txtREcoderTime.setText("00:00.00");

        mRecorder.getValumws().clear();
        if (surfaceView.isLayoutRequested()){
            surfaceView.requestLayout();
        }
        surfaceView.setVisibility(View.INVISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        txtREcoderTime.setVisibility(View.GONE);
        txtREcoderTime.setText(currentAudioFile.length()*1.0/ AudioConfig.EACHSCENDSIZE*1.0+" ");

    }

    private void handleRecord() {
        switch (mRecorder.getState()){
            case Stoped:
                checkRecorderStateToPlay();
                if (currentAudioFilePath==null){
                    currentAudioFilePath = FileOperation.getPath();
                }
                currentAudioFile = new File(currentAudioFilePath);
                //创建录音文件
                if (!currentAudioFile.exists()) {
                    try {
                        currentAudioFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mRecorder.addCallback(new RecorderCallback() {
                    @Override
                    public void onRecord() {
                        setState(imgCutPlay,false,R.drawable.audio_animalerecorde_icon_play_grey);
                        setState(imgBgmPlayIconCut,false);
                    }

                    @Override
                    public void onProgress(int sec) {
                        time=sec;
                        Log.e("时间长度",currentAudioFile.length()+"  ");
                        txtREcoderTime.setText(TimeUtils.convertSecondToMinute((int) (currentAudioFile.length()/AudioConfig.EACHSCENDSIZE)));
                    }

                    @Override
                    public void onPause() {

                        setState(imgCutPlay,true,R.drawable.audio_animalerecorde_icon_play_green);
                        setState(imgBgmPlayIconCut,true);
                    }

                    @Override
                    public void onStop() {
                        setState(imgCutPlay,false,R.drawable.audio_animalerecorde_icon_play_grey);
                        setState(imgBgmPlayIconCut,false);
                    }
                });
                mRecorder.record(currentAudioFilePath);
                break;
            case Recording:
                checkRecorderStateToPause();
                mRecorder.pause();
                break;
            case Paused:
                if (mPlayer.getState()==PlayerState.Playing||mPlayer.getState()== PlayerState.Paused){
                    isPlaying=false;
                    mPlayer.stop();
                    setState(imgCutPlay,false,R.drawable.audio_animalerecorde_icon_play_grey);
                    setState(imgBgmPlayIconCut,false);
                }
                Log.e("暂停","暂停");
                mRecorder.setFilePath(currentAudioFilePath);
                mRecorder.resume();
                checkRecorderStateToPlay();
                break;
            default:
                break;
        }
    }

    private void checkRecorderStateToPause(){
        txtREcoderTime.setVisibility(View.VISIBLE);
        imgPlaystate.setImageResource(R.drawable.record_icon_playing);
    }

    private void setState(View view,boolean isSelect){
        view.setSelected(isSelect);
        view.setEnabled(isSelect);
    }
    private void checkRecorderStateToPlay(){
        imgBgmPlayIconCut.setVisibility(View.VISIBLE);
        imgCutPlay.setVisibility(View.VISIBLE);
        txtBackgroundmusicDelete.setVisibility(View.VISIBLE);
        imgPlaystate.setImageResource(R.drawable.audio_animalerecorde_recording);
        txtREcoderTime.setVisibility(View.VISIBLE);
    }

    private void setState(ImageView img,boolean isSelect,int id){
        img.setImageResource(id);
        img.setEnabled(isSelect);
    }
}
