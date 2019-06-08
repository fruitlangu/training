/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This is the ItemDecoration class used for the Grid list.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Grid Span count
     */
    private int spanCount;

    /**
     * Grid Spacing
     */
    private int spacing;

    /**
     * Grid Edge inclusion
     */
    private boolean includeEdge;

    /**
     * Header Count
     */
    private int headerNum;

    /**
     * Constructor to set the Grid Layout
     *
     * @param spanCount   Span Count
     * @param spacing     Spacing
     * @param includeEdge True to include edge else False
     * @param headerNum   Header Number
     */
    public GridSpaceItemDecoration(int spanCount, int spacing, boolean includeEdge, int headerNum) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.headerNum = headerNum;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        int position = parent.getChildAdapterPosition(view) - headerNum; // item position

        if (position >= 0) {
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        } else {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
