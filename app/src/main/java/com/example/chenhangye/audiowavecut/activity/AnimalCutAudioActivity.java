package com.example.chenhangye.audiowavecut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chenhangye.audiowavecut.Audio.AudioPlayer;
import com.example.chenhangye.audiowavecut.Audio.player.AudioPCMPlayer;
import com.example.chenhangye.audiowavecut.Audio.player.PlayerCallback;
import com.example.chenhangye.audiowavecut.Audio.player.PlayerState;
import com.example.chenhangye.audiowavecut.R;
import com.example.chenhangye.audiowavecut.Util.CutAudio.AudioCut;
import com.example.chenhangye.audiowavecut.Util.CutAudio.IAudioCut;
import com.example.chenhangye.audiowavecut.Util.FileOperation;
import com.example.chenhangye.audiowavecut.View.AudioViewCombination;
import com.example.chenhangye.audiowavecut.View.Interface.getIPosition;

import java.util.ArrayList;
import java.util.LinkedList;

public class AnimalCutAudioActivity extends BaseActivity implements getIPosition, View.OnClickListener {


    private AudioViewCombination mViewCombination;
    private LinkedList<Double> mValumws;
    public final static String VOICEVALUMS = "VoiceValums";
    public final static String CURRENTTIME="CurrentTime";
    public final static String FILEPATH="FilePath";
    public final static String AUDIOBUNDLE ="AudioBundle";
    public final static String ISCUT="IsCut";
    private double time;
    private TextView txtTime;
    private double mTimeRight;
    private double mTimeLeft;
    private IAudioCut mIAudioCut;
    private TextView txtFinish;
    private TextView txtBack;
    private String mFilePath;
    private AudioPlayer mPlayer;

    @Override
    protected void initDate() {
        Bundle bundle = getIntent().getBundleExtra(AUDIOBUNDLE);
        mValumws = new LinkedList((ArrayList<Double>) bundle.getSerializable(VOICEVALUMS));
        time=bundle.getDouble(CURRENTTIME);
        mFilePath=bundle.getString(FILEPATH);
        mViewCombination.setValumws(mValumws);
        mViewCombination.setGetIPosition(this);
        mTimeLeft=time;
        mTimeRight=0;
        txtTime.setText(String.format("%02d:%02d-%02d:%02d",(int)mTimeRight/60,(int)mTimeRight%60,
                (int)mTimeLeft/60,((int)mTimeLeft)%60));
        mTimeLeft=0;
    }

    @Override
    protected void initVariable() {
        mIAudioCut=new AudioCut();
        mPlayer= new AudioPCMPlayer();
        mPlayer.addCallback(new PlayerCallback() {
            @Override
            public void onPlay() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onStop() {

            }
        });
    }

    @Override
    protected void initView() {
        mViewCombination = (AudioViewCombination) findViewById(R.id.act_animalecut_Combination);
        txtTime= (TextView) findViewById(R.id.act_animal_cut_time);
        txtFinish= (TextView) findViewById(R.id.title_bar_finish);
        txtBack= (TextView) findViewById(R.id.title_bar_back);
        txtFinish.setOnClickListener(this);
        txtBack.setOnClickListener(this);
    }

    @Override
    protected int LayoutId() {
        return R.layout.activity_animal_cut_audio;
    }


    @Override
    public void getRight(double right) {
        mTimeRight=right*time;
        Log.e("右边的距离",right*1.0*mViewCombination.getWidth()*1.0+" ");
        mViewCombination.getLeftView().setPaddingLeft(right*1.0*mViewCombination.getWidth()*1.0);
        txtTime.setText(String.format("%02d:%02d-%02d:%02d",(int)mTimeLeft/60,(int)mTimeLeft%60,
                (int)mTimeRight/60,(int)mTimeRight%60));
    }

    @Override
    public void getLeft(double left) {
        mTimeLeft=left*time;
        Log.e("左边边的距离",left*1.0*mViewCombination.getWidth()*1.0+" ");
        mViewCombination.getRightView().setPaddingLeft(left*1.0*mViewCombination.getWidth()*1.0);
        playResume();
        txtTime.setText(String.format("%02d:%02d-%02d:%02d",(int)mTimeLeft/60,(int)mTimeLeft%60,
                (int)mTimeRight/60,(int)mTimeRight%60));
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.title_bar_finish:
                mIAudioCut.addCallBack(new AudioCut.cutAudioCallBack() {
                    @Override
                    public void cutErrer(String message) {
                        Log.e("message",message);
                    }

                    @Override
                    public void cutFinish(String path) {
                        getCutArray(mViewCombination.getLeftTime(), mViewCombination.getRightTime());
                        playStop();
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putString(FILEPATH,path);
                        bundle.putBoolean(ISCUT,true);
                        bundle.putSerializable(VOICEVALUMS,mValumws);
                        intent.putExtra(AUDIOBUNDLE,bundle);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
                mIAudioCut.start(mFilePath,FileOperation.getPath(),(int)mTimeLeft, (int)(mTimeLeft-mTimeLeft));
                break;
            case R.id.title_bar_back:
                myBackPress();
                break;
            default:
                break;
        }

    }


    private void playResume(){
        if (mPlayer.getState()== PlayerState.Playing){
            mPlayer.stop();
        }
        mPlayer.setOffSet((int) mTimeLeft);
        mPlayer.play(mFilePath);
    }
    private void playStop(){
        if (mPlayer.getState()==PlayerState.Playing){
            mPlayer.stop();
            mPlayer.release();
        }
    }
    private void getCutArray(float left,float right){
        LinkedList<Double> list=mValumws;
        int lenght=list.size();
        int leftSize= (int) (left*lenght);
        int rightSize=lenght-(int) (right*lenght);
        for(int i=0;i<leftSize;++i){
            list.remove(0);
        }
        for (int i=0;i<rightSize;++i){
            list.remove(list.size()-1);
        }
        mValumws=list;
    }

    @Override
    public void onBackPressed() {
        myBackPress();
    }

    private void myBackPress() {
        playStop();
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString(FILEPATH,mFilePath);
        bundle.putBoolean(ISCUT,false);
        bundle.putSerializable(VOICEVALUMS,mValumws);
        intent.putExtra(AUDIOBUNDLE,bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
