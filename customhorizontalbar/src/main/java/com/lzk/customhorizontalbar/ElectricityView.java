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
    private static final int DEFAULT_VALUE_WARNING_COLOR = Color.RED;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_WARNING_TEXT_COLOR = Color.LTGRAY;
    private static final float DEFAULT_TEXT_SIZE = 24f;//px
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
    //电量警告色
    private int mValueWarningColor = DEFAULT_VALUE_WARNING_COLOR;
    //默认电量颜色
    private int mNormalValueColor = DEFAULT_VALUE_COLOR;
    //文字正常颜色
    private int mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;
    //文字警告颜色
    private int mWaningTextColor = DEFAULT_WARNING_TEXT_COLOR;
    //字体大小
    private float mTextSize = DEFAULT_TEXT_SIZE;
    //endregion

    //region Paint
    private Paint mBarPaint;
    private Paint mTextPaint;
    //endregion

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
        mNormalValueColor = mValueColor;
        mCorner = typedArray.getDimension(R.styleable.ElectricityView_corner,DEFAULT_CORNER);
        mProgress = typedArray.getInteger(R.styleable.ElectricityView_progress,0);
        mValueWarningColor = typedArray.getColor(R.styleable.ElectricityView_value_warning_color,DEFAULT_VALUE_WARNING_COLOR);
        mNormalTextColor = typedArray.getColor(R.styleable.ElectricityView_normal_text_color,DEFAULT_NORMAL_TEXT_COLOR);
        mWaningTextColor = typedArray.getColor(R.styleable.ElectricityView_warning_text_color,DEFAULT_WARNING_TEXT_COLOR);
        mTextSize = typedArray.getDimension(R.styleable.ElectricityView_text_size_px,DEFAULT_TEXT_SIZE);
        typedArray.recycle();
    }

    private void initPaint(){
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mNormalTextColor);
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
                int height = Math.min(result,Math.min(specSize,DEFAULT_HEIGHT));
                int textHeight = (int) (mTextPaint.descent() - mTextPaint.ascent());
                result = Math.max(height,textHeight);
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
        drawText(canvas);
        canvas.restore();
    }

    //画底部进度条
    private void drawBottomBar(Canvas canvas){
        mBarPaint.setColor(mBackgroundColor);
        RectF rectF = new RectF(0,0,mRealWidth,mRealHeight);
        canvas.drawRoundRect(rectF,mCorner,mCorner, mBarPaint);
    }

    //画进度条
    private void drawValueBar(Canvas canvas){
        if (mProgress > 20){
            mValueColor = mNormalValueColor;
        }else {
            mValueColor = mValueWarningColor;
        }
        mBarPaint.setColor(mValueColor);
        float width = getProgressBarWidth();
        if (width > 0 && width<mCorner){//如果进度条宽度比圆角的宽度还小，就默认宽度为圆角的宽度，不然很难看
            width = mCorner;
        }
        RectF rectF = new RectF(0,0,width,mRealHeight);
        canvas.drawRoundRect(rectF,mCorner,mCorner, mBarPaint);
    }

    //画文字
    private void drawText(Canvas canvas){
        String text = mProgress+"%";
        float textHeight = mTextPaint.descent() - mTextPaint.ascent();
        float textWidth = mTextPaint.measureText(text);
        float textBaseLineY = textHeight/2f - mTextPaint.descent();
        float textY = mRealHeight/2f + textBaseLineY;
        float textX = 0f;
        if (mProgress > 20){
            textX = getProgressBarWidth()/2f - textWidth/2f;
            mTextPaint.setColor(mNormalTextColor);
        }else {
            textX = mRealWidth/2f - textWidth/2f;
            mTextPaint.setColor(mWaningTextColor);
        }
        canvas.drawText(text,textX,textY,mTextPaint);
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
