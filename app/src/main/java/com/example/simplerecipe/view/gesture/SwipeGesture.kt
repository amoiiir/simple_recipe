package com.example.simplerecipe.view.gesture

import android.graphics.Canvas
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.roundToInt

abstract class SwipeGesture: ItemTouchHelper(object : SimpleCallback(0, ItemTouchHelper.LEFT) {

    val displayMetrics: DisplayMetrics = DisplayMetrics()
    val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //background color based on swipe
        when {
//            abs(d)
        }



        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), DisplayMetrics()
        ).roundToInt()

})