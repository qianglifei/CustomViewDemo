package com.moible.qlf.customviewdemo.demo1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.moible.qlf.customviewdemo.R;

public class CustomTitleView1 extends View {


    //文本
    private String mTitleText;
    //文本大小
    private int mTitleTextSize;
    //文本颜色
    private int mTitleTextColor;

    /**
     * 绘制时，控制绘制文本的范围。
     */
    private Rect mRect;
    private Paint mPaint;

    public CustomTitleView1(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public CustomTitleView1(Context context)
    {
        super(context);
    }

    public CustomTitleView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray typedArray = context.getTheme().
                obtainStyledAttributes(attrs,
                R.styleable.customTitleView,
                defStyleAttr,
                0);

        int attr = typedArray.getIndexCount();
        Log.i("TAG:", "===CustomTitleView1:" + attr);
        for (int i = 0; i < attr ; i++) {
            int attrStatus = typedArray.getIndex(i);
            switch (attrStatus){
                case R.styleable.CustomTitleView1_titleText1:
                    mTitleText = typedArray.getString(attrStatus);
                    break;
                case R.styleable.CustomTitleView1_titleText1Color:
                    mTitleTextColor = typedArray.getColor(attrStatus,Color.BLACK);
                    break;
                case R.styleable.CustomTitleView1_titleText1Size:
                    //默认设置为16sp，TypedValue也能把sp 变为px
                    mTitleTextSize = typedArray.getDimensionPixelSize(attrStatus,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                                    16,
                                    getResources().getDisplayMetrics()));
                    break;
            }
        }


        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setTextSize(mTitleTextSize);
        mPaint.setColor(mTitleTextColor);

        mRect = new Rect();
        mPaint.getTextBounds(mTitleText,0,mTitleText.length(),mRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(mTitleText, getWidth() / 2 - mRect.width() / 2, getHeight() / 2 + mRect.height() / 2, mPaint);
    }
}
