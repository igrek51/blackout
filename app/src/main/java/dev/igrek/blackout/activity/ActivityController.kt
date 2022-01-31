package dev.igrek.blackout.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import dev.igrek.blackout.info.logger.LoggerFactory
import dev.igrek.blackout.inject.LazyExtractor
import dev.igrek.blackout.inject.LazyInject
import dev.igrek.blackout.inject.appFactory
import dev.igrek.blackout.system.WindowManagerService

class ActivityController(
        windowManagerService: LazyInject<WindowManagerService> = appFactory.windowManagerService,
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val windowManagerService by LazyExtractor(windowManagerService)
    private val activity by LazyExtractor(activity)

    private val logger = LoggerFactory.logger
    var initialized = false

    fun onConfigurationChanged(newConfig: Configuration) {
        // resize event
        val screenWidthDp = newConfig.screenWidthDp
        val screenHeightDp = newConfig.screenHeightDp
        val orientationName = getOrientationName(newConfig.orientation)
        logger.debug("Screen resized: " + screenWidthDp + "dp x " + screenHeightDp + "dp - " + orientationName)
    }

    private fun getOrientationName(orientation: Int): String {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "landscape"
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait"
        }
        return orientation.toString()
    }

    fun quit() {
        windowManagerService.keepScreenOn(false)
        activity.finish()
    }

    fun onStart() {
        if (initialized) {
            logger.debug("starting activity...")
        }
    }

    fun onStop() {
        if (initialized) {
            logger.debug("stopping activity...")
        }
    }

    fun onDestroy() {
        if (initialized) {
            logger.info("activity has been destroyed")
        }
    }

    fun minimize() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(startMain)
    }

}
