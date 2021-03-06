package com.moible.qlf.customviewdemo.surfaceviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.moible.qlf.customviewdemo.R;
import com.moible.qlf.customviewdemo.doubleseekbar.DoubleSeekBar;
import com.moible.qlf.customviewdemo.doubleseekbar.Scale;
import com.moible.qlf.customviewdemo.util.DensityUtil;

import java.util.HashSet;
import java.util.Set;

public class SurfaceViewDemo extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * SurfaceVIew里面有一个getHolder的方法，我们可以获取一个SurfaceHolder，
     * 通过SurfaceHolder我们可以监听SurfaceView的生命周期以及获取Canvas对象
     */
    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas对象
     */
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     */
    private Thread t;

    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    //刻度数量
    private Integer scaleNumber = 0;
    //绘画刻度，字体
    private Paint mPaintScale, mPaintText;
    //将要绘制的图片
    private Bitmap seekBarSlideBlockBit, seekBarForegroundBit, seekBarBackgroundBit;
    //seekBar的起始位置,长度
    private int mSeekBarBegin = 0, mSeekBarWidth, mSeekBarTop = 0, mSeekBarBottom;
    //控制图片绘制的范围
    private Rect mRectBack, mRectFore, mRectLeft, mRectRight;
    /**
     * 滑块的宽度以及高度
     */
    private static final Integer SLIDER_WIDTH = 50;
    private static final Integer SLIDER_HEIGHT = 100;
    private String[] scaleArray = new String[10];
    private Integer[] titleDistance = new Integer[11];
    /**
     * 字体的绘制范围
     */
    private Rect rectText;
    /**
     * 按下的坐标
     */
    private Integer mPreX;
    /**
     * 进度条的左右位置
     */
    private int currentX, currentXT = 0;
    private int currentX2, currentX2T = 0;
    /**
     * 进度条Y起始，终止
     *
     * @param context
     */
    private int SEEKBAR_Y_HEIGHT_B = DensityUtil.dip2px(getContext(), 55);
    private int SEEKBAR_Y_HEIGHT_E = DensityUtil.dip2px(getContext(), 60);

    /**
     * 进度条的间距
     *
     * @param context
     */

    private static int sDistance = 0;
    private int[] dValue = new int[11];
    private int minValueIndex = 0;
    private int minValueIndex2 = 10;
    private boolean isSoliderLeft = false, isSoliderRight = false;
    private int[] dValue2 = new int[11];

    /**
     * 刻度数组
     *
     * @param context
     */

    private Set<Scale> mScale = new HashSet<>();

    /**
     * @param context
     */
    private DoubleSeekBar.OnScaleListener iScaleListener = null;

    private static final String INSTANCE = "instance";

    public SurfaceViewDemo(Context context) {
        this(context, null);
    }

    public SurfaceViewDemo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //设置可获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置屏幕常亮
        this.setKeepScreenOn(true);

        //属性数组
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DoubleSeekBar, defStyleAttr, 0);

        //获取刻度数组
        scaleArray = this.getResources().getStringArray(R.array.titleNameArr);
        for (int i = 0; i < scaleArray.length; i++) {
            Scale scale = new Scale();
            scale.setIndex(i);
            scale.setScaleValue(scaleArray[i]);
            mScale.add(scale);
        }

        int a = typedArray.getIndexCount();

        for (int i = 0; i < a; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.DoubleSeekBar_scaleNumber:
                    scaleNumber = typedArray.getInteger(attr, 0);
                    break;
                default:
            }
        }

        typedArray.recycle();
//      Paint.FILTER_BITMAP_FLAG 是使位图过滤的位掩码标志
//      Paint.ANTI_ALIAS_FLAG 是使位图抗锯齿的标志
//      Paint.DITHER_FLAG 是使位图进行有利的抖动的位掩码标志
        mPaintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        mPaintScale.setAntiAlias(true);
//      Android在用画笔的时候有三种Style，分别是
//      Paint.Style.STROKE 只绘制图形轮廓（描边）
//      Paint.Style.FILL 只绘制图形内容
//      Paint.Style.FILL_AND_STROKE 既绘制轮廓也绘制内容
        mPaintScale.setStyle(Paint.Style.STROKE);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);

        //滑动块图
        seekBarSlideBlockBit = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_tuodong);
        if (seekBarSlideBlockBit == null) {
            Toast.makeText(context, "你的图片是空的", Toast.LENGTH_SHORT).show();
        }
        //进度条前景图
        seekBarForegroundBit = BitmapFactory.decodeResource(getResources(), R.mipmap.blue);
        //进度条背景图
        seekBarBackgroundBit = BitmapFactory.decodeResource(getResources(), R.mipmap.back);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //关闭线程
        isRunning = false;
    }

    @Override
    public void run() {
        //不断的进行绘制
        while (isRunning) {
            draw();
        }
    }

    private void draw() {
        //获取canvas
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //drawSomething...
                drawText(mCanvas);
                drawBitmap(mCanvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    private void drawBitmap(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        //绘制文字
        drawText(canvas);

        //绘制背景
        mRectBack = new Rect(SLIDER_WIDTH / 2,
                SEEKBAR_Y_HEIGHT_B,
                getWidth() - SLIDER_WIDTH / 2,
                SEEKBAR_Y_HEIGHT_E);
        canvas.drawBitmap(seekBarBackgroundBit, null, mRectBack, mPaintScale);

        //绘制前景
        mRectFore = new Rect(currentX + SLIDER_WIDTH / 2,
                SEEKBAR_Y_HEIGHT_B,
                currentX2 - SLIDER_WIDTH / 2,
                SEEKBAR_Y_HEIGHT_E);
        canvas.drawBitmap(seekBarForegroundBit, null, mRectFore, mPaintScale);

        //绘制左滑块
        mRectLeft = new Rect(currentX,
                DensityUtil.dip2px(getContext(), 50),
                SLIDER_WIDTH + currentX,
                getHeight());
        canvas.drawBitmap(seekBarSlideBlockBit, null, mRectLeft, mPaintScale);

        //绘制右滑块
        mRectRight = new Rect(currentX2,
                DensityUtil.dip2px(getContext(), 50),
                currentX2 - SLIDER_WIDTH, getHeight());
        Log.i("TAG", "===onDrawCurrentX2: " + currentX2);
        Log.i("TAG", "===onDrawHeight: " + getHeight());
        canvas.drawBitmap(seekBarSlideBlockBit, null, mRectRight, mPaintScale);
    }

    private void drawText(Canvas canvas) {
        //刻度坐标数组
        for (int i = 0; i < scaleArray.length; i++) {
            titleDistance[i] = (mSeekBarWidth - SLIDER_WIDTH) / 10 * i;
            Log.i("TAG", "===drawText: " + titleDistance[i]);
        }
        sDistance = titleDistance[1];
        for (int n = 0; n < scaleArray.length; n++) {
            int measreTextWidth = (int) mPaintText.measureText(scaleArray[n]);
            Log.i("TAG", "===drawTextMeasure: " + measreTextWidth);
//            canvas.drawText(scaleArray[n], sDistance * n + SLIDER_WIDTH / 2 - measreTextWidth / 2, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                    40,getResources().getDisplayMetrics()), mPaintText);
            int measureTextWidth = (int) mPaintText.measureText(scaleArray[n]);
            Log.i("TAG", "===drawTextMeasure: " + measureTextWidth);
            if (n == scaleArray.length - 1) {
                canvas.drawText(scaleArray[n], sDistance * n + SLIDER_WIDTH / 2 - measureTextWidth * 3 / 4, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        30, getResources().getDisplayMetrics()), mPaintText);
            } else if (n == scaleArray.length - 2) {
                canvas.drawText(scaleArray[n], sDistance * n + SLIDER_WIDTH / 2 - measureTextWidth * 3 / 4, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        30, getResources().getDisplayMetrics()), mPaintText);
            } else {
                canvas.drawText(scaleArray[n], sDistance * n + SLIDER_WIDTH / 2 - measureTextWidth / 2, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        30, getResources().getDisplayMetrics()), mPaintText);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreX = x;
//                Log.i("TAG", "===onTouchEventX:" + mPreX);
//                Log.i("TAG", "===onTouchEventCurrentX:" + currentX);
//                Log.i("TAG", "===onTouchEventCurrentX2:" + currentX2);
//                Log.i("TAG", "===onTouchEventCurrentX-X2:" +(currentX2 - currentX));
                break;
            case MotionEvent.ACTION_MOVE:
                //判断手指移动的左滑块还是右滑块
                //左滑块在移动
                if (Math.abs(mPreX - mRectLeft.right) < Math.abs(mPreX - mRectRight.left)) {
                    isSoliderLeft = true;
                    isSoliderRight = false;
                    if (x < mRectLeft.width() / 2) {
                        currentX = 0;
                    } else {
                        currentX = x;
                    }
//                    Log.i("TAG", "===onTouchEventCurrentX:" + currentX);
//                    Log.i("TAG", "===onTouchEventCurrentX2:" + currentX2);
//                    Log.i("TAG", "===onTouchEventCurrentX-X2:" +(currentX2 - currentX));
                }
                //右滑块在移动
                if (Math.abs(mPreX - mRectLeft.right) >= Math.abs(mPreX - mRectRight.left)) {
                    isSoliderLeft = false;
                    isSoliderRight = true;
                    if (mPreX > mSeekBarWidth) {
                        currentX2 = getWidth();
                    } else {
                        currentX2 = x;
                    }
                }
//                Log.i("TAG", "===MnTouchEventCurrentX:" + currentX);
//                Log.i("TAG", "===MnTouchEventCurrentX2:" + currentX2);
//                Log.i("TAG", "===MnTouchEventCurrentX-X2:" +(currentX2 - currentX));
                if (currentX2 - currentX >= sDistance) {
                    invalidate();
                    currentXT = currentX;
                    currentX2T = currentX2;
                }
                break;
            case MotionEvent.ACTION_UP:
                currentX = currentXT;
                currentX2 = currentX2T;
                if (isSoliderLeft) {
//                    for (int i = 0; i < titleDistance.length ; i++) {
//                        //Log.i("TAG", "===onTouchEvent: " + (currentX - titleDistance[i]));
//                        dValue[i] = Math.abs(currentX - titleDistance[i]);
//                        //Log.i("TAG", "====onTouchEvent: " + dValue[i]);
//                    }
//                    minValueIndex = getMinIndex(dValue);
                    minValueIndex = Math.round(currentX / sDistance);
                    Log.i("TAG", "===onTouchEventIndex: " + (currentX / sDistance));
                    Log.i("TAG", "===onTouchEventDistance: " + minValueIndex);
                    currentX = (titleDistance[minValueIndex]);
                    Log.i("TAG", "===onTouchEvent: " + titleDistance[minValueIndex]);
                    Log.i("TAG", "===onTouchEventCurrent: " + currentX);
                    invalidate();
                } else if (isSoliderRight) {
//                    for (int i = 0; i < titleDistance.length ; i++) {
//                        dValue2[i] = Math.abs(currentX2 - titleDistance[i]);
//                    }
//                   minValueIndex2 = getMinIndex(dValue2);
                    minValueIndex2 = (int) Math.round(currentX2 / (sDistance + 0.0));
                    Log.i("TAG", "===onTouchEvent: " + currentX2);
                    Log.i("TAG", "===onTouchEvent: " + sDistance);
                    Log.i("TAG", "===onTouchEvent: " + (currentX2 / sDistance));
                    currentX2 = titleDistance[minValueIndex2] + SLIDER_WIDTH;
                    Log.i("TAG", "===onTouchEventminValueIndex2: " + minValueIndex2);
                    invalidate();
                }
                iScaleListener.getDoubleSeekValue(minValueIndex + "", minValueIndex2 + "");
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //宽为精确值或,match_parent
        if (widthMode == MeasureSpec.EXACTLY) {
            mSeekBarWidth = width;
            currentX2 = mSeekBarWidth;
        } else {
            //宽一般为wrap_content
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mSeekBarBottom = height;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public interface OnScaleListener {
        void getDoubleSeekValue(String strLow, String strHigh);
    }

    public void setOnScaleListener(DoubleSeekBar.OnScaleListener listener) {
        iScaleListener = listener;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        invalidate();
    }
}
