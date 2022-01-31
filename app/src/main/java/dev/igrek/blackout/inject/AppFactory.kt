package dev.igrek.blackout.inject

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import dev.igrek.blackout.activity.*
import dev.igrek.blackout.info.logger.Logger
import dev.igrek.blackout.info.logger.LoggerFactory
import dev.igrek.blackout.system.*


class AppFactory(
        activity: AppCompatActivity,
) {
    val activity: LazyInject<Activity> = SingletonInject { activity }
    val appCompatActivity: LazyInject<AppCompatActivity> = SingletonInject { activity }

    val context: LazyInject<Context> = SingletonInject { activity.applicationContext }
    val logger: LazyInject<Logger> = PrototypeInject { LoggerFactory.logger }

    /* Services */
    val activityData = SingletonInject { MainActivityData() }
    val activityController = SingletonInject { ActivityController() }
    val appInitializer = SingletonInject { AppInitializer() }
    val windowManagerService = SingletonInject { WindowManagerService() }
}
