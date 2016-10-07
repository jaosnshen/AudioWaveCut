package com.example.chenhangye.audiowavecut.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.chenhangye.audiowavecut.R;
import com.example.chenhangye.audiowavecut.View.Enum.ProgressEnum;
import com.example.chenhangye.audiowavecut.View.Interface.getIPosition;

import java.util.LinkedList;

/**
 * Created by chenhangye on 2016/10/5.
 */

public class AudioViewCombination extends RelativeLayout {
    private AudioCutLineView leftView;
    private AudioCutLineView rightView;
    private RecordView recordView;
    private View mRoot;



    public void setGetIPosition(getIPosition getIPosition) {
       recordView.setGetIPosition(getIPosition);
    }

    public AudioViewCombination(Context context) {
        super(context);
        initView(context);
    }

    public float getLeftTime(){
        return recordView.getMaveLeft();
    }

    public float getRightTime(){
        return recordView.getWaveRight();
    }

    public void setValumws(LinkedList<Double> info){
        recordView.setValumws(info);
    }
    public AudioViewCombination(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioViewCombination(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        mRoot= LayoutInflater.from(context).inflate(R.layout.widget_audio_cut_view,this,true);
        leftView= (AudioCutLineView) mRoot.findViewById(R.id.widget_audio_recordView_left);
        rightView= (AudioCutLineView) mRoot.findViewById(R.id.widget_audio_recordView_right);
        recordView= (RecordView) mRoot.findViewById(R.id.widget_audio_recordView);
        leftView.setmIpostion(recordView, ProgressEnum.Type.Left);
        rightView.setmIpostion(recordView,ProgressEnum.Type.Right);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
       RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(rightView.getLayoutParams());
        lp.setMargins(getWidth()-20,0,0,0);
        Log.e("宽度",getWidth()+" ");
        rightView.setLayoutParams(lp);
    }
}
