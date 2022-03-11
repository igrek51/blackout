package dev.igrek.blackout.activity


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
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

        try {
            checkSystemWritePermission()
        } catch (e: Throwable) {
            Toast.makeText(activity, "Error: $e", Toast.LENGTH_LONG).show()
        }

        Handler().postDelayed(this::dimScreen, 1000)
    }

    private fun dimScreen() {
        try{
            val cResolver = activity.contentResolver
            val window = activity.window

            // To handle the auto
            Settings.System.putInt(
                    cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            // Get the current system brightness
            val brightness = Settings.System.getInt(
                    cResolver, Settings.System.SCREEN_BRIGHTNESS
            )

            // Set the system brightness using the brightness variable value
            Settings.System.putInt(
                    cResolver, Settings.System.SCREEN_BRIGHTNESS, 0
            )
        } catch (e: Throwable) {
            Toast.makeText(activity, "Error: $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkSystemWritePermission(): Boolean {
        var retVal = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(activity)
            if (retVal) {
                Toast.makeText(activity, "System Settings Write allowed", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "System Settings Write not allowed", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.setData(Uri.parse("package:dev.igrek.blackout"));
                activity.startActivity(intent)
            }
        }
        return retVal
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
