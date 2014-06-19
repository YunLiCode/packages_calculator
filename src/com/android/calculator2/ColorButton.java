/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calculator2;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Button with click-animation effect.
 */
class ColorButton extends Button implements OnClickListener {
    // int CLICK_FEEDBACK_COLOR;
    // static final int CLICK_FEEDBACK_INTERVAL = 10;
    // static final int CLICK_FEEDBACK_DURATION = 350;

    float mTextX;
    float mTextY;
    // long mAnimStart;
    OnClickListener mListener;
    Paint mFeedbackPaint;
    private Drawable mSrc;
//    private int mTextColor;

    // private Context mContext;

    public ColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // mContext = context;
        Calculator calc = (Calculator) context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorButtonSrc);
        mSrc = a.getDrawable(R.styleable.ColorButtonSrc_src);
//        mTextColor = a.getColor(R.styleable.ColorButtonSrc_textColor, -1);
        a.recycle();
        init(calc);
        mListener = calc.mListener;
        setOnClickListener(this);
    }

    public void onClick(View view) {
        mListener.onClick(this);
    }

    private void init(Calculator calc) {
        Resources res = getResources();

        // CLICK_FEEDBACK_COLOR = res.getColor(R.color.magic_flame);
        mFeedbackPaint = new Paint();
        mFeedbackPaint.setStyle(Style.STROKE);
        mFeedbackPaint.setStrokeWidth(2);
//        getPaint().setColor(res.getColor(R.color.button_text));jczou

        // mAnimStart = -1;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        measureText();
    }

    private void measureText() {
        Paint paint = getPaint();
        mTextX = (getWidth() - paint.measureText(getText().toString())) / 2;
        mTextY = (getHeight() - paint.ascent() - paint.descent()) / 2;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before,
            int after) {
        measureText();
    }

    // private void drawMagicFlame(int duration, Canvas canvas) {
    // int alpha = 255 - 255 * duration / CLICK_FEEDBACK_DURATION;
    // int color = CLICK_FEEDBACK_COLOR | (alpha << 24);

    // mFeedbackPaint.setColor(color);
    // canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, mFeedbackPaint);
    // }

    @Override
    public void onDraw(Canvas canvas) {
        // if (mAnimStart != -1) {
        // int animDuration = (int) (System.currentTimeMillis() - mAnimStart);
        //
        // if (animDuration >= CLICK_FEEDBACK_DURATION) {
        // mAnimStart = -1;
        // } else {
        // drawMagicFlame(animDuration, canvas);
        // postInvalidateDelayed(CLICK_FEEDBACK_INTERVAL);
        // }
        // } else if (isPressed()) {
        // drawMagicFlame(0, canvas);
        // }
        if (mSrc == null) {
            CharSequence text = getText();
            TextPaint paint = getPaint();
//            if(mTextColor != -1)
//                paint.setColor(mTextColor);
            paint.setColor(getCurrentTextColor());
            canvas.drawText(text, 0, text.length(), mTextX, mTextY, paint);
        } else {
            Bitmap bitmap = ((BitmapDrawable) mSrc).getBitmap();
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2,
                    (getHeight() - bitmap.getHeight()) / 2, getPaint());
        }
    }

    // public void animateClickFeedback() {
    // mAnimStart = System.currentTimeMillis();
    // invalidate();
    // }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            if (isPressed()) {
                // animateClickFeedback();
            } else {
                invalidate();
            }
            break;
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_CANCEL:
            invalidate();
            break;
        }

        return result;
    }
}
