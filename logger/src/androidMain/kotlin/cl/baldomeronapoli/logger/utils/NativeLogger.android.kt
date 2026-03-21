package cl.baldomeronapoli.logger.utils

import android.util.Log
import cl.baldomeronapoli.logger.domain.model.LogLevel

/**
 * Implementación Android del logger nativo.
 * Usa android.util.Log directamente.
 */
actual object NativeLogger {
    actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val logMessage = message

        when (level) {
            LogLevel.VERBOSE -> {
                if (throwable != null) {
                    Log.v(tag, logMessage, throwable)
                } else {
                    Log.v(tag, logMessage)
                }
            }
            LogLevel.DEBUG -> {
                if (throwable != null) {
                    Log.d(tag, logMessage, throwable)
                } else {
                    Log.d(tag, logMessage)
                }
            }
            LogLevel.INFO -> {
                if (throwable != null) {
                    Log.i(tag, logMessage, throwable)
                } else {
                    Log.i(tag, logMessage)
                }
            }
            LogLevel.WARN -> {
                if (throwable != null) {
                    Log.w(tag, logMessage, throwable)
                } else {
                    Log.w(tag, logMessage)
                }
            }
            LogLevel.ERROR -> {
                if (throwable != null) {
                    Log.e(tag, logMessage, throwable)
                } else {
                    Log.e(tag, logMessage)
                }
            }
            LogLevel.FATAL -> {
                if (throwable != null) {
                    Log.wtf(tag, logMessage, throwable)
                } else {
                    Log.wtf(tag, logMessage)
                }
            }
        }
    }
}
