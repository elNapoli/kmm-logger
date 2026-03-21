package cl.baldomeronapoli.logger.utils

import cl.baldomeronapoli.logger.domain.model.LogLevel
import platform.Foundation.NSLog

/**
 * Implementación iOS del logger nativo.
 * Usa NSLog directamente para que aparezca en la consola de Xcode.
 */
actual object NativeLogger {
    actual fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val levelStr = when (level) {
            LogLevel.VERBOSE -> "VERBOSE"
            LogLevel.DEBUG -> "DEBUG"
            LogLevel.INFO -> "INFO"
            LogLevel.WARN -> "WARN"
            LogLevel.ERROR -> "ERROR"
            LogLevel.FATAL -> "FATAL"
        }

        val logMessage = "$levelStr [$tag]: $message"
        NSLog(logMessage)

        // Si hay una excepción, loguear el stacktrace
        throwable?.let {
            NSLog("Exception: ${it.message}")
            NSLog(it.stackTraceToString())
        }
    }
}
