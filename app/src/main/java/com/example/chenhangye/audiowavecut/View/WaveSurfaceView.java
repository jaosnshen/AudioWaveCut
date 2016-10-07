package com.example.chenhangye.audiowavecut.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.chenhangye.audiowavecut.R;

/**
 * Created by chenhangye on 2016/10/1.
 */

public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private int line_off=20;//上下边距距离
    private Paint secendPaint;
    private Paint LongLinePaint;

    private Paint paintLine;
    private Paint centerLine;
    private Paint circlePaint;
    private int paddingLeft=8;

    private int LongLineLengh=50;



    public WaveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.holder = getHolder();
        holder.addCallback(this);
        secendPaint=new Paint();
        secendPaint.setColor(getResources().getColor(R.color.default_txt_black));
        secendPaint.setAntiAlias(true);
        secendPaint.setStrokeWidth(1);
        secendPaint.setTextSize(30);


        LongLinePaint=new Paint();
        LongLinePaint.setColor(getResources().getColor(R.color.default_txt_black));
        secendPaint.setAntiAlias(true);
        secendPaint.setStrokeWidth(2);
        secendPaint.setTextSize(20);

        paintLine =new Paint();
        centerLine =new Paint();

        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.default_green_color));
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(4);
        circlePaint.setStyle(Paint.Style.FILL);

    }



    public void initSurfaceView(final SurfaceView sfv){
        final int width=getWidth()/5;
        new Thread(){
            public void run() {
                Canvas canvas = sfv.getHolder().lockCanvas(
                        new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));// 关键:获取画布
                if(canvas==null){
                    return;
                }
                canvas.drawARGB(255, 239, 239, 239);
                int height = sfv.getHeight()-line_off;
                canvas.drawCircle(paddingLeft, line_off, line_off/2, circlePaint);// 上面小圆
                canvas.drawCircle(paddingLeft, sfv.getHeight()-line_off, line_off/2, circlePaint);// 下面小圆
                canvas.drawLine(paddingLeft, line_off, paddingLeft, sfv.getHeight()-line_off, circlePaint);//垂直的线
                paintLine.setColor(Color.rgb(90, 90, 90));
                centerLine.setColor(Color.rgb(90, 90, 90));
                canvas.drawLine(0, line_off/2, sfv.getWidth(), line_off/2, paintLine);//最上面的那根线
                canvas.drawLine(0, sfv.getHeight()-line_off/2-1, sfv.getWidth(), sfv.getHeight()-line_off/2-1, paintLine);//最下面的那根线

                canvas.drawLine(0, height*0.5f+line_off/2, sfv.getWidth() ,height*0.5f+line_off/2, centerLine);//中心线
                int startX=0;
                for (int i=0;i<=sfv.getWidth()/width;++i){
                    int tempx=startX+width*i;
                    canvas.drawLine(tempx,0+line_off/2,tempx,LongLineLengh+line_off/2,LongLinePaint);
                    for (int l = 0; l < 2; l++) {
                        int temp = tempx + width/3 * (l + 1);
                        canvas.drawLine(temp, line_off/2+0, temp, LongLineLengh/2+line_off/2, secendPaint);
                    }
                    canvas.drawText(String.format("%02d:%02d",i/60,i%60), (float) (tempx+width/15/4.0*5.0),LongLineLengh+line_off/2,secendPaint);
                }
                sfv.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
            }
        }.start();



    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initSurfaceView(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
