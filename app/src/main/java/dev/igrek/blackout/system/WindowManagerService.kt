package dev.igrek.blackout.system


import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.igrek.blackout.inject.LazyExtractor
import dev.igrek.blackout.inject.LazyInject
import dev.igrek.blackout.inject.appFactory

class WindowManagerService(
        appCompatActivity: LazyInject<AppCompatActivity> = appFactory.appCompatActivity,
) {
    private val activity by LazyExtractor(appCompatActivity)

    private val dpi: Int
        get() {
            val metrics = activity.resources.displayMetrics
            return metrics.densityDpi
        }

    fun keepScreenOn(set: Boolean) {
        if (set) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        setShowWhenLocked(set)
    }

    fun hideTaskbar() {
        activity.supportActionBar?.hide()
    }

    fun setFullscreen(set: Boolean) {
        val flag = WindowManager.LayoutParams.FLAG_FULLSCREEN
        if (set) {
            activity.window.addFlags(flag)
        } else {
            activity.window.clearFlags(flag)
        }
    }

    private fun setShowWhenLocked(set: Boolean) {
        val flag = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        if (set) {
            activity.window.setFlags(flag, flag)
        } else {
            activity.window.clearFlags(flag)
        }
    }

    fun dp2px(dp: Float): Float {
        return dp * (dpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun showAppWhenLocked() {
        activity.window
                .addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    }

    fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(activity.window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}
