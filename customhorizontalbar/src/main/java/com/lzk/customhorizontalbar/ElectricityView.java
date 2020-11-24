package com.lzk.customhorizontalbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Author: LiaoZhongKai
 * Date: 2020/11/23 11:08
 * Description:水平电量View
 */
public class ElectricityView extends View {

    //region 常量
    private static final int DEFAULT_BACKGROUND_COLOR = Color.GRAY;
    private static final int DEFAULT_VALUE_COLOR = Color.GREEN;
    private static final float DEFAULT_CORNER = 16f;//px
    private static final int DEFAULT_HEIGHT = 40;//px
    private static final int MAX_PROGRESS = 100;
    //endregion

    //region 属性值
    //底部颜色
    private int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    //进度条颜色
    private int mValueColor = DEFAULT_VALUE_COLOR;
    //进度条圆角
    private float mCorner = DEFAULT_CORNER;
    //进度
    private int mProgress = 0;
    //endregion

    private Paint mPaint;

    //去掉Padding后的宽度
    private int mRealWidth;

    //去掉Padding后的高度
    private int mRealHeight;

    public ElectricityView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ElectricityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.ElectricityView);
        mBackgroundColor = typedArray.getColor(R.styleable.ElectricityView_background_color, DEFAULT_BACKGROUND_COLOR);
        mValueColor = typedArray.getColor(R.styleable.ElectricityView_value_color,DEFAULT_VALUE_COLOR);
        mCorner = typedArray.getDimension(R.styleable.ElectricityView_corner,DEFAULT_CORNER);
        mProgress = typedArray.getInteger(R.styleable.ElectricityView_progress,0);
        typedArray.recycle();
    }

    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
        mRealWidth = getMeasuredWidth()-getPaddingStart()-getPaddingEnd();
        mRealHeight = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = getMeasuredHeight()+getPaddingBottom()+getPaddingTop();
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,Math.min(specSize,DEFAULT_HEIGHT));
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingStart(),getPaddingTop());
        drawBottomBar(canvas);
        drawValueBar(canvas);
        canvas.restore();
    }

    //画底部进度条
    private void drawBottomBar(Canvas canvas){
        mPaint.setColor(mBackgroundColor);
        RectF rectF = new RectF(0,0,mRealWidth,mRealHeight);
        canvas.drawRoundRect(rectF,mCorner,mCorner,mPaint);
    }

    //画进度条
    private void drawValueBar(Canvas canvas){
        mPaint.setColor(mValueColor);
        float width = getProgressBarWidth();
        if (width > 0 && width<mCorner){//如果进度条宽度比圆角的宽度还小，就默认宽度为圆角的宽度，不然很难看
            width = mCorner;
        }
        RectF rectF = new RectF(0,0,width,mRealHeight);
        canvas.drawRoundRect(rectF,mCorner,mCorner,mPaint);
    }

    private float getProgressBarWidth(){
        if (mProgress > MAX_PROGRESS) {
            mProgress = MAX_PROGRESS;
        }

        float ratio = mProgress*1f/(MAX_PROGRESS*1f);
        if (ratio > 1f){
            ratio = 1f;
        }
        return mRealWidth*ratio;
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress){
        mProgress = progress;
        invalidate();
    }
}
