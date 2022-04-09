package com.bihe0832.android.lib.text.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.style.ReplacementSpan;

public class ZixieTextRadiusBackgroundSpan extends ReplacementSpan {

    private final static int DEFAULT_REDIUS = 8;
    private final static int DEFAULT_MARGIN = 12;
    private final static int DEFAULT_PADDING_LEFT = 12;
    private final static int DEFAULT_PADDING_TOP = 8;
    private final static float DEFAULT_TEXT_SIZE = 0f;
    private final static int DEFAULT_TEXT_COLOR = 0;


    private int mTextDataLength;
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mRadius = DEFAULT_REDIUS;
    private int mMargin = DEFAULT_PADDING_LEFT;
    private int mPaddingLeft = DEFAULT_PADDING_LEFT;
    private int mPaddingTop = DEFAULT_PADDING_TOP;
    private float mTextSize = DEFAULT_TEXT_SIZE;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private Typeface mTypeface = null;


    public ZixieTextRadiusBackgroundSpan(int bgColor) {
        this(bgColor, DEFAULT_REDIUS);
    }

    public ZixieTextRadiusBackgroundSpan(int bgColor, int radius) {
        this(bgColor, radius, DEFAULT_TEXT_SIZE);
    }

    public ZixieTextRadiusBackgroundSpan(int bgColor, int radius, float textSize) {
        this(bgColor, radius, DEFAULT_MARGIN, DEFAULT_PADDING_LEFT, DEFAULT_PADDING_TOP, textSize, DEFAULT_TEXT_COLOR,
                null);
    }

    public ZixieTextRadiusBackgroundSpan(int bgColor, int radius, int paddingLeft, int paddingTop) {
        this(bgColor, radius, DEFAULT_MARGIN, paddingLeft, paddingTop, DEFAULT_TEXT_SIZE, DEFAULT_TEXT_COLOR, null);
    }

    public ZixieTextRadiusBackgroundSpan(int bgColor, int radius, int textSize, int textColor, Typeface mTypeface) {
        this(bgColor, radius, DEFAULT_MARGIN, DEFAULT_PADDING_LEFT, DEFAULT_PADDING_TOP, textSize, textColor,
                mTypeface);
    }

    public ZixieTextRadiusBackgroundSpan(int bgColor, int radius, int margin, int paddingLeft, int paddingTop,
            float textSize,
            int textColor, Typeface typeface) {
        initData(bgColor, radius, margin, paddingLeft, paddingTop, textSize, textColor, typeface);
    }

    private void initData(
            int bgColor,
            int radius,
            int margin,
            int paddingLeft,
            int paddingTop,
            float textSize,
            int textColor,
            Typeface typeface) {
        mBackgroundColor = bgColor;
        mRadius = radius;
        mMargin = margin;
        mPaddingLeft = paddingLeft;
        mPaddingTop = paddingTop;
        mTextSize = textSize;
        mTextColor = textColor;
        mTypeface = typeface;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float textSize = paint.getTextSize();
        if (mTextSize > 0 && paint.getTextSize() >= mTextSize) {
            paint.setTextSize(mTextSize);
        }
        mTextDataLength = (int) (paint.measureText(text, start, end) + 2 * mRadius + 2 * mPaddingLeft + 2 * mMargin);
        paint.setTextSize(textSize);
        return mTextDataLength;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
            Paint paint) {
        int color = paint.getColor();
        Typeface typeface = paint.getTypeface();
        float size = paint.getTextSize();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int sourceTextHeight = fm.descent - fm.ascent;
        int cent = fm.descent + fm.ascent;
        //字号过大，cent为负数，字号过小，cent为正
        int halfCenter = cent / 2;
        if (cent < 0) {
            halfCenter = (-cent) / 2;
        }
        int borderMaxBottom = y + cent;
        if (cent < 0) {
            borderMaxBottom = y + halfCenter;
        }
        if (borderMaxBottom > bottom) {
            borderMaxBottom = bottom - 1;
        }
        int borderMinTop = borderMaxBottom - sourceTextHeight - halfCenter;
        if (borderMinTop < top) {
            borderMinTop = top + 1;
        }

        canvas.save();
//        canvas.drawLine(0, borderMinTop, 3000, borderMinTop, paint);
//        paint.setColor(Color.BLUE);
//        canvas.drawLine(0, borderMaxBottom, 3000, borderMaxBottom, paint);

        if (mTextSize > 0 && paint.getTextSize() >= mTextSize) {
            paint.setTextSize(mTextSize);
        }
        paint.setColor(mBackgroundColor);//设置背景颜色
        paint.setAntiAlias(true);// 设置画笔的锯齿效果

        Paint.FontMetricsInt newFm = paint.getFontMetricsInt();
        int textHeight = newFm.descent - newFm.ascent;
        int realHeight = borderMaxBottom - borderMinTop;
        int baseHeight = borderMaxBottom - y;
        int space = (realHeight - baseHeight - textHeight) / 2;

        int realPadding = space;
        if (space > mPaddingTop) {
            realPadding = mPaddingTop;
        }

        int borderRealTop = borderMinTop + (space - realPadding);
        int borderRealBottom = borderMaxBottom - (space - realPadding);

        RectF oval = new RectF(x + mMargin, borderRealTop, x + mTextDataLength - mMargin,
                borderRealBottom);
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(oval, mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        int textBottom;
        if (realPadding > 0) {
            textBottom = y - (borderMaxBottom - y);
        } else {
            textBottom = borderRealBottom - (borderMaxBottom - y);
        }

//        canvas.drawLine(x + mRadius + mPaddingLeft, textBottom, 100, textBottom, paint);
//        paint.setColor(Color.BLUE);
//        canvas.drawLine(x + mRadius + mPaddingLeft, y, 100, y, paint);

        if (mTextColor != 0) {
            paint.setColor(mTextColor);
        } else {
            paint.setColor(color);
        }

        if (mTypeface != null) {
            paint.setTypeface(mTypeface);
        } else {
            paint.setTypeface(typeface);
        }
        canvas.drawText(text, start, end, x + mMargin + mRadius + mPaddingLeft, textBottom, paint);//绘制文字
        canvas.save();
        canvas.restore();
    }
}
