package com.moible.qlf.customviewdemo.doubleseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.moible.qlf.customviewdemo.R;
import com.moible.qlf.customviewdemo.util.DensityUtil;

public class DoubleSeekBar extends View {
    //刻度数量
    private Integer scaleNumber = 0;
    //绘画刻度，字体
    private Paint mPaintScale,mPaintText;
    //将要绘制的图片
    private Bitmap seekBarSlideBlockBit,seekBarForegroundBit,seekBarBackgroundBit;
    //seekBar的起始位置,长度
    private int mSeekBarBegin = 0,mSeekBarWidth,mSeekBarTop = 0,mSeekBarBottom;
    //控制图片绘制的范围
    private Rect mRectBack,mRectFore,mRectLeft,mRectRight;
    //滑块的宽度以及高度
    private static final Integer SLIDER_WIDTH = 50;
    private static final Integer SLIDER_HEIGHT = 100;
    String[] scaleArray;
    Integer[] titleDistance = new Integer[10];
    public DoubleSeekBar(Context context) {
       this(context,null);
        Log.i("TAG", "===DoubleSeekBar1: " + mSeekBarBottom);
    }

    public DoubleSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //属性数组
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DoubleSeekBar,defStyleAttr,0);
        //获取刻度数组
        scaleArray = this.getResources().getStringArray(R.array.titleNameArr);

        int a = typedArray.getIndexCount();
        for (int i = 0; i < a ; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.DoubleSeekBar_scaleNumber:
                     scaleNumber = typedArray.getInteger(attr,0);
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

        mPaintScale.setAntiAlias(true);
//      Android在用画笔的时候有三种Style，分别是
//      Paint.Style.STROKE 只绘制图形轮廓（描边）
//      Paint.Style.FILL 只绘制图形内容
//      Paint.Style.FILL_AND_STROKE 既绘制轮廓也绘制内容
        mPaintScale.setStyle(Paint.Style.STROKE);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);

        //滑动块图
        seekBarSlideBlockBit = BitmapFactory.decodeResource(getResources(),R.mipmap.icon_tuodong);
        Log.i("TAG", "===DoubleSeekBar3: " + mSeekBarBottom);
        if(seekBarSlideBlockBit == null){
            Toast.makeText(context, "你的图片是空的", Toast.LENGTH_SHORT).show();

        }
        //进度条前景图
        seekBarForegroundBit = BitmapFactory.decodeResource(getResources(),R.mipmap.blue);
        //进度条背景图
        seekBarBackgroundBit = BitmapFactory.decodeResource(getResources(),R.mipmap.back);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //宽为精确值或,match_parent
        if (widthMode == MeasureSpec.EXACTLY){
            mSeekBarWidth = width;
            Log.i("TAG", "===onMeasure: " + DensityUtil.px2dip(getContext(),mSeekBarWidth));
        }else {
        //宽一般为wrap_content

        }


        if (heightMode == MeasureSpec.EXACTLY){
            mSeekBarBottom = height;
            Log.i("TAG", "===onMeasure: " + DensityUtil.px2dip(getContext(),mSeekBarBottom));
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("TAG", "===onDraw: "  + DensityUtil.px2dip(getContext(),getPaddingLeft()));


        for (int i = 0; i < scaleArray.length - 1 ; i++) {
           titleDistance[i] = mSeekBarWidth / 10 * i;
           Log.i("TAG", "===onDraw: " + titleDistance[i]);
        }

        mPaintText.setColor(Color.BLUE);
        mPaintText.setTextSize(30);

        for (int j = 0; j < titleDistance.length; j++) {
            canvas.drawText(scaleArray[j],titleDistance[j],40,mPaintText);
        }
        
//      //绘制背景
        mRectBack = new Rect(27,60,getWidth() - 27, 70);
        canvas.drawBitmap(seekBarBackgroundBit,null,mRectBack,mPaintScale);

//      //绘制前景
        mRectFore = new Rect(27,60,getWidth() - 27, 70);
        canvas.drawBitmap(seekBarForegroundBit,null,mRectFore,mPaintScale);

        //绘制左滑块
        mRectLeft = new Rect(0,50,SLIDER_WIDTH,getHeight());
        canvas.drawBitmap(seekBarSlideBlockBit,null,mRectLeft,mPaintScale);

        //绘制右滑块
        mRectRight = new Rect(mSeekBarWidth,50,mSeekBarWidth -50,getHeight());
        canvas.drawBitmap(seekBarSlideBlockBit,null,mRectRight,mPaintScale);
    }
}
