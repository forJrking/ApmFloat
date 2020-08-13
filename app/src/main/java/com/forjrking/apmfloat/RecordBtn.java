package com.forjrking.apmfloat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


public class RecordBtn extends View implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private static final int ANIMATION_TIME = 200;
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    private RectF mOvalRectF;

    /**
     * btn 需要绘制的宽度
     */
    private int drawWidth;

    /**
     * 圆环的颜色
     */
    private int ringColor = Color.parseColor("#E4E6E7");

    private int innerColor = Color.parseColor("#FD0406");

    /**
     * 圆环进度的颜色
     */
    private int ringSecondColor = Color.YELLOW;

    /**
     * 圆环的宽度
     */
    private float ringWidth, gapWidth;

    /**
     * 最大进度
     */
    private long max = 30 * 1000;

    /**
     * 是否正在录制
     */
    private volatile boolean mIsRecording = false;

    private volatile long mStartTime = 0;

    // 0 为视频 1 为拍照
    private int mCurrentMode = 0;

    /**
     * 当前进度
     */
    private long mSecond = 0;

    private OnRecordListener mOnRecordListener;

    private Context mContext;

    public RecordBtn(Context context) {
        this(context, null);
        mContext = context;
    }

    public RecordBtn(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public RecordBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mContext = context;
        ringSecondColor = Color.parseColor("#2998FE");

        setOnClickListener(this);
        setOnLongClickListener(this);
        setOnTouchListener(this);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setDither(true);
        paint.setAntiAlias(true);

        mOvalRectF = new RectF();
        ringWidth = 15;
        gapWidth = 8;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawWidth <= 0) {
            drawWidth = getWidth();
        }
        int centreX = getWidth() / 2; //获取圆心的x坐标
        int centreY = getHeight() - drawWidth / 2;
        int radius = (int) (drawWidth / 2 - ringWidth / 2 + .5f); //圆环的半径

        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        paint.setColor(ringColor); //设置圆环的颜色
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        canvas.drawCircle(centreX, centreY, radius, paint); //画出圆环

        paint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        paint.setColor(innerColor);
        //画内部红色圆圈
        int gap = (int) (ringWidth + gapWidth + .5f);
        canvas.drawCircle(centreX, centreY, radius - gap, paint);
        /*** 画圆弧 ，画圆环的进度*/
        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        int i = (int) (gap / 2 + .5f);
        paint.setStrokeWidth(gap); //设置圆环的宽度
        paint.setColor(ringSecondColor);  //设置进度的颜色
        mOvalRectF.set(centreX - radius + i, centreY - radius + i, centreX + radius - i, centreY + radius - i);  //用于定义的圆弧的形状和大小的界限
        canvas.drawArc(mOvalRectF, 270, 360 * mSecond / max, false, paint);  //根据进度画圆弧
        if (mSecond >= max && mOnRecordListener != null) {
            if (mIsRecording) {
                mIsRecording = false;
                mSecond = 0;
                mOnRecordListener.stopRecord();
            }
        }
    }

    public synchronized long getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            return;
        }
        this.max = max;
    }

    /**
     * 设置当前模式
     *
     * @param mode
     */
    public void setCurrentMode(int mode) {
        this.mCurrentMode = mode;
    }


    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized long getSecond() {
        return mSecond;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param second
     */
    public synchronized void setSecond(long second) {
        if (second < 0) {
            return;
        }
        if (second >= max) {
            mSecond = max;
        }
        if (second < max) {
            mSecond = second;
        }
        postInvalidate();
    }


    public int getCricleColor() {
        return ringColor;
    }

    public void setCricleColor(int cricleColor) {
        this.ringColor = cricleColor;
        invalidate();
    }

    public int getCriclesecondColor() {
        return ringSecondColor;
    }

    public void setCriclesecondColor(int criclesecondColor) {
        this.ringSecondColor = criclesecondColor;
    }

    public float getringWidth() {
        return ringWidth;
    }

    public void setringWidth(float ringWidth) {
        this.ringWidth = ringWidth;
        invalidate();
    }

    public int getDrawWidth() {
        return drawWidth;
    }

    public void setDrawWidth(int drawWidth) {
        this.drawWidth = drawWidth;
        invalidate();
    }

    @Override
    public void onClick(View v) {
        if (mOnRecordListener != null) {
            if (mIsRecording) {
                // 如果正在录制，则暂停录制，并将view 重置回原来大小
                if (System.currentTimeMillis() - mStartTime < 1000) {
                    return;
                }
                mIsRecording = false;
                mOnRecordListener.stopRecord();
            } else {
                mIsRecording = true;
                invalidate();
                mOnRecordListener.startRecord();
                mStartTime = System.currentTimeMillis();
            }
        }
    }

    public void stop() {
        mIsRecording = false;
        mSecond = 0;
        invalidate();
    }

    public void resetRecordButton() {
        mIsRecording = false;
        mSecond = 0;
        invalidate();
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public interface OnRecordListener {
        void takePic();

        void startRecord();

        void stopRecord();
    }
}
