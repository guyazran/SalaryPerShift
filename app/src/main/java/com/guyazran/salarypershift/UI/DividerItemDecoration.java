package com.guyazran.salarypershift.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guyazran.salarypershift.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DividerItemDecoration(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider, context.getTheme());
        } else {
            mDivider =  context.getResources().getDrawable(R.drawable.line_divider);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int parentPaddingLeft = parent.getPaddingLeft();
        int parentPaddingRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(parentPaddingLeft, top, parentPaddingRight, bottom);
            mDivider.draw(c);
        }
    }
}