package dev.igrek.blackout.info.logger

object LoggerFactory {

    val CONSOLE_LEVEL = LogLevel.TRACE

    val SHOW_TRACE_DETAILS_LEVEL = LogLevel.FATAL

    const val LOG_TAG = "blackout"

    val logger: Logger
        get() = Logger()

}
