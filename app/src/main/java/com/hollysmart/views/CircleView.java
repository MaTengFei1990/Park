package com.hollysmart.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hollysmart.park.R;

public class CircleView extends View {

    private Paint mPaint;

    private float textSize;

    private String centerText;

    private int  circleColor;

    private int  textColor;


    private float outWeight;
    private float outHeight;


    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }



    private void initView(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.CircleView);
        if (ta != null) {
            textSize = ta.getFloat(R.styleable.CircleView_textSize, 35);
            centerText = ta.getString(R.styleable.CircleView_text);
            circleColor = ta.getInt(R.styleable.CircleView_circleColor, 6);
            textColor = ta.getInt(R.styleable.CircleView_textColor, 6);
            ta.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);


        outWeight = widthSpecSize;

        outHeight = heightSpecSize;

    }

    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }


    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(circleColor);
        canvas.drawCircle(outHeight/2,outHeight/2,40,mPaint);
        mPaint.setColor(textColor);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(textSize);

        //计算baseline
        Paint.FontMetrics fontMetrics=mPaint.getFontMetrics();
        float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        float baseline=outHeight/2+distance;
        canvas.drawText(centerText,outHeight/2,baseline,mPaint);



    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);



    }




}
