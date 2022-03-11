package dev.igrek.blackout.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message


class DummyBrightnessActivity : Activity() {
    private val DELAYED_MESSAGE = 1

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what === DELAYED_MESSAGE) {
                    finish()
                }
                super.handleMessage(msg)
            }
        }
        val brightnessIntent = this.intent
        val brightness = brightnessIntent.getFloatExtra("brightness value", 0f)
        val lp = window.attributes
        lp.screenBrightness = brightness
        window.attributes = lp
        val message: Message = handler!!.obtainMessage(DELAYED_MESSAGE)
        //this next line is very important, you need to finish your activity with slight delay
        handler!!.sendMessageDelayed(message, 1000)
    }
}
