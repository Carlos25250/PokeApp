package com.example.pokeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class HexagonView extends LinearLayout {

    private final Path hexagonPath = new Path();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int hexagonColor = Color.parseColor("#CC1E293B");

    public HexagonView(Context context) {
        super(context);
        init();
    }

    public HexagonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(hexagonColor);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#33FFFFFF"));
        strokePaint.setStrokeWidth(3f);
    }

    public void setHexagonColor(int color) {
        this.hexagonColor = color;
        fillPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePath(w, h);
    }

    private void calculatePath(float width, float height) {
        hexagonPath.reset();
        hexagonPath.moveTo(width * 0.25f, 0f);
        hexagonPath.lineTo(width * 0.75f, 0f);
        hexagonPath.lineTo(width, height * 0.5f);
        hexagonPath.lineTo(width * 0.75f, height);
        hexagonPath.lineTo(width * 0.25f, height);
        hexagonPath.lineTo(0f, height * 0.5f);
        hexagonPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(hexagonPath, fillPaint);
        canvas.drawPath(hexagonPath, strokePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(hexagonPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
