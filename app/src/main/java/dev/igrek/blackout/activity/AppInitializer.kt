package dev.igrek.blackout.activity


import android.app.Activity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import dev.igrek.blackout.R
import dev.igrek.blackout.canvas.Canvas
import dev.igrek.blackout.info.logger.LoggerFactory
import dev.igrek.blackout.inject.LazyExtractor
import dev.igrek.blackout.inject.LazyInject
import dev.igrek.blackout.inject.appFactory
import dev.igrek.blackout.system.WindowManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppInitializer(
    windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
    activityController: LazyInject<ActivityController> = appFactory.activityController,
    activity: LazyInject<Activity> = appFactory.activity,
) {
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val activityController by LazyExtractor(activityController)
    private val activity by LazyExtractor(activity)

    private val logger = LoggerFactory.logger

    fun init() {
        logger.info("Initializing application...")

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                windowManagerService.hideTaskbar()

                try {
                    initScreen()
                } catch (t: Throwable) {
                    handleError(t)
                }

                activityController.initialized = true
            }

            logger.info("Application has been initialized.")
        }
    }

    private fun initScreen() {
        activity.setContentView(R.layout.main_layout)

        windowManagerService.keepScreenOn(true)

        val canvas = Canvas(activity).apply {
            reset()
        }
        val canvasContainer = activity.findViewById<ViewGroup>(R.id.canvasContainer)
        canvasContainer.addView(canvas)

        windowManagerService.setFullscreen(true)

        windowManagerService.hideSystemBars()

        // Settings.System.putInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)

        // dim brightness
        val windowAttr: WindowManager.LayoutParams = activity.window.attributes
        windowAttr.screenBrightness = 0f
        activity.window.attributes = windowAttr
    }

    private fun handleError(t: Throwable) {
        LoggerFactory.logger.error(t)
        val err: String = when {
            t.message != null -> t.message
            else -> t::class.simpleName
        }.orEmpty()
        showToast(err)
    }

    private fun showToast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(activity.applicationContext, message, Toast.LENGTH_LONG).show()
        }
        logger.debug("UI: toast: $message")
    }

}
