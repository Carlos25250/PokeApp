package com.example.pokeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
<<<<<<< HEAD
import android.graphics.Path;
import android.graphics.Shader;
=======
import android.graphics.RectF;
>>>>>>> baba7b1b5bd0a80031588d106547933e3736fe2b
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HexagonView extends FrameLayout {

<<<<<<< HEAD
public class HexagonView extends LinearLayout {

    private final Path hexagonPath = new Path();
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int hexagonColor = Color.parseColor("#E63946");
=======
    private String shapeType = "pokemon";
    private Paint redPaint, whitePaint, blackPaint, buttonCenterPaint, strokePaint;
    private RectF ballBounds;
>>>>>>> baba7b1b5bd0a80031588d106547933e3736fe2b

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

<<<<<<< HEAD
        fillPaint.setStyle(Paint.Style.FILL);
=======
        // Rojo Pokébola (Mitad superior)
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setStyle(Paint.Style.FILL);
        redPaint.setColor(Color.parseColor("#EE1515"));
>>>>>>> baba7b1b5bd0a80031588d106547933e3736fe2b

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
<<<<<<< HEAD
        strokePaint.setColor(Color.parseColor("#99FFFFFF"));
        strokePaint.setStrokeWidth(4.5f);
    }

    public void setHexagonColor(int color) {
        this.hexagonColor = color;
=======
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
>>>>>>> baba7b1b5bd0a80031588d106547933e3736fe2b
        invalidate();
    }

    @Override
<<<<<<< HEAD
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
=======
>>>>>>> baba7b1b5bd0a80031588d106547933e3736fe2b
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();

        if ("pokeball".equals(shapeType)) {
            float radius = Math.min(w, h) / 2f - 8f;
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