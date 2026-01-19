package cl.baldomeronapoli.kmm.logger.writer

import cl.baldomeronapoli.kmm.logger.config.LogLevel

/**
 * Implementación iOS del writer de Crashlytics.
 * TODO: Implementar integración con Firebase Crashlytics iOS SDK.
 */
actual class CrashlyticsLogWriter actual constructor(
    crashlyticsInstance: Any?,
    isEnabled: Boolean,
    private val minLevel: LogLevel
) : LogWriter {

    actual override val isEnabled: Boolean = isEnabled

    init {
        if (isEnabled) {
            println("⚠️ CrashlyticsLogWriter iOS: Implementación pendiente. Los logs no se enviarán a Crashlytics.")
        }
    }

    actual override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        if (!isEnabled) return
        if (!level.shouldLog(minLevel)) return

        // TODO: Implementar cuando se agregue Firebase iOS SDK
        // Por ahora, solo log a consola
        println("📱 iOS Crashlytics [${level.name}][$tag]: $message")
        throwable?.let {
            println("   Exception: ${it.message}")
            it.printStackTrace()
        }
    }
}
