package com.example.pokeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HexagonView extends FrameLayout {

    private String shapeType = "pokemon";
    private Paint redPaint, whitePaint, blackPaint, buttonCenterPaint, strokePaint;
    private RectF ballBounds;

    public HexagonView(Context context) {
        super(context);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        ballBounds = new RectF();

        // Rojo Pokébola (Mitad superior)
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(Color.parseColor("#EE1515"));

        // Blanco Cyber (Mitad inferior)
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(Color.parseColor("#F0F0F0"));

        // Franja y Borde Negro
        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.parseColor("#222224"));

        // Botón central neón
        buttonCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonCenterPaint.setStyle(Paint.Style.FILL);
        buttonCenterPaint.setColor(Color.parseColor("#00E5FF"));

        // Borde Neón Blanco
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(4f);
        strokePaint.setColor(Color.parseColor("#FFFFFF"));
    }

    public void setShapeType(String shapeType) {
        this.shapeType = shapeType != null ? shapeType.toLowerCase() : "pokemon";
        invalidate();
    }

    public void setHexagonColor(int color) {
        if (!"pokeball".equals(shapeType)) {
            redPaint.setColor(color);
        }
        invalidate();
    }

    public void setStrokeColor(int color) {
        strokePaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();

        if ("pokeball".equals(shapeType)) {
            float radius = (Math.min(w, h) / 2f) - 8f;
            float cx = w / 2f;
            float cy = h / 2f;

            ballBounds.set(cx - radius, cy - radius, cx + radius, cy + radius);

            // 1. Mitad superior (Rojo)
            canvas.drawArc(ballBounds, 180, 180, true, redPaint);

            // 2. Mitad inferior (Blanco)
            canvas.drawArc(ballBounds, 0, 180, true, whitePaint);

            // 3. Franja negra central
            float bandHeight = radius * 0.22f;
            canvas.drawRect(cx - radius, cy - (bandHeight / 2f), cx + radius, cy + (bandHeight / 2f), blackPaint);

            // 4. Círculo exterior e interior del botón central
            canvas.drawCircle(cx, cy, radius * 0.32f, blackPaint);
            canvas.drawCircle(cx, cy, radius * 0.22f, whitePaint);
            canvas.drawCircle(cx, cy, radius * 0.12f, buttonCenterPaint);

            // 5. Borde neón exterior
            canvas.drawCircle(cx, cy, radius, strokePaint);
        }

        super.onDraw(canvas);
    }
}