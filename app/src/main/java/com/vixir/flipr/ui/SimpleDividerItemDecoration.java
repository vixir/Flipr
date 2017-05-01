package com.vixir.flipr.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Rect viewRect = new Rect();
    private final Paint stroke = new Paint();
    private final int padding;
    /**
     * @param parentPadding should be 1/2 the padding desired between views, as views will be surrounded by the
     *                      passed padding, causing it to double up on all edges.  This is also used to determine
     *                      where to draw a fake padding border to hide our trickery.
     */
    SimpleDividerItemDecoration(final int parentPadding, final int backgroundColor) {
        this.padding = parentPadding;
        stroke.setStrokeWidth(parentPadding * 2);
        stroke.setColor(backgroundColor);
        stroke.setStyle(Paint.Style.STROKE);
    }
    @Override
    public void getItemOffsets(
            final Rect outRect,
            final View view,
            final RecyclerView parent,
            final RecyclerView.State state) {
        // pad the views so the background shows through
        outRect.set(padding, padding, padding, padding);
    }
    @Override
    public void onDrawOver(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
        // draw a solid border around the canvas which ends up looking like "normal" recyclerview padding, clipping
        // the children as they scroll past.
        viewRect.set(0, 0, c.getWidth(), c.getHeight());
        viewRect.inset(padding, padding);
        c.drawRect(viewRect, stroke);
    }

}