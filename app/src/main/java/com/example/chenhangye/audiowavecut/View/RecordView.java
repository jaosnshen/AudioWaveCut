package com.example.chenhangye.audiowavecut.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.chenhangye.audiowavecut.R;
import com.example.chenhangye.audiowavecut.Util.ScreenUtil;
import com.example.chenhangye.audiowavecut.View.Enum.ProgressEnum;
import com.example.chenhangye.audiowavecut.View.Interface.getIPosition;
import com.example.chenhangye.audiowavecut.View.Interface.setIPosition;

import java.util.LinkedList;

/**
 * Created by chenhangye on 2016/10/5.
 */

public class RecordView extends View implements setIPosition {
    private int screenWidth;
    private Paint secendPaint;

    private Context mContext;
    private int left,right;
    private int cutRight=0,cutLeft=0;

    private Paint mDrawLinePaint;
    private int height;
    private int width;


    private int LongLineLength=50;
    private getIPosition mGetIPosition;

    public void setGetIPosition(getIPosition getIPosition) {
        mGetIPosition = getIPosition;
    }

    private LinkedList<Double> valumws=null;

    private Paint mCoverPaint;

    // 距离右边的百分比
    public float getWaveRight() {
        return cutRight*1.0f/screenWidth*1.0f;
    }

    //距离左边的百分比
    public float getMaveLeft() {
        return cutLeft*1.0f/screenWidth*1.0f;
    }

    public void setValumws(LinkedList<Double> valumws) {
        this.valumws = valumws;
        postInvalidate();
    }

    public RecordView(Context context) {
        super(context);
        init(context);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec),getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec));
    }
    public static int getDefaultSize(int size,int measureSpec){
        int result=size;
        int specMode=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        switch (specMode){
            case MeasureSpec.UNSPECIFIED:
                result=size;
                break;
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                result=specSize;
                break;
        }
        return result;
    }

    private void init(Context context) {
        this.mContext=context;
        cutRight=ScreenUtil.getScreenWidthPix(context);
        initPaint();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        left=0;
        right=getWidth();
    }

    private void initPaint() {


        secendPaint=new Paint();
        secendPaint.setColor(getResources().getColor(R.color.default_txt_black));
        secendPaint.setAntiAlias(true);
        secendPaint.setStrokeWidth(1);
        secendPaint.setTextSize(20);


        mDrawLinePaint=new Paint();
        mDrawLinePaint.setColor(getResources().getColor(R.color.default_txt_black));
        mDrawLinePaint.setAntiAlias(true);
        mDrawLinePaint.setStrokeWidth(1);
        mDrawLinePaint.setTextSize(20);


        mCoverPaint=new Paint();
        mCoverPaint.setColor(mContext.getResources().getColor(R.color.cover_color_grenn_aph));
        mDrawLinePaint.setAntiAlias(true);
        mDrawLinePaint.setStrokeWidth(1);


    }


    @Override
    protected void onDraw(Canvas canvas) {

        height = getHeight();
        width = getWidth();
        screenWidth=width;
        canvas.drawRect(cutLeft,0,cutRight,height,mCoverPaint);
        float time= (float) (valumws.size()*1.0*2.0/25.0);

        canvas.drawLine(0,0,width,0,mDrawLinePaint);

        float eachTime=time/5;
        float bigGap= (float) (width*1.0/5.0);
        float smallGap= (float) (bigGap*1.0/3.0);
        for(int i=0;i<=5;++i){
            float startX=bigGap*i;
            canvas.drawLine(startX,0,startX,LongLineLength,mDrawLinePaint);
            for (int l = 0; l < 3; l++) {
                float temp = startX + smallGap * (l + 1);
                canvas.drawLine(temp, 0, temp, LongLineLength/2, mDrawLinePaint);
            }
            canvas.drawText(String.format("%02d:%02d",(int)((i+1)*eachTime/60+0.5),(int)(((i+1)*eachTime)%60+0.5)),startX+10,LongLineLength-10,mDrawLinePaint);
        }



        canvas.drawLine(0, height / 2, left, height / 2, secendPaint);
        secendPaint.setColor(getResources().getColor(R.color.default_txt_black));
        canvas.drawLine(left, height / 2, right, height / 2, secendPaint);
        secendPaint.setColor(getResources().getColor(R.color.default_txt_black));
        canvas.drawLine(right, height / 2, width, height / 2, secendPaint);

        secendPaint.setColor(mContext.getResources().getColor(R.color.crowdfund_info_grey_color));

        if (valumws!=null){
            float gap=(width*1.0f/valumws.size()*1.0f);
            float x=0.0f;
            //如果间距小于1
            int exchangGap=1;
            if (gap<3){
                exchangGap=3;
            }
            for (int i = 0; i <valumws.size() ; i=i+exchangGap) {
                int y= (int) (valumws.get(i)+height/2);
                x+= gap*exchangGap;
                if (y <height / 4||y>height/4*3) {
                    canvas.drawLine(x, height / 4, x, height - height / 4, secendPaint);//中间出波形
                } else {
                    canvas.drawLine(x, y, x, height - y, secendPaint);//中间出波形
                }
            }
        }
        super.onDraw(canvas);
    }
    @Override
    public void setRightPosition(int postion, ProgressEnum.Type type) {
        if (type.equals(ProgressEnum.Type.Left)){
            mGetIPosition.getLeft(postion*1.0/width*1.0);
            cutLeft=postion;
        }else if (type.equals(ProgressEnum.Type.Right)){
            double result=postion*1.0/width/1.0;
            Log.e("结果",result+" ");
            mGetIPosition.getRight(result);
            cutRight=postion;
        }
        postInvalidate();
    }
}
