package dev.igrek.blackout.canvas

import android.content.Context
import android.view.MotionEvent
import android.view.View


class Canvas(
        context: Context,
) : BaseCanvasView(context), View.OnTouchListener {

    override fun onRepaint() {
        drawBackground()
    }

    private fun drawBackground() {
        val backgroundColor = 0x000000
        setColor(backgroundColor)
        clearScreen()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return false
    }
}
