package com.example.chenhangye.audiowavecut.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.chenhangye.audiowavecut.View.Enum.ProgressEnum;
import com.example.chenhangye.audiowavecut.View.Interface.setIPosition;

/**
 * Created by chenhangye on 2016/10/5.
 */

public class AudioCutLineView extends View {
    //  private Scroller scroller;
    private setIPosition mIpostion;
    private int top;
    private int bottom;
    private int downX;
    private int downY;
    private ProgressEnum.Type type;
    Paint paint;
    private int line_off=30 ;//上下边距的距离


    public void setmIpostion(setIPosition mIpostion, ProgressEnum.Type type) {
        this.mIpostion = mIpostion;
        this.type=type;

    }

    public AudioCutLineView(Context context) {
        super(context);
        initView(context);
    }

    public AudioCutLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioCutLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        //  scroller=new Scroller(context);
        paint=new Paint();
        paint.setColor(Color.rgb(11,185,8));
        paint.setStrokeWidth(6);
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
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.top=top;
        this.bottom=bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x= (int) event.getX();
        int y= (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX= x-downX;
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (getLeft()+offsetX<((RelativeLayout)getParent()).getWidth()-10&&getLeft()+offsetX>0) {
                    layoutParams.leftMargin = getLeft() + offsetX;
                }
                //监听不为空
                if (mIpostion != null) {
                    mIpostion.setRightPosition(getLeft() + offsetX,type);
                }
                setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height=getHeight();
        int width=getWidth();

        canvas.drawCircle(width/2, line_off, line_off/2, paint);// 上圆
        canvas.drawCircle(width/2, height-line_off, line_off/2, paint);// 下圆
        canvas.drawLine(width/2, 6, width/2, height-6, paint);//垂直的线
        //   canvas.drawLine(width/2,0,width/2,height,paint);

        super.onDraw(canvas);
    }
}
