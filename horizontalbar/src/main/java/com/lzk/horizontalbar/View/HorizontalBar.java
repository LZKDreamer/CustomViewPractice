package com.lzk.horizontalbar.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.print.PrinterId;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.lzk.horizontalbar.R;
import com.lzk.horizontalbar.View.Utils.DensityUtil;

public class HorizontalBar extends View {

    private static final int DEFAULT_BAR_HEIGHT = 10;//dp
    private static final int DEFAULT_BOTTOM_COLOR = Color.GRAY;
    private static final int DEFAULT_VALUE_BAR_COLOR = Color.BLUE;
    private static final int DEFAULT_CURRENT_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE_TEXT_COLOR = Color.DKGRAY;
    private static final int DEFAULT_VALUE_TEXT_SIZE = 16;//sp
    private static final int VISIBLE = 0;
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
        mPaint.setTextSize(mValueTextSize);
        mTextOffset = DensityUtil.dp2px(context,DEFAULT_TEXT_OFFSET);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
        mRealWidth = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
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
        float radio = mCurrentValue/mMaxValue*1.0f;
        float valueBarWidth = radio*mRealWidth;
        String valueText = mCurrentValue+"%";
        float textWidth = mPaint.measureText(valueText);
        float textHeight = mPaint.descent() - mPaint.ascent();
        if (valueBarWidth+textWidth+mTextOffset > mRealWidth){
            drawBottomBar = false;
            valueBarWidth = mRealWidth-textWidth-mTextOffset;
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
        canvas.drawLine(0,0,valueBarWidth,0,mPaint);
        //画数字
        if (mValueTextVisible == VISIBLE){
            mPaint.setColor(mValueTextColor);
            canvas.drawText(valueText,valueBarWidth+mTextOffset,-textHeight/2.0f,mPaint);
        }
        canvas.restore();
    }
}
