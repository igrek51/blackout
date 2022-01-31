package dev.igrek.blackout.activity

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import dev.igrek.blackout.info.logger.Logger
import dev.igrek.blackout.info.logger.LoggerFactory
import dev.igrek.blackout.inject.AppContextFactory
import dev.igrek.blackout.inject.LazyExtractor
import dev.igrek.blackout.inject.LazyInject
import dev.igrek.blackout.inject.appFactory


open class MainActivity(
        mainActivityData: LazyInject<MainActivityData> = appFactory.activityData,
) : AppCompatActivity() {
    private var activityData by LazyExtractor(mainActivityData)

    private val logger: Logger = LoggerFactory.logger

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            logger.info("Creating Dependencies container...")
            AppContextFactory.createAppContext(this)
            recreateFields() // Workaround for reusing finished activities by Android
            super.onCreate(savedInstanceState)
            activityData.appInitializer.init()
        } catch (t: Throwable) {
            logger.fatal(t)
            throw t
        }
    }

    private fun recreateFields() {
        activityData = appFactory.activityData.get()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        activityData.activityController.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityData.activityController.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).post {
            activityData.activityController.onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        activityData.activityController.onStop()
    }
}
