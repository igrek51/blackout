package dev.igrek.blackout.activity

import androidx.appcompat.app.AppCompatActivity
import dev.igrek.blackout.inject.LazyExtractor
import dev.igrek.blackout.inject.LazyInject
import dev.igrek.blackout.inject.appFactory

/*
    Main Activity starter pack
    Workaround for reusing finished activities by Android
 */
class MainActivityData(
        appInitializer: LazyInject<AppInitializer> = appFactory.appInitializer,
        activityController: LazyInject<ActivityController> = appFactory.activityController,
) : AppCompatActivity() {
    val appInitializer by LazyExtractor(appInitializer)
    val activityController by LazyExtractor(activityController)
}