package com.alan.floresparatania;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(new LetterView());
    }

    private final class LetterView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random(14);
        private final List<Flower> flowers = new ArrayList<>();
        private float progress = 0f;
        private boolean opened = false;

        LetterView() {
            super(MainActivity.this);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            setContentDescription("Carta para Tania. Toca para abrir.");
            for (int i = 0; i < 34; i++) flowers.add(new Flower());
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w = getWidth(), h = getHeight();
            paint.setShader(new LinearGradient(0, 0, 0, h, Color.rgb(10, 35, 29), Color.rgb(29, 58, 45), Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, paint); paint.setShader(null);

            drawGlow(c, w * .5f, h * .43f, w * .45f);
            if (progress > .05f) drawFlowers(c, w, h);
            drawEnvelope(c, w, h);
            if (progress > .55f) drawMessage(c, w, h);
            else drawClosedText(c, w, h);
        }

        private void drawGlow(Canvas c, float x, float y, float radius) {
            paint.setShader(new android.graphics.RadialGradient(x, y, radius, 0x35F5D66C, 0x00000000, Shader.TileMode.CLAMP));
            c.drawCircle(x, y, radius, paint); paint.setShader(null);
        }

        private void drawClosedText(Canvas c, float w, float h) {
            paint.setColor(0xFFFFF7DE); paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD));
            paint.setTextSize(w * .09f); c.drawText("Para Tania", w / 2, h * .23f, paint);
            paint.setTypeface(android.graphics.Typeface.create("sans", android.graphics.Typeface.NORMAL));
            paint.setTextSize(w * .042f); paint.setColor(0xFFEADFBF);
            c.drawText("Una pequeña sorpresa para ti", w / 2, h * .29f, paint);
            paint.setColor(0xFFE8B945); c.drawRoundRect(w * .27f, h * .72f, w * .73f, h * .79f, 50, 50, paint);
            paint.setColor(0xFF26331F); paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); paint.setTextSize(w * .041f);
            c.drawText("PRESIONA AQUÍ", w / 2, h * .765f, paint);
        }

        private void drawEnvelope(Canvas c, float w, float h) {
            float cx = w / 2, top = h * (.40f - .10f * progress), ew = w * .76f, eh = h * .24f;
            float left = cx - ew / 2, right = cx + ew / 2, bottom = top + eh;
            paint.setShadowLayer(24, 0, 12, 0x88000000); paint.setColor(0xFFF2D489);
            c.drawRoundRect(left, top, right, bottom, 18, 18, paint); paint.clearShadowLayer();
            Path fold = new Path(); fold.moveTo(left, top); fold.lineTo(cx, top + eh * .62f); fold.lineTo(right, top); fold.close();
            paint.setColor(0xFFDCA94C); c.drawPath(fold, paint);
            Path front = new Path(); front.moveTo(left, bottom); front.lineTo(cx, top + eh * .42f); front.lineTo(right, bottom); front.close();
            paint.setColor(0xFFFFE8AA); c.drawPath(front, paint);

            if (progress > 0) {
                float flapLift = eh * .92f * progress;
                Path flap = new Path(); flap.moveTo(left, top); flap.lineTo(cx, top - flapLift); flap.lineTo(right, top); flap.close();
                paint.setColor(0xFFEBC56E); c.drawPath(flap, paint);
            }
            if (progress < .55f) {
                paint.setColor(0xFFC98A24); c.drawCircle(cx, top + eh * .42f, ew * .065f, paint);
                paint.setColor(0xFFFFE59B); paint.setTextSize(ew * .07f); paint.setTextAlign(Paint.Align.CENTER);
                c.drawText("T", cx, top + eh * .45f, paint);
            }
        }

        private void drawMessage(Canvas c, float w, float h) {
            float a = Math.min(1f, (progress - .55f) / .3f);
            paint.setAlpha((int)(255 * a)); paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD));
            paint.setColor(0xFFFFF8E7); paint.setTextSize(w * .076f); c.drawText("Para Tania", w / 2, h * .17f, paint);
            paint.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.ITALIC));
            paint.setTextSize(w * .047f); paint.setColor(0xFFFFF3CE);
            String[] lines = {"Tal vez no pueda dártelas", "en físico, pero te las obsequio", "de esta manera, a la distancia."};
            for (int i = 0; i < lines.length; i++) c.drawText(lines[i], w / 2, h * (.26f + i * .055f), paint);
            paint.setColor(0xFFE9BB43); paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); paint.setTextSize(w * .038f);
            c.drawText("Con cariño, Alan", w / 2, h * .89f, paint); paint.setAlpha(255);
        }

        private void drawFlowers(Canvas c, float w, float h) {
            float bloom = Math.min(1f, progress * 1.35f);
            for (Flower f : flowers) {
                float y = h * (1.08f - bloom * f.travel);
                float x = w * f.x + (float)Math.sin(progress * 7 + f.phase) * w * .025f;
                float scale = f.size * Math.min(1f, progress * 2.2f);
                drawFlower(c, x, y, scale, f.rotation + progress * 40f);
            }
        }

        private void drawFlower(Canvas c, float x, float y, float r, float rotation) {
            c.save(); c.rotate(rotation, x, y);
            paint.setColor(0xFF73A85B); paint.setStrokeWidth(Math.max(2, r * .12f));
            c.drawLine(x, y + r * .6f, x, y + r * 2.1f, paint);
            paint.setColor(0xFFFFD84D);
            for (int i = 0; i < 8; i++) {
                double a = Math.PI * 2 * i / 8;
                c.drawOval(new RectF((float)(x + Math.cos(a) * r * .62 - r * .34), (float)(y + Math.sin(a) * r * .62 - r * .52),
                        (float)(x + Math.cos(a) * r * .62 + r * .34), (float)(y + Math.sin(a) * r * .62 + r * .52)), paint);
            }
            paint.setColor(0xFF9D6A16); c.drawCircle(x, y, r * .38f, paint); c.restore();
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP && !opened) {
                opened = true; performClick();
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(2600); animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(a -> { progress = (float)a.getAnimatedValue(); invalidate(); });
                animator.start(); return true;
            }
            return true;
        }

        @Override public boolean performClick() { super.performClick(); return true; }

        private final class Flower {
            final float x = .04f + random.nextFloat() * .92f;
            final float travel = .35f + random.nextFloat() * .95f;
            final float size = 10f + random.nextFloat() * 16f;
            final float phase = random.nextFloat() * 6.28f;
            final float rotation = random.nextFloat() * 360f;
        }
    }
}
