package com.example.chenhangye.audiowavecut.Audio.recorder;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceView;

import com.example.chenhangye.audiowavecut.Audio.AudioConfig;
import com.example.chenhangye.audiowavecut.Audio.AudioRecorder;
import com.example.chenhangye.audiowavecut.R;
import com.example.chenhangye.audiowavecut.Util.TimeUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by chenhangye on 2016/10/2.
 */

public class AudioPCMRecorder extends AbsRecorder {

    private static final String TAG = "AudioRecordRecorder";

    private Paint circlePaint;
    private int line_off=30 ;//上下边距的距离
    // audiorecord
    private AudioRecord audioRecord;
    private int inBufferSize;
    private String targetPath;
    private SurfaceView sfFaceView;
    private int readsize;
    public int rateX = 1;//控制多少帧取一帧
    private int draw_time = 80 / 1000;//两次绘图间隔的时间
    long c_time;
    private ArrayList<Short> inBuf = new ArrayList<Short>();//缓冲区数据
    private ArrayList<byte[]> write_data = new ArrayList<>();//写入文件数据
    private float divider =150/(1000f/80f);//为了节约绘画时间，每0.2个像素画一个数据
    private Paint center;
    private Paint paintLine;
    private int marginRight=30;//波形图绘制距离右边的距离
    public int rateY = 3; //  Y轴缩小的比例 默认为1
    private Paint mPaint;
    private boolean isWriting = false;// 录音线程写入文件控制标记

    private Paint sPaint;

    private int bigGap=150;
    private int smallGap=50;

    private int LenghDraw=0;

    private int pastLineX=0;

    private boolean isFromCancelCut=false;

    private LinkedList<Double> volumws=new LinkedList<>(); //记录分贝值


    private int LongLineLength=50;

    public AudioPCMRecorder(){
        init();
    }

    public AudioPCMRecorder(SurfaceView sfFaceView) {
        this.sfFaceView = sfFaceView;
        // line_off = ((WaveSurfaceView)sfFaceView).getLine_off();
        init();
    }

    private void init() {
        circlePaint = new Paint();//画圆
        circlePaint.setColor(Color.rgb(11,185,8));//设置上圆的颜色
        circlePaint.setStrokeWidth(6);


        paintLine = new Paint();
        paintLine.setColor(Color.rgb(90,90, 90));

        center=new Paint();
        center.setColor(Color.rgb(90,90, 90));// 画笔为color
        center.setStrokeWidth(1);// 设置画笔粗细
        center.setAntiAlias(true);
        center.setFilterBitmap(true);
        center.setStyle(Paint.Style.FILL);
        inBufferSize = AudioRecord.getMinBufferSize(
                AudioConfig.SAMPLE_RATE,
                AudioConfig.CHANNEL_IN,
                AudioConfig.AUDIO_ENCODING);

        audioRecord = new AudioRecord(
                AudioConfig.AUDIO_SOURCE,
                AudioConfig.SAMPLE_RATE,
                AudioConfig.CHANNEL_IN,
                AudioConfig.AUDIO_ENCODING,
                inBufferSize);

        mPaint = new Paint();
        mPaint.setColor(Color.rgb(90, 90, 90));// 画笔为color
        mPaint.setStrokeWidth(1);// 设置画笔粗细
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        isWriting=true;

        sPaint=new Paint();
        sPaint.setColor(R.color.default_txt_black);
        sPaint.setAntiAlias(true);
        sPaint.setStrokeWidth(2);
        sPaint.setTextSize(20);
    }




    @Override
    void onAudioRecord(String targetPath) {
        Log.e("路径",targetPath);
        this.targetPath = targetPath;
        isWriting=true;
        CongTimer.resetTimer();
        new Thread(new WriteRunable()).start();
        new RecordTask().execute(targetPath);
    }

    @Override
    void onAudioResume() {
        isWriting=true;
        new Thread(new WriteRunable()).start();
        new RecordTask().execute(targetPath);

    }

    @Override
    void onAudioPause() {
        super.onAudioPause();
        isWriting=false;
    }


    @Override
    void onAudioRelease() {
        if (audioRecord != null){
            if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED){
                audioRecord.stop();
            }
            audioRecord.release();
        }
    }

    @Override
    public int getDuration() {
        return CongTimer.workingTime;
    }

    @Override
    public void setDuration(int duration) {
        CongTimer.workingTime=duration;
    }

    @Override
    public void clearCanvas() {
        if (mState!=RecordState.Recording&&sfFaceView!=null){
            Canvas canvas = sfFaceView.getHolder().lockCanvas(
                    new Rect(0, 0, sfFaceView.getWidth(), sfFaceView.getHeight()));// 关键:获取画布
            Paint mPaint = new Paint();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            inBuf.clear();
            sfFaceView.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
        }
    }

    @Override
    public void reDrawCanvas() {
        isFromCancelCut=false;
        SimpleDraw(sfFaceView.getHeight()/2);
    }

    @Override
    public LinkedList<Double> getValumws() {
        return volumws;
    }

    @Override
    public void setValumws(LinkedList<Double> linkedList,SurfaceView sfFaceView) {
        this.volumws=linkedList;
        this.sfFaceView=sfFaceView;
        isFromCancelCut=false;
        SimpleDraw(sfFaceView.getHeight()/2);
    }

    @Override
    public void setFilePath(String pathFile) {
        this.targetPath=pathFile;
    }


    private class RecordTask extends AsyncTask<String, Integer, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallback != null){
                mCallback.onRecord();
            }
        }

        @Override
        protected Void doInBackground(String... params) {


            try {
                short[] buffer = new short[inBufferSize];
                audioRecord.startRecording();
                CongTimer.startTimer();

                while(true) {
                    if (mState == RecordState.Paused || mState == RecordState.Stoped){
                        CongTimer.stopTimer();
                        break;
                    }
                    readsize=audioRecord.read(buffer, 0, buffer.length);

                    //计算分贝
                    int v=0;
                    for(int i=0;i<buffer.length;++i){
                        v=v%Integer.MAX_VALUE+buffer[i]*buffer[i];
                    }
                    double mean=v/2.0d/readsize;
                    double volumw=10*Math.log10(mean)-10;
                    volumws.add(volumw);
                    synchronized (inBuf){
                        for(int i=0;i<readsize;i+=rateX) {
                            inBuf.add(buffer[i]);
                        }
                    }publishProgress(CongTimer.workingTime);

                    if (AudioRecord.ERROR_INVALID_OPERATION!=readsize) {
                        synchronized (write_data) {
                            byte bys[] = new byte[readsize * 2];
                            //因为arm字节序问题，所以需要高低位交换
                            for (int i = 0; i < readsize; i++) {
                                byte ss[] = getBytes(buffer[i]);
                                bys[i * 2] = ss[0];
                                bys[i * 2 + 1] = ss[1];
                            }
                            write_data.add(bys);
                        }
                    }

                }

                if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED){
                    audioRecord.stop();
                }
                isWriting = false;

            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mCallback != null){
                mCallback.onProgress(values[0]);
            }

            //不为空绘制图片
            if (sfFaceView!=null){
                long time=new Date().getTime();
                if (time-c_time>=draw_time){
                    SimpleDraw(sfFaceView.getHeight()/2);
                    c_time=new Date().getTime();
                }

            }
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mState == RecordState.Paused){
                if (mCallback != null){
                    mCallback.onPause();
                }
            } else {
                mCallback.onStop();
            }
        }
    }

    public byte[] getBytes(short s)
    {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++)
        {
            buf[i] = (byte) (s & 0x00ff);
            s >>= 8;
        }
        return buf;
    }

    /**2/25秒 刷新12个像素
     * 绘制指定区域
     * Y轴基线
     *
     */
    private void SimpleDraw(int baseLine) {
        bigGap=sfFaceView.getWidth()/5;
        smallGap=bigGap/3;
        if (RecordState.Recording!=mState&&isFromCancelCut) {
            isFromCancelCut=true;
            return;
        }
        int height;
        Canvas canvas = sfFaceView.getHolder().lockCanvas(
                new Rect(0, 0, sfFaceView.getWidth(), sfFaceView.getHeight()));// 关键:获取画布

        if(canvas==null)
            return;

        canvas.drawARGB(255, 239, 239, 239);
        if (volumws==null){
            return;
        }
        int start =(int) ((volumws.size())* divider);
        float y;

        if(sfFaceView.getWidth() - start <= marginRight){//如果超过预留的右边距距离
            start = sfFaceView.getWidth() -marginRight;//画的位置x坐标
            LenghDraw= (int) (LenghDraw+2f/25f*150f);
            pastLineX=start;
        }

        for(int i=0;i<=(sfFaceView.getWidth()+LenghDraw)/bigGap;++i){
            int startX=bigGap*i;
            int tempx=startX-LenghDraw;
            canvas.drawLine(tempx,0+line_off/2,tempx,LongLineLength+line_off/2,sPaint);
            for (int l = 0; l < 3; l++) {
                int temp = tempx + smallGap * (l + 1);
                canvas.drawLine(temp, line_off/2+0, temp, LongLineLength/2+line_off/2, sPaint);
            }
            canvas.drawText(TimeUtils.convertSecondToMinute(i),tempx+10,LongLineLength+line_off/2,sPaint);
        }

        canvas.drawCircle(start, line_off, line_off/2, circlePaint);// 上圆
        canvas.drawCircle(start, sfFaceView.getHeight()-line_off, line_off/2, circlePaint);// 下圆
        canvas.drawLine(start, 6, start, sfFaceView.getHeight()-6, circlePaint);//垂直的线
        height = sfFaceView.getHeight()-line_off;

        canvas.drawLine(0, line_off/2, sfFaceView.getWidth(), line_off/2, paintLine);//最上面的那根线
        canvas.drawLine(0, height*0.5f+line_off/2, sfFaceView.getWidth() ,height*0.5f+line_off/2, center);//中心线
        canvas.drawLine(0, sfFaceView.getHeight()-line_off/2-1, sfFaceView.getWidth(), sfFaceView.getHeight()-line_off/2-1, paintLine);//最下面的那根线

        float temp=0;
        if (sfFaceView.getWidth() - (volumws.size()-1) * divider <=marginRight){
            temp=((volumws.size()-1)*divider)-sfFaceView.getWidth();
        }
        for(int i=0;i<volumws.size();i++){
            //防止出现意外报空 暂时复制30
            if(volumws.get(i)==null){
                volumws.set(i, Double.valueOf(30));
            }
            y= (int)((volumws.get(i)/rateX+baseLine));
            float x=i*divider;
            if (y < sfFaceView.getHeight() / 4||y>sfFaceView.getHeight()/4*3) {
                canvas.drawLine(x-temp, sfFaceView.getHeight() / 4, x-temp, sfFaceView.getHeight() - sfFaceView.getHeight() / 4, mPaint);//中间出波形
            } else {
                canvas.drawLine(x-temp, y, x-temp, sfFaceView.getHeight() - y, mPaint);//中间出波形
            }
            x+=6;
            if (y < sfFaceView.getHeight() / 4||y>sfFaceView.getHeight()/4*3) {
                canvas.drawLine(x-temp, sfFaceView.getHeight() / 4, x-temp, sfFaceView.getHeight() - sfFaceView.getHeight() / 4, mPaint);//中间出波形
            } else {
                canvas.drawLine(x-temp, y, x-temp, sfFaceView.getHeight() - y, mPaint);//中间出波形
            }

        }
        sfFaceView.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    // Timer
    private static class CongTimer{
        private static Handler mHandler = new Handler();
        private static int workingTime;
        private static boolean isTimerStoped;

        private static void startTimer(){
            isTimerStoped = false;
            mHandler.postDelayed(recordTime, 0);
        }

        private static void resetTimer(){
            isTimerStoped = true;
            workingTime = 0;
        }

        private static void stopTimer(){
            isTimerStoped = true;
            mHandler.removeCallbacks(recordTime);
        }

        private static Runnable recordTime = new Runnable() {
            @Override
            public void run() {
                if (!isTimerStoped){
                    workingTime += 1;
                    mHandler.postDelayed(this, 1000);
                }
            }
        };
    }
    //write file thread
    class WriteRunable implements Runnable{

        @Override
        public void run() {
            RandomAccessFile randomAccessFile = null;
            try {
                try {
                    Log.e("创建文件","真的"+targetPath);
                    randomAccessFile = new RandomAccessFile(targetPath, "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("创建文件",e.toString());
                }
                if (randomAccessFile == null){
                    Log.e("结果","文件空了");
                    return;
                }
                while(isWriting||write_data.size()>0){
                    byte[] buffer=null;
                    synchronized (write_data){
                        //按照 一位一位写入
                        if (write_data.size()>0){
                            buffer=write_data.get(0);
                            write_data.remove(0);
                        }
                    }
                    try {
                        //拼接
                        if (buffer!=null) {
                            randomAccessFile.seek(randomAccessFile.length());
                            randomAccessFile.write(buffer, 0, buffer.length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //读写结束 关闭文件流
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
