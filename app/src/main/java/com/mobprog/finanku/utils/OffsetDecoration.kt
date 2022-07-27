package com.mobprog.finanku.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetDecoration(
    context: Context,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
    left: Int = 0
) : RecyclerView.ItemDecoration() {

    private val spaceTopPx = top.toPx(context)
    private val spaceRightPx = right.toPx(context)
    private val spaceBottomPx = bottom.toPx(context)
    private val spaceLeftPx = left.toPx(context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        outRect.left = spaceLeftPx
        outRect.right = spaceRightPx
        outRect.bottom = spaceBottomPx
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = spaceTopPx
        }
    }
}