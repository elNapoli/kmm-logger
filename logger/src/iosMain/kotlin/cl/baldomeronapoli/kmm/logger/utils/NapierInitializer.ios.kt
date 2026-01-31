package cl.baldomeronapoli.kmm.logger.utils

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import platform.Foundation.NSLog

/**
 * Implementación iOS del Antilog.
 * Usa NSLog para que los logs aparezcan en la consola de Xcode.
 */
actual fun createNapierAntilog(): Antilog = object : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        val logMessage = "${priority.name} [$tag]: $message"
        NSLog(logMessage)
        throwable?.let { NSLog(it.stackTraceToString()) }
    }
}