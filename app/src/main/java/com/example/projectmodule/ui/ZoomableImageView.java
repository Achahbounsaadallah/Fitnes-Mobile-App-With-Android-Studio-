package com.example.projectmodule.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private static final float MAX_SCALE_MULTIPLIER = 4f;

    private final Matrix matrix = new Matrix();
    private final PointF last = new PointF();
    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    private float minScale = 1f;
    private float maxScale = 4f;
    private float currentScale = 1f;
    private int viewWidth;
    private int viewHeight;
    private boolean readyToDraw;

    public ZoomableImageView(Context context) {
        this(context, null);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);

        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
        setImageMatrix(matrix);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        readyToDraw = true;
        fitImageToView();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        fitImageToView();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        fitImageToView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                last.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scaleDetector.isInProgress() && currentScale > minScale) {
                    float dx = event.getX() - last.x;
                    float dy = event.getY() - last.y;
                    matrix.postTranslate(dx, dy);
                    fixTranslation();
                    setImageMatrix(matrix);
                    last.set(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void fitImageToView() {
        Drawable drawable = getDrawable();
        if (!readyToDraw || drawable == null || viewWidth == 0 || viewHeight == 0) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return;
        }

        matrix.reset();
        float scale = Math.min((float) viewWidth / drawableWidth, (float) viewHeight / drawableHeight);
        float dx = (viewWidth - drawableWidth * scale) / 2f;
        float dy = (viewHeight - drawableHeight * scale) / 2f;

        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);

        minScale = scale;
        maxScale = minScale * MAX_SCALE_MULTIPLIER;
        currentScale = minScale;
        setImageMatrix(matrix);
    }

    private void fixTranslation() {
        RectF rect = getTransformedRect();
        if (rect == null) {
            return;
        }

        float deltaX = 0f;
        float deltaY = 0f;

        if (rect.width() <= viewWidth) {
            deltaX = (viewWidth - rect.width()) / 2f - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }

        if (rect.height() <= viewHeight) {
            deltaY = (viewHeight - rect.height()) / 2f - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }

        matrix.postTranslate(deltaX, deltaY);
    }

    private RectF getTransformedRect() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        RectF rect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(rect);
        return rect;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float targetScale = currentScale * scaleFactor;

            if (targetScale < minScale) {
                scaleFactor = minScale / currentScale;
                currentScale = minScale;
            } else if (targetScale > maxScale) {
                scaleFactor = maxScale / currentScale;
                currentScale = maxScale;
            } else {
                currentScale = targetScale;
            }

            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            fixTranslation();
            setImageMatrix(matrix);
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            if (currentScale > minScale) {
                fitImageToView();
            } else {
                float scaleFactor = Math.min(2f, maxScale / minScale);
                matrix.postScale(scaleFactor, scaleFactor, e.getX(), e.getY());
                currentScale = minScale * scaleFactor;
                fixTranslation();
                setImageMatrix(matrix);
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            performClick();
            return true;
        }
    }
}

