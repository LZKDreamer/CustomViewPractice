package com.lzk.customhorizontalbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class HorizontalBar extends View {

    private static final int DEFAULT_BAR_HEIGHT = 10;//dp
    private static final int DEFAULT_BOTTOM_COLOR = Color.GRAY;
    private static final int DEFAULT_VALUE_BAR_COLOR = Color.BLUE;
    private static final int DEFAULT_CURRENT_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE_TEXT_COLOR = Color.GREEN;
    private static final int DEFAULT_VALUE_TEXT_SIZE = 16;//sp
    private static final int VISIBLE = 0;
    private static final int GONE = 1;
    private static final int DEFAULT_TEXT_OFFSET = 5;//dp

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
    //数字颜色
    private int mValueTextColor;
    //数字大小
    private int mValueTextSize;
    //是否显示数字
    private int mValueTextVisible;
    //Paint;
    private Paint mPaint;
    //除去padding的宽度
    private int mRealWidth;
    //字与bar的间距
    private int mTextOffset ;
    //笔头的宽度
    private int mPaintCapWidth;
    //文字Paint
    private Paint mTextPaint;



    public HorizontalBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint(context);
    }

    private void initAttrs(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.HorizontalBar);
        mBarHeight = typedArray.getDimensionPixelSize(R.styleable.HorizontalBar_bar_height,
                DensityUtil.dp2px(context,DEFAULT_BAR_HEIGHT));
        mBottomColor = typedArray.getColor(R.styleable.HorizontalBar_bottom_color,DEFAULT_BOTTOM_COLOR);
        mValueBarColor = typedArray.getColor(R.styleable.HorizontalBar_value_bar_color,DEFAULT_VALUE_BAR_COLOR);
        mCurrentValue = typedArray.getInt(R.styleable.HorizontalBar_current_value,DEFAULT_CURRENT_VALUE);
        mMaxValue = typedArray.getInt(R.styleable.HorizontalBar_max_value,DEFAULT_MAX_VALUE);
        mValueTextColor = typedArray.getColor(R.styleable.HorizontalBar_value_text_color,DEFAULT_VALUE_TEXT_COLOR);
        mValueTextSize = typedArray.getDimensionPixelSize(R.styleable.HorizontalBar_value_text_size,
                DensityUtil.sp2px(context,DEFAULT_VALUE_TEXT_SIZE));
        mValueTextVisible = typedArray.getInt(R.styleable.HorizontalBar_value_text_visible,VISIBLE);
        typedArray.recycle();
    }

    private void initPaint(Context context){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mValueTextSize);
        mTextOffset = DensityUtil.dp2px(context,DEFAULT_TEXT_OFFSET);
        mPaintCapWidth = mBarHeight/2;
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
            float textHeight = mPaint.descent() - mPaint.ascent();
            result = (int)Math.max(Math.abs(textHeight),mBarHeight)
                        +getPaddingTop()+getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(),getHeight()/2.0f);
        boolean drawBottomBar = true;
        float radio = mCurrentValue*1.0f/mMaxValue;
        float valueBarWidth = radio*mRealWidth;
        String valueText = mCurrentValue+"%";
        float textWidth = mTextPaint.measureText(valueText);
        float textHeight = (mTextPaint.descent() + mTextPaint.ascent())/2;
        if ( mCurrentValue >= mMaxValue){
            drawBottomBar = false;
        }
        //画底部bar
        if (drawBottomBar){
            mPaint.setColor(mBottomColor);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(mBarHeight);
            canvas.drawLine(valueBarWidth,0,mRealWidth,0,mPaint);
        }
        //画顶部bar
        mPaint.setColor(mValueBarColor);
        mPaint.setStrokeWidth(mBarHeight);
        if (valueBarWidth > 0 && valueBarWidth > mPaintCapWidth){
            canvas.drawLine(mPaintCapWidth,0,valueBarWidth,0,mPaint);
        }
        //画数字
        if (mValueTextVisible == VISIBLE){
            float endTextX = valueBarWidth+mTextOffset+mPaintCapWidth;
            if (endTextX+textWidth >= mRealWidth){
                endTextX = mRealWidth-textWidth;
            }
            mTextPaint.setColor(mValueTextColor);
            canvas.drawText(valueText,endTextX,-textHeight,mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 设置显示数据及样式
     * @param currentValue 当前值
     * @param maxValue 总量
     * @param showValueText 是否现实顶部bar右侧文字
     */
    public void setUI(int currentValue,int maxValue,boolean showValueText){
        mCurrentValue = currentValue;
        mMaxValue = maxValue;
        mValueTextVisible = showValueText?VISIBLE:GONE;
        invalidate();
    }
}
