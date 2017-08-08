package com.a10.infohub.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuchenlong on 2017/6/25.
 */

public class Linerlayout extends ViewGroup {

    private List<List<View>> mListViews = new ArrayList<>();
    private List<Integer> mHeights = new ArrayList<>();

    public Linerlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /*
        测量布局容器
         */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;
        int count = getChildCount();

        // 循环遍历子控件
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // 系统自动测量
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = mlp.leftMargin + mlp.rightMargin + child.getMeasuredWidth();
            int childHeigth = mlp.topMargin + mlp.bottomMargin + child.getMeasuredHeight();
            if (childWidth + lineWidth > sizeWidth) { // 一旦超出容器的宽度，需要换行
                width = Math.max(width, lineWidth);
                height += lineHeight;
                lineWidth = childWidth;
                lineHeight = childHeigth;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeigth);
            }

            if (i == count - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        int measureWidth = modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width;
        int measureHeight = modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height;

        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mListViews.clear();
        mHeights.clear();

        int lineWidth = 0;
        int lineHeight = 0;
        int width = getWidth();
        int count = getChildCount();
        List<View> mList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
            int childHeight = child.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;
            if (childWidth + lineWidth > width) {
                mListViews.add(mList);
                mHeights.add(lineHeight);
                lineWidth = 0;
                lineHeight = childHeight;
                mList = new ArrayList<>();
            }
            mList.add(child);
            lineWidth += childWidth;
            lineHeight = Math.max(childHeight, lineHeight);
        }
        mListViews.add(mList);
        mHeights.add(lineHeight);

        // 排列 每一个子控件的left top right bottom
        int left = 0;
        int top = 0;
        int size = mListViews.size();
        for (int i = 0; i < size; i++) {
            mList = mListViews.get(i);
            lineHeight = mHeights.get(i);
            for (int j = 0; j < mList.size(); j++) {
                View child = mList.get(j);
                MarginLayoutParams mlp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + mlp.leftMargin;
                int tc = top + mlp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
            }
            top += lineHeight;
            left = 0;

        }
    }

}
