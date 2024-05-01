package com.ssafy.chelitalk.activity.carousel;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

public class CustomTypefaceScan extends MetricAffectingSpan {
    private Typeface typeface;

    public CustomTypefaceScan(Typeface typeface){
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {
        textPaint.setTypeface(typeface);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(typeface);
    }
}
