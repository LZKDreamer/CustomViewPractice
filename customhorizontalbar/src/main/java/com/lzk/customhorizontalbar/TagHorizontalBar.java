package com.lzk.customhorizontalbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.lang.reflect.MalformedParameterizedTypeException;

public class TagHorizontalBar extends View {
    private static final int DEFAULT_BAR_HEIGHT = 10;//dp
    private static final int DEFAULT_BOTTOM_COLOR = Color.GRAY;
    private static final int DEFAULT_VALUE_BAR_COLOR = Color.BLUE;
    private static final int DEFAULT_CURRENT_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_TAG_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_TAG_TEXT_SIZE = 16;//sp
    private static final int DEFAULT_TAG_PERCENT_TEXT_SIZE = 20;//sp
    private static final int DEFAULT_OFFSET = 5;//dp
    private static final int DEFAULT_TRIANGLE_HEIGHT = DEFAULT_BAR_HEIGHT;

    //bar高度
    private int mBarHeight;
    //底部颜色
    private int mBottomColor;
    //顶部bar颜色
    private int mValueBarColor;
    //当前值
    private int mCurrentValue;
    //最大值
    private int mMaxValue;
    //标签文字颜色
    private int mTagTextColor;
    //标签文字大小
    private int mTagTextSize;
    //标签百分比文字大小
    private int mTagPercentTextSize;
    //标签文字
    private String mTagText;
    //除去Padding的宽度
    private int mRealWidth;
    //间距
    private int mOffset ;
    //笔头的宽度
    private int mPaintCapWidth;
    //三角形的高度
    private int mTriangleHeight;
    private Path mTrianglePath = new Path();
    //Paint
    private Paint mTagTextPaint;
    private Paint mTagPercentPaint;
    private Paint mPaint;


    public TagHorizontalBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint(context);

    }

    public TagHorizontalBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint(context);
    }


    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.TagHorizontalBar);
        mBarHeight = typedArray.getDimensionPixelSize(R.styleable.TagHorizontalBar_bar_height,
                DensityUtil.dp2px(context,DEFAULT_BAR_HEIGHT));
        mBottomColor = typedArray.getColor(R.styleable.TagHorizontalBar_bottom_color,DEFAULT_BOTTOM_COLOR);
        mValueBarColor = typedArray.getColor(R.styleable.TagHorizontalBar_value_bar_color,DEFAULT_VALUE_BAR_COLOR);
        mCurrentValue = typedArray.getInt(R.styleable.TagHorizontalBar_current_value,DEFAULT_CURRENT_VALUE);
        mMaxValue = typedArray.getInt(R.styleable.TagHorizontalBar_max_value,DEFAULT_MAX_VALUE);
        mTagTextColor = typedArray.getColor(R.styleable.TagHorizontalBar_tag_text_color,DEFAULT_TAG_TEXT_COLOR);
        mTagTextSize = typedArray.getDimensionPixelSize(R.styleable.TagHorizontalBar_tag_text_size,
                DensityUtil.sp2px(context,DEFAULT_TAG_TEXT_SIZE));
        mTagPercentTextSize = typedArray.getDimensionPixelSize(R.styleable.TagHorizontalBar_tag_percent_text_size,
                DensityUtil.sp2px(context,DEFAULT_TAG_PERCENT_TEXT_SIZE));
        mTagText = typedArray.getString(R.styleable.TagHorizontalBar_tag_text);
        typedArray.recycle();
    }

    private void initPaint(Context context){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mTagTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTagTextPaint.setTextSize(mTagTextSize);
        mTagPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTagPercentPaint.setTextSize(mTagPercentTextSize);
        mOffset = DensityUtil.dp2px(context,DEFAULT_OFFSET);
        mPaintCapWidth = mBarHeight/2;
        mTriangleHeight = DensityUtil.dp2px(context,DEFAULT_TRIANGLE_HEIGHT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
        mRealWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight()-mPaintCapWidth;
    }

    private int measureHeight(int heightMeasureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            float tagTextHeight = mTagTextPaint.descent() - mTagTextPaint.ascent();
            float tagPercentHeight = mTagPercentPaint.descent() - mTagPercentPaint.ascent();
            float maxTagHeight = Math.max(tagTextHeight,tagPercentHeight);
            result = (int) ((mBarHeight+mOffset*2+mTriangleHeight+maxTagHeight)+getPaddingTop()+getPaddingBottom());
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop()+mBarHeight/2.0f);
        float radio = mCurrentValue*1.0f/mMaxValue;
        float valueBarWidth = radio*mRealWidth;
        //画底部
        if (valueBarWidth < mRealWidth){
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setColor(mBottomColor);
            mPaint.setStrokeWidth(mBarHeight);
            canvas.drawLine(mPaintCapWidth,0,mRealWidth,0, mPaint);
        }

        if (valueBarWidth > 0) {
            //画顶部bar
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setColor(mValueBarColor);
            mPaint.setStrokeWidth(mBarHeight);
            canvas.drawLine(mPaintCapWidth,0,valueBarWidth,0,mPaint);
            //画三角形
            float x1 = valueBarWidth+mPaintCapWidth < mRealWidth ? valueBarWidth+mPaintCapWidth-mTriangleHeight/2.0f:valueBarWidth+mPaintCapWidth-mTriangleHeight/2.0f;
            float y1 = mBarHeight/2.0f + mOffset;
            float x2 = x1-mTriangleHeight/2.0f;
            float y2 = y1+mTriangleHeight;
            float x3 = x1+mTriangleHeight/2.0f;
            float y3 = y2;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mValueBarColor);
            mTrianglePath = new Path();
            mTrianglePath.moveTo(x1,y1);
            mTrianglePath.lineTo(x2,y2);
            mTrianglePath.lineTo(x3,y3);
            mTrianglePath.close();
            canvas.drawPath(mTrianglePath,mPaint);
        }
        //画文字
        float tagTextWidth=0.0f;
        float tagTextHeight=0.0f;
        String tagPercentText = mCurrentValue+"%";
        float tagPercentWidth = mTagPercentPaint.measureText(tagPercentText);
        float tagPercentHeight = mTagPercentPaint.descent()-mTagPercentPaint.ascent();
        float textWidth = 0.0f;
        if (!TextUtils.isEmpty(mTagText)){
            tagTextWidth = mTagTextPaint.measureText(mTagText);
            tagTextHeight = mTagTextPaint.descent()-mTagTextPaint.ascent();
            textWidth = tagTextWidth+mOffset+tagPercentWidth;
        }else {
            textWidth = mOffset+tagPercentWidth;
        }
        //文字的绘制起点坐标
        float x = 0.0f;
        float y = 0.0f;

        if (valueBarWidth+textWidth/2 > mRealWidth){
            mTagTextPaint.setColor(mTagTextColor);
            x = mRealWidth - textWidth;
        }else if (textWidth/2 > valueBarWidth){
            x = valueBarWidth;
        }else {
            x = valueBarWidth - textWidth/2;
        }
        //画Tag文字
        y = mBarHeight + mOffset*2+mTriangleHeight+Math.max(tagTextHeight,tagPercentHeight)/2;
        if (!TextUtils.isEmpty(mTagText)){
            mTagTextPaint.setColor(mTagTextColor);
            canvas.drawText(mTagText,x,y,mTagTextPaint);
        }
        //画百分比
        mTagPercentPaint.setColor(mValueBarColor);
        canvas.drawText(tagPercentText,x+mOffset+tagTextWidth,y,mTagPercentPaint);
        canvas.restore();
    }

    /**
     * 设置显示的数据
     * @param currentValue 当前值
     * @param maxValue 总量
     * @param tagText 底部标签文字
     */
    public void setUI(int currentValue,int maxValue,String tagText){
        mCurrentValue = currentValue;
        mMaxValue = maxValue;
        mTagText = tagText;
        invalidate();
    }
}
