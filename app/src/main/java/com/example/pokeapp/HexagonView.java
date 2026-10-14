package com.example.pokeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class HexagonView extends LinearLayout {

    private final Path hexagonPath = new Path();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int hexagonColor = Color.parseColor("#E63946");

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

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#99FFFFFF"));
        strokePaint.setStrokeWidth(4.5f);
    }

    public void setHexagonColor(int color) {
        this.hexagonColor = color;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculatePath(w, h);
        updateGradient(w, h);
    }

    private void updateGradient(float width, float height) {
        int colorLight = adjustColor(hexagonColor, 1.25f);
        int colorDark = adjustColor(hexagonColor, 0.7f);
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, height,
                new int[]{colorLight, hexagonColor, colorDark},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);
    }

    private int adjustColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.min(255, Math.max(0, (int) (Color.red(color) * factor)));
        int g = Math.min(255, Math.max(0, (int) (Color.green(color) * factor)));
        int b = Math.min(255, Math.max(0, (int) (Color.blue(color) * factor)));
        return Color.argb(a, r, g, b);
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
