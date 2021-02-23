package com.example.huabei_competition.widget;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


import androidx.annotation.Nullable;


import java.util.List;

/**
 * Create by FanChenYang at 2021/1/8
 * <p>
 * 自定义饼状图，在设置显示内容是 显示半径的大小
 * 显示的内容 由List<Content> 集合提供
 * <p>
 */
public class CakeShapeView extends View {
    // 半径
    private int radius;
    private List<Content> contents;
    // 扇形图画笔
    private Paint sectorPaint;
    // 轮廓线画笔
    private Paint outerLinePaint;
    // 指示线画笔
    private Paint guideLinePaint;
    // 说明文字画笔
    private Paint guideTextPaint;

    private float[] mSweep;
    private static final float LINE_WIDTH = 4f;
    private static final int PAINT_COLOR = 0xed3535;
    private static final float OUTER_LINE_WIDTH = 5f;
    private static final float TEXT_SIZE = 30;
    private static final float TEXT_WIDTH = 40;
    private static final int DURATION_TIME = 2000;
    // 开始绘制的角度
    private static final float START_DEGREE = 0;
    private RectF rectF;

    public CakeShapeView(Context context) {
        this(context, null);
    }

    public CakeShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CakeShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private static final String TAG = "CakeShapeView";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        int x = getMeasuredWidth() / 2, y = getMeasuredHeight() / 2;
        rectF.left = x - radius;
        rectF.right = x + radius;
        rectF.top = y - radius;
        rectF.bottom = y + radius;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSector(canvas);
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化扇形画笔
        sectorPaint = new Paint();
        sectorPaint.setStyle(Paint.Style.FILL);
        sectorPaint.setAntiAlias(true);
        // 初始化轮廓线画笔
        outerLinePaint = new Paint();
        outerLinePaint.setAntiAlias(true);
        outerLinePaint.setStyle(Paint.Style.STROKE);
        outerLinePaint.setStrokeWidth(OUTER_LINE_WIDTH);
        outerLinePaint.setColor(Color.WHITE);
        rectF = new RectF();
        // 初始化指示线画笔
        guideLinePaint = new Paint();
        guideLinePaint.setAntiAlias(true);
        guideLinePaint.setStrokeWidth(LINE_WIDTH);
        // 初始化指示文字画笔
        guideTextPaint = new Paint();
        guideTextPaint.setAntiAlias(true);
        guideTextPaint.setStrokeWidth(TEXT_WIDTH);
        guideTextPaint.setTextSize(TEXT_SIZE);
    }

    /**
     * @param canvas 画布
     *
     *               <p>
     *               画扇形
     */
    private void initSector(Canvas canvas) {
        if (contents == null || contents.size() == 0)
            return;
        int size = contents.size();
        float sectorStart = START_DEGREE;
        if (mSweep == null)
            mSweep = new float[size];
        for (Content content : contents) {
            sectorPaint.setColor(content.getColor());
            canvas.drawArc(rectF, sectorStart, mSweep[contents.indexOf(content)], true, sectorPaint);
            canvas.drawArc(rectF, sectorStart, mSweep[contents.indexOf(content)], true, outerLinePaint);
            initGuide(canvas, sectorStart, mSweep[contents.indexOf(content)], content.getColor(), content.getDes());
            sectorStart += mSweep[contents.indexOf(content)];
        }

    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * @param contents 显示的数据
     */
    public void setData(List<Content> contents, int radius) {
        this.radius = radius;
        this.contents = contents;

        float[] ultimate = new float[contents.size()];
        for (int i = 0; i < contents.size(); i++) {
            ultimate[i] = contents.get(i).getSize() * 3.6f;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MyEvaluator(), new float[contents.size()], ultimate);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mSweep == null)
                    mSweep = new float[contents.size()];
                mSweep = (float[]) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();

    }


    /**
     * 画指示线与文字
     */
    private void initGuide(Canvas canvas, float startAngle, float angle, int color, String text) {
        float endX, endY;
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;
        guideLinePaint.setColor(color);
        guideTextPaint.setColor(color);
        float cos = (float) Math.cos((startAngle + angle / 2) / 180 * Math.PI);
        float sin = (float) Math.sin((startAngle + angle / 2) / 180 * Math.PI);
        endX = (radius + dipToPx(10)) * cos;
        endY = (radius + dipToPx(10)) * sin;
        canvas.drawLine(centerX + (radius - dipToPx(10)) * cos, centerY + (radius - dipToPx(10)) * sin, endX + centerX, endY + centerY, guideLinePaint);
        Rect rect = new Rect();
        guideTextPaint.getTextBounds(text, 0, text.length(), rect);
        float height = rect.height();
        float width = rect.width();
        // 在左侧
        if (endX > 0) {
            canvas.drawLine(centerX + endX, centerY + endY, centerX + endX + dipToPx(15), centerY + endY, guideLinePaint);
            canvas.drawText(text, 0, text.length(), centerX + endX + dipToPx(20), centerY + endY + height / 2, guideTextPaint);
        } else {
            canvas.drawLine(centerX + endX, centerY + endY, centerX + endX - dipToPx(15), centerY + endY, guideLinePaint);
            canvas.drawText(text, 0, text.length(), centerX + endX - dipToPx(20) - width, centerY + endY + height / 2, guideTextPaint);
        }
    }

    final float density = getResources().getDisplayMetrics().density;

    /**
     * @param dipValue dp值
     * @return 对应像素值
     */
    private int dipToPx(float dipValue) {
        return (int) (density * dipValue);
    }

    public static class Content {
        // 文字描述
        private String des;
        // 百分比 所有项目加起来为100
        private int size;
        // 饼状图颜色
        private int color;

        public Content(String des, int size, int color) {
            this.des = des;
            this.size = size;
            this.color = color;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    // 传入的数据大小相加总共是多少
    private static final float PIE_ANIMATION_VALUE = 100;

    private class CustomerAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mSweep = new float[contents.size()];
            for (int i = 0; i < mSweep.length; i++) {
                mSweep[i] = interpolatedTime * contents.get(i).getSize() / PIE_ANIMATION_VALUE * 360f;
            }
            invalidate();
        }
    }

    private class MyEvaluator implements TypeEvaluator<float[]> {
        @Override
        public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
            mSweep = new float[contents.size()];
            for (int i = 0; i < mSweep.length; i++) {
                mSweep[i] = endValue[i] * fraction;
            }
            return mSweep;
        }
    }

}
