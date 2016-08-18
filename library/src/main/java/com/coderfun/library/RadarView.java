package com.coderfun.library;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * a radar view with random bitmap shown
 *
 * @author lls
 * @since 16/8/11 下午2:17
 */
public class RadarView extends View {
    public static final String TAG = "RadarView";
    /** 设计稿-虚线半径 */
    public static final int DEFAULT_TOTAL_RADIUS = 650;
    /** 设计稿-内环半径 */
    public static final int DEFAULT_INNER_RADIUS = 54;
    /** 设计稿-中心裁剪圆半径 */
    public static final int DEFAULT_CLIP_RADIUS = 32;
    /** 设计稿雷达半径 */
    public static final int DEFAULT_RADAR_RADIUS = 600;
    public static final int DEFAULT_BITMAP_RADIUS = 80;
    public static final int DEFAULT_BITMAP_DISTRIBUTION_RADIUS = 360;
    public static final int DEFAULT_BITMAP_SHOW_COUNT = 3;
    public static final int DEFAULT_BITMAP_TOTAL_COUNT = 5;
    public static final int DEFAULT_DURATION = 3000;
    private static final int DEFAULT_RADAR_LINE_WIDTH = 4;
    private static final int DEFAULT_RADAR_RADIUS_LINE_WIDTH = 8;
    ValueAnimator valueAnimator;
    int currentStartIndex = 0;
    Random random = new Random();
    private List<Bitmap> totalBitmaps;
    private Paint paint;
    private float centerX;
    private float centerY;
    private float innerRadius;
    private float radarRadius;
    private float clipRadius;
    private float bitmapDistributionRadius;
    private int bitmapRadius;
    private float radarLineWidth;
    private float radarRadiusLineWidth;
    private Shader radarShader;
    private Path clipPath;
    private Rect bitmapResRect;
    private Rect bitmapDstRect;
    private int duration = DEFAULT_DURATION;
    private int currentRotation;
    private int bitmapShowCount;
    private int bitmapTotalCount;
    private double angle;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        totalBitmaps = new ArrayList<>();
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setColor(Color.WHITE);
        clipPath = new Path();
        bitmapResRect = new Rect();
        bitmapDstRect = new Rect();
        bitmapShowCount = DEFAULT_BITMAP_SHOW_COUNT;
        bitmapTotalCount = DEFAULT_BITMAP_TOTAL_COUNT;
        angle = Math.PI * 2f / bitmapTotalCount;

        valueAnimator = ValueAnimator.ofInt(0, 360);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentRotation = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float widthMinusPadding = w - getPaddingLeft() - getPaddingRight();
        float heightMinusPadding = h - getPaddingTop() - getPaddingBottom();
        centerX = w / 2;
        centerY = h / 2;
        float radius = widthMinusPadding > heightMinusPadding ? heightMinusPadding / 2 : widthMinusPadding / 2;
        float ratio = radius / DEFAULT_TOTAL_RADIUS;
        innerRadius = ratio * DEFAULT_INNER_RADIUS;
        radarRadius = ratio * DEFAULT_RADAR_RADIUS;
        clipRadius = ratio * DEFAULT_CLIP_RADIUS;
        radarLineWidth = ratio * DEFAULT_RADAR_LINE_WIDTH;
        radarRadiusLineWidth = ratio * DEFAULT_RADAR_RADIUS_LINE_WIDTH;
        bitmapDistributionRadius = ratio * DEFAULT_BITMAP_DISTRIBUTION_RADIUS;
        bitmapRadius = (int) (ratio * DEFAULT_BITMAP_RADIUS);
        radarShader = new SweepGradient(centerX, centerY, new int[]{0x00ffffff, 0x00ffffff, 0xffffffff}, new float[]{0,
                0.0f, 1});
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmaps(canvas);
        drawRadar(canvas);
        drawForeground(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            if (heightMode != MeasureSpec.UNSPECIFIED) {
                widthSize = Math.min(screenWidth, heightSize);
            } else {
                widthSize = screenWidth;
            }
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (heightMode != MeasureSpec.UNSPECIFIED) {
                widthSize = Math.min(widthSize,Math.min(screenWidth, heightSize));
            } else {
                widthSize = Math.min(screenWidth, widthSize);
            }
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = widthSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = Math.min(heightSize, widthSize);
        }
        setMeasuredDimension(widthSize, heightSize);

    }

    private void drawBitmaps(Canvas canvas) {
        if (totalBitmaps.isEmpty()) {
            return;
        }
        //起始角,显示图片范围的结束点,显示图片范围=这个角到线的角度
        double startAngle = currentRotation / 360f * (Math.PI * 2f) + (this.bitmapTotalCount - bitmapShowCount) * angle;

        int startIndex = 0;
        while (startAngle > startIndex * angle) {
            startIndex++;
        }
        if (currentStartIndex != startIndex) {
            int randomIndex = random.nextInt(totalBitmaps.size() - bitmapTotalCount) + bitmapTotalCount;
            Collections.swap(totalBitmaps, (currentStartIndex + bitmapShowCount) % bitmapTotalCount, randomIndex);
            currentStartIndex = startIndex;
        }


        for (int i = 0; i < bitmapShowCount; i++) {
            double cAngle = (startIndex + i) * angle;
            Bitmap bitmap = totalBitmaps.get((startIndex + i) % bitmapTotalCount);
            bitmapResRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            int x = (int) (centerX + bitmapDistributionRadius * Math.cos(cAngle));
            int y = (int) (centerY + bitmapDistributionRadius * Math.sin(cAngle));
            bitmapDstRect.set(x - bitmapRadius, y - bitmapRadius, x + bitmapRadius, y + bitmapRadius);
            paint.setAlpha((int) (255 * Math.sin((cAngle - startAngle) * bitmapTotalCount / (2 * bitmapTotalCount - 4))));
            canvas.drawBitmap(bitmap, bitmapResRect, bitmapDstRect, paint);
        }
        paint.setAlpha(255);
    }

    private void drawForeground(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(innerRadius - clipRadius);
        canvas.drawCircle(centerX, centerY, (innerRadius + clipRadius) / 2, paint);
    }

    private void drawRadar(Canvas canvas) {
        canvas.save();
        clipPath.reset();
        clipPath.addCircle(centerX, centerY, innerRadius, Path.Direction.CW);
        canvas.clipPath(clipPath, Region.Op.DIFFERENCE);
        paint.setShader(radarShader);
        canvas.rotate(currentRotation, centerX, centerY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(128);
        canvas.drawCircle(centerX, centerY, radarRadius, paint);
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(radarLineWidth);
        canvas.drawCircle(centerX, centerY, radarRadius, paint);
        paint.setShader(null);

        paint.setStrokeWidth(radarRadiusLineWidth);
        canvas.drawLine(centerX, centerY, centerX + radarRadius, centerY, paint);

        canvas.restore();
    }

    public void startRadarAnimation() {
        valueAnimator.start();
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        if (bitmaps.size() > bitmapTotalCount) {
            this.totalBitmaps = bitmaps;
        } else {
            totalBitmaps.clear();
            //need at least one more bitmap to random
            for (int i = 0; i < bitmapTotalCount + 1; i++) {
                this.totalBitmaps.add(bitmaps.get(i % bitmaps.size()));
            }
        }
    }
}
