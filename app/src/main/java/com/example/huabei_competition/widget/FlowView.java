package com.example.huabei_competition.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanchenyang
 * @date 2020/11/18 11:53
 * <p>
 * 自定义流式布局
 */
public class FlowView extends ViewGroup {
    // 存储每一行的元素
    private List<List<View>> views = new ArrayList<>();
    // 存储每一行的高度
    private List<Integer> maxHeights = new ArrayList<>();

    private boolean isMeasured = false;

    public FlowView(Context context) {
        super(context);
    }

    public FlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private static final String TAG = "FlowView";

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measureWidth = 0, measureHeight = getPaddingBottom() + getPaddingTop();
        // 记录宽和行高的最值
        int maxHeight = 0, maxWidth = 0;

        List<View> lineList = new ArrayList<>();

        // 用来记录当前每行已经使用长度
        int lineCountWidth = 0;

        int childCount = getChildCount();
        if (!isMeasured) {
            isMeasured = true;
        } else {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                // 考虑child的margin计算child的大小
                measureChildWithMargins(childAt, widthMeasureSpec, 0, heightMeasureSpec, 0);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childAt.getLayoutParams();
                int childWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int childHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                if (childWidth + lineCountWidth > widthSize - getPaddingStart() - getPaddingEnd()) {
                    // 要换行
                    // 记录行高
                    maxHeights.add(maxHeight);
                    // 累加每一行的高
                    measureHeight += maxHeight;
                    // 记录行宽的最值
                    maxWidth = Math.max(maxWidth, lineCountWidth);
                    // 存储上一行的view
                    views.add(lineList);
                    // 初始化
                    lineList = new ArrayList<>();
                    lineList.add(childAt);
                    maxHeight = childHeight;
                    lineCountWidth = childWidth;
                } else {
                    //不要换行
                    lineCountWidth += childWidth;
                    lineList.add(childAt);
                    maxHeight = Math.max(maxHeight, childHeight);


                }
                if (i == childCount - 1) {
                    maxWidth = Math.max(maxWidth, lineCountWidth);
                    measureHeight += maxHeight;
                    maxHeights.add(maxHeight);
                    views.add(lineList);
                }
            }
        }
        maxWidth += getPaddingStart() + getPaddingEnd();
        int resultWidth = widthMode == MeasureSpec.AT_MOST ? maxWidth : widthSize;
        int resultHeight = heightMode == MeasureSpec.AT_MOST ? measureHeight : heightSize;
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "FlowView " + l + "\n" + t + "\n" + r + "\n" + b);
        System.out.println(maxHeights.size());
        int i = 0;
        int left = 0, top = 0, right = 0, bottom = 0;
        for (List<View> view : views) {
            left = getPaddingLeft();
            for (View view1 : view) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view1.getLayoutParams();
                left += marginLayoutParams.getMarginStart();
                right = left + view1.getMeasuredWidth();
                bottom = top + view1.getMeasuredHeight();
                view1.layout(left, top + marginLayoutParams.topMargin, right, bottom + marginLayoutParams.topMargin);
                Log.d(TAG, "onLayout: " + left + "\n" + top + "\n" + right + "\n" + bottom);
                left = right + marginLayoutParams.rightMargin;
            }
            top += maxHeights.get(i++);
        }
        views.clear();
        maxHeights.clear();
    }

    /**
     * @param attrs xml中参数
     * @return 子view的layoutParams返回为一个MarginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}